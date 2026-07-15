# fancy

`fancy` é um projeto de estudo sobre sistemas distribuídos usando o domínio de um encurtador de URLs.

A ideia não é construir apenas um CRUD de links, mas usar um problema aparentemente simples para explorar decisões arquiteturais comuns em sistemas distribuídos: cache distribuído, banco orientado a consultas, consistência configurável, processamento assíncrono, tolerância a falhas, observabilidade e testes de carga.

O projeto ainda está no início e deve evoluir por fases.

## Visão Geral

Um encurtador de URLs pode parecer simples:

```text
URL longa -> código curto -> redirect
```

Mas, em um cenário distribuído, surgem várias perguntas relevantes:

- Como resolver redirects com baixa latência?
- Como escalar o serviço horizontalmente?
- Como continuar funcionando se o cache cair?
- Como registrar analytics sem atrasar o usuário?
- Como escolher entre consistência forte e disponibilidade?
- Como modelar dados em Cassandra sem pensar de forma relacional?
- Como observar falhas, gargalos e degradações?

O `fancy` usa esse contexto para estudar essas decisões de forma prática.

## Objetivos

O projeto busca demonstrar:

- Separação entre gerenciamento de links, redirecionamento e analytics.
- Uso de Go para serviços pequenos, explícitos e performáticos.
- Cassandra como fonte de verdade.
- Redis como cache distribuído.
- Redis Streams para eventos assíncronos.
- Consistência configurável conforme o tipo de operação.
- Degradação controlada em caso de falhas parciais.
- Observabilidade com métricas, logs e tracing.
- Testes unitários, integração e carga.

## Arquitetura Planejada

A arquitetura planejada possui três componentes principais:

```text
                ┌─────────────────────┐
                │ Link Management API │
                └──────────┬──────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ Cassandra   │
                    └─────────────┘


Usuário
  │
  │ GET /{code}
  ▼
┌──────────────────┐
│ Redirect Service │
└───────┬──────────┘
        │
        ├── Redis Cache
        │
        ├── Cassandra
        │
        └── Redis Streams
                  │
                  ▼
          ┌──────────────────┐
          │ Analytics Worker │
          └──────────────────┘
```

O ponto mais importante da arquitetura é que o redirecionamento é o caminho crítico.

O `Redirect Service` deve ser simples, stateless, rápido e escalável horizontalmente. Analytics e outras tarefas secundárias não devem bloquear o redirect.

## Componentes

### Link Management API

Responsável pelas operações administrativas dos links.

Responsabilidades planejadas:

- Criar links.
- Consultar links.
- Listar links por usuário e período.
- Atualizar links.
- Ativar e desativar links.
- Expirar links.
- Excluir links.
- Invalidar cache após mudanças.

Esse serviço pode priorizar mais consistência, já que lida com alterações de estado.

### Redirect Service

Responsável por resolver:

```http
GET /{code}
```

E responder com:

```http
302 Found
Location: https://url-original
```

Fluxo planejado:

```text
1. Buscar o código no Redis Cache.
2. Se encontrar, redirecionar.
3. Se não encontrar, buscar no Cassandra.
4. Se encontrar no Cassandra, popular o cache.
5. Publicar um evento de acesso no Redis Streams.
6. Retornar o redirect.
```

A publicação do evento de analytics não deve impedir o redirecionamento.

### Analytics Worker

Responsável por consumir eventos de acesso e persistir dados analíticos.

Responsabilidades planejadas:

- Consumir eventos do Redis Streams.
- Processar eventos em lote.
- Persistir analytics no Cassandra.
- Trabalhar com entrega at-least-once.
- Usar ACK após processamento.
- Recuperar mensagens pendentes.
- Aplicar retry.
- Enviar mensagens problemáticas para uma dead-letter stream.

O worker pode ficar fora do ar sem derrubar o sistema de redirects.

## Cassandra

Cassandra será a fonte de verdade do sistema.

A modelagem será orientada a consultas, e não relacional. Isso significa que as tabelas serão desenhadas a partir das perguntas que o sistema precisa responder.

Tabelas planejadas:

| Tabela | Objetivo |
| --- | --- |
| `links_by_code` | Resolver um código curto para a URL original. |
| `links_by_owner_month` | Listar links de um usuário por mês. |
| `click_events_by_code_day` | Armazenar eventos de clique por código, dia e shard. |

Essa abordagem evita depender de joins e favorece leituras previsíveis em escala.

## Redis

Redis terá dois papéis diferentes no projeto.

### Redis Cache

Usado como cache-aside para links mais acessados.

O cache reduz latência e diminui a pressão sobre o Cassandra. Ele não é fonte de verdade.

Se o cache estiver indisponível, o sistema deve tentar resolver o link diretamente no Cassandra.

### Redis Streams

Usado como fila de eventos de acesso.

O `Redirect Service` publica eventos no stream, e o `Analytics Worker` consome esses eventos de forma assíncrona.

