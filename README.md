# fancy

`fancy` é um encurtador de URLs usado como projeto de estudo sobre backend e sistemas distribuídos.

A proposta não é construir apenas um CRUD de links. O domínio de encurtamento de URLs é simples o suficiente para evoluir em fases, mas rico o bastante para explorar decisões comuns em sistemas distribuídos: cache, persistência orientada a consultas, consistência, tolerância a falhas, processamento assíncrono e observabilidade.

## Ideia Central

Um encurtador de URLs parece simples:

```text
URL longa -> código curto -> redirect
```

Mas o caminho de redirect levanta perguntas importantes:

- Como resolver uma URL com baixa latência?
- Como reduzir pressão sobre o banco de dados?
- Como continuar funcionando se o cache estiver indisponível?
- Como registrar eventos de acesso sem atrasar o usuário?
- Como observar gargalos, falhas e degradações?

O objetivo do `fancy` é usar esse problema pequeno para estudar essas decisões de forma prática.

## Arquitetura Atual

O projeto possui um serviço principal em `services/fancy-url-shortener`, responsável por criar URLs curtas e resolver redirects.

```text
Cliente
  |
  |-- POST /api/v1/urls
  |       |
  |       v
  |   Spring Boot API
  |       |
  |       v
  |   Cassandra
  |
  `-- GET /r/{code}
          |
          v
      Spring Boot API
          |
          |-- Redis Cache
          |
          v
      Cassandra
```

Cassandra é a fonte de verdade. Redis é usado como cache-aside para acelerar o caminho de redirect.

## Fluxos Principais

### Criar URL Curta

```http
POST /api/v1/urls
Content-Type: application/json

{
  "url": "https://example.com",
  "expiresAt": "2027-01-01T00:00:00Z"
}
```

A API gera um código curto, persiste a URL no Cassandra e retorna a URL encurtada.

### Resolver Redirect

```http
GET /r/{code}
```

Fluxo esperado:

```text
1. Buscar o código no Redis.
2. Se encontrar, redirecionar.
3. Se não encontrar, buscar no Cassandra.
4. Se encontrar no Cassandra, popular o cache.
5. Retornar o redirect.
```

O redirect é o caminho crítico do sistema. Ele deve ser simples, rápido e tolerante a falhas sempre que possível.

## Stack

- Java 21
- Spring Boot
- Cassandra
- Redis
- Maven
- Docker
- Testcontainers

## Como Rodar

Pré-requisitos:

- Java 21
- Docker
- Make

Subir infraestrutura local:

```sh
make infra-up
```

Aplicar migrations do Cassandra:

```sh
make cassandra-migrate
```

Rodar a aplicação:

```sh
make boot
```

Rodar os testes:

```sh
make test
```

## Estrutura

```text
services/
`-- fancy-url-shortener/
    |-- src/main/java/.../shortening/
    |-- src/main/java/.../redirecting/
    |-- src/main/java/.../shared/
    `-- src/main/resources/db/cassandra/

scripts/
|-- start-cassandra.sh
|-- apply-cassandra-migrations.sh
`-- start-redis.sh
```

## Status Atual

O projeto está em desenvolvimento inicial, mas já possui uma base funcional para o encurtador.

Já existe:

- Modelagem de URL curta, código curto e URL original.
- Caso de uso para criação de URL curta.
- Endpoint para criação de URLs.
- Persistência inicial em Cassandra.
- Resolução de redirect por código curto.
- Cache-aside com Redis no fluxo de redirect.
- Testes cobrindo domínio, aplicação e partes da infraestrutura.

Ainda está planejado:

- Eventos assíncronos de acesso.
- Worker de analytics.
- Observabilidade com métricas, logs e tracing.
- Testes de carga.
- Cenários de falha e degradação controlada.

## Roadmap

Próximos passos prováveis:

1. Melhorar o fluxo de redirect e tratamento de falhas parciais.
2. Publicar eventos de acesso de forma assíncrona.
3. Implementar processamento de analytics.
4. Adicionar métricas e health checks mais específicos.
5. Criar testes de integração mais próximos do ambiente real.
6. Executar testes de carga e estudar comportamento sob pressão.

## Princípio

O redirect é o caminho crítico.

Funcionalidades secundárias, como analytics e observabilidade, não devem impedir o usuário de chegar à URL original quando o sistema ainda possui informação suficiente para redirecionar.