Idealmente, Redis Cache e Redis Streams devem rodar em instâncias separadas, porque possuem necessidades diferentes de memória, persistência, eviction policy e criticidade.

## Consistência

A política inicial planejada é:

| Operação | Consistência |
| --- | --- |
| Criação e atualização de links | `LOCAL_QUORUM` |
| Resolução de redirects | `LOCAL_ONE` |
| Analytics | `LOCAL_ONE` |
| LWT para evitar colisão | `LOCAL_SERIAL` + `LOCAL_QUORUM` |

No redirect, a escolha inicial por `LOCAL_ONE` favorece baixa latência e disponibilidade.

O custo é aceitar uma chance maior de leitura desatualizada em troca de respostas mais rápidas. Em operações administrativas, como criação e atualização, `LOCAL_QUORUM` oferece garantias melhores.

## Geração de Códigos

A estratégia inicial será:

```text
código aleatório Base62 + INSERT IF NOT EXISTS
```

O `INSERT IF NOT EXISTS` usa LWT no Cassandra para evitar colisões.

No futuro, o projeto pode comparar essa abordagem com uma estratégia baseada em ID distribuído + Base62.

## Resiliência

Princípio central do projeto:

> O sistema deve continuar redirecionando sempre que possível, mesmo quando cache, fila ou analytics estiverem degradados.

Comportamentos esperados:

| Falha | Comportamento |
| --- | --- |
| Redis Cache indisponível | Fallback para Cassandra. |
| Redis Streams indisponível | Redirect continua; analytics pode ser perdido. |
| Worker indisponível | Eventos acumulam no stream. |
| Worker cai antes do ACK | Mensagem fica pendente e pode ser recuperada. |
| Cassandra indisponível com cache hit | Redirect pode continuar. |
| Cassandra indisponível com cache miss | Redirect falha temporariamente. |

A prioridade é preservar o caminho crítico sempre que houver informação suficiente para redirecionar.

## Observabilidade

O projeto deve ser instrumentado desde cedo.

Ferramentas planejadas:

- OpenTelemetry.
- Prometheus.
- Grafana.
- Logs estruturados.
- Health checks.
- Readiness checks.

Métricas importantes:

| Métrica | Descrição |
| --- | --- |
| `redirect_requests_total` | Total de redirects. |
| `redirect_duration_seconds` | Latência do redirect. |
| `redirect_cache_hits_total` | Hits no cache. |
| `redirect_cache_misses_total` | Misses no cache. |
| `cassandra_query_duration_seconds` | Latência das consultas ao Cassandra. |
| `redis_stream_pending_messages` | Mensagens pendentes no stream. |
| `analytics_events_processed_total` | Eventos processados pelo worker. |
| `analytics_dlq_messages_total` | Mensagens enviadas para DLQ. |

## Testes

Estratégia planejada:

- Testes unitários para o domínio.
- Testes de integração com Testcontainers.
- Testes envolvendo Cassandra e Redis.
- Testes de carga com k6 ou ferramenta equivalente.
- Testes de falhas e degradação controlada.

O objetivo não é apenas testar se a aplicação funciona, mas entender como ela se comporta sob pressão e falhas parciais.

## Stack Planejada

- Go
- Cassandra
- Redis Cache
- Redis Streams
- Docker Compose
- OpenTelemetry
- Prometheus
- Grafana
- OpenAPI
- Testcontainers
- k6 ou ferramenta equivalente

## Estrutura Planejada

```text
cmd/
├── link-api/
├── redirect-api/
└── analytics-worker/

internal/
├── link/
├── redirect/
├── analytics/
├── adapters/
├── platform/
└── shared/

migrations/
deployments/
tests/
docs/
```

## Status Atual

O projeto está em fase inicial.

Já existe ou está sendo iniciado o domínio de links em Go, incluindo:

- Modelagem de `Link`.
- Modelagem de `LinkID`.
- Modelagem de `LinkStatus`.
- Validação de URL.
- Expiração padrão.
- Caso de uso de criação de link.
- Testes unitários guiando a modelagem.

Funcionalidades distribuídas como Cassandra, Redis, Streams, observabilidade e workers ainda fazem parte da arquitetura planejada.

## Roadmap

Fases previstas:

1. Modelagem do domínio de links.
2. Link Management API.
3. Persistência em Cassandra.
4. Redirect Service.
5. Cache-aside com Redis.
6. Publicação de eventos em Redis Streams.
7. Analytics Worker.
8. Observabilidade com métricas, logs e traces.
9. Testes de integração com Testcontainers.
10. Testes de carga e cenários de falha.

## Princípio Central

`fancy` é um laboratório de arquitetura distribuída usando um encurtador de URLs como domínio.

O foco é entender e demonstrar como decisões de consistência, cache, mensageria, observabilidade e tolerância a falhas afetam um sistema real.

> O sistema deve continuar redirecionando sempre que possível, mesmo quando partes não críticas estiverem degradadas.
