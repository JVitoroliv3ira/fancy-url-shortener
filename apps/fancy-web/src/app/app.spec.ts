import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { App } from './app';
import { UrlShortenerService } from './url-shortener.service';

describe('App', () => {
  let fixture: ComponentFixture<App>;
  let component: App;
  let service: { createShortUrl: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    service = { createShortUrl: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [{ provide: UrlShortenerService, useValue: service }],
    }).compileComponents();

    fixture = TestBed.createComponent(App);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('renders the PT-BR title and form', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('fancy');
    expect(compiled.textContent).toContain('Encurte links de forma simples.');
    expect(compiled.querySelector('input[name="url"]')).not.toBeNull();
    expect(compiled.querySelector('input[name="expiresAt"]')).not.toBeNull();
  });

  it('shows an error when URL is empty', () => {
    component.url = '';

    component.submit();

    expect(component.errorMessage).toBe('Informe uma URL para encurtar.');
    expect(service.createShortUrl).not.toHaveBeenCalled();
  });

  it('shows an error when URL is invalid', () => {
    component.url = 'not-a-url';

    component.submit();

    expect(component.errorMessage).toBe('Informe uma URL válida, incluindo http:// ou https://.');
    expect(service.createShortUrl).not.toHaveBeenCalled();
  });

  it('clears a previous result when a later URL is invalid', () => {
    component.result = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: null,
    };
    component.url = 'not-a-url';

    component.submit();

    expect(component.errorMessage).toBe('Informe uma URL válida, incluindo http:// ou https://.');
    expect(component.result).toBeNull();
  });

  it('submits a valid URL with null expiration when empty', () => {
    const response = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: null,
    };
    service.createShortUrl.mockReturnValue(of(response));
    component.url = 'https://example.com';
    component.expiresAt = '';

    component.submit();

    expect(service.createShortUrl).toHaveBeenCalledWith({ url: 'https://example.com', expiresAt: null });
    expect(component.result).toEqual(response);
    expect(component.errorMessage).toBe('');
  });

  it('updates the page after an asynchronous successful request', async () => {
    const response = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: null,
    };
    service.createShortUrl.mockReturnValue(new Observable((subscriber) => {
      setTimeout(() => {
        subscriber.next(response);
        subscriber.complete();
      }, 0);
    }));
    component.url = 'https://example.com';

    component.submit();
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Criando...');

    await new Promise((resolve) => setTimeout(resolve, 0));

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Criar link curto');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Seu link curto');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('http://localhost:8080/r/abc123');
  });

  it('converts local expiration to ISO UTC before submitting', () => {
    service.createShortUrl.mockReturnValue(of({
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: '2027-01-01T03:00:00.000Z',
    }));
    component.url = 'https://example.com';
    component.expiresAt = '2027-01-01T00:00';

    component.submit();

    const request = service.createShortUrl.mock.calls.at(-1)?.[0];
    expect(request.url).toBe('https://example.com');
    expect(request.expiresAt).toBe(new Date('2027-01-01T00:00').toISOString());
  });

  it('shows API error message when available', () => {
    service.createShortUrl.mockReturnValue(throwError(() => ({ error: { message: 'URL inválida' } })));
    component.url = 'https://example.com';

    component.submit();

    expect(component.errorMessage).toBe('URL inválida');
  });

  it('clears a previous result when a later API request fails', () => {
    component.result = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: null,
    };
    service.createShortUrl.mockReturnValue(throwError(() => ({ error: { message: 'URL inválida' } })));
    component.url = 'https://example.com';

    component.submit();

    expect(component.errorMessage).toBe('URL inválida');
    expect(component.result).toBeNull();
  });

  it('shows generic error when API message is unavailable', () => {
    service.createShortUrl.mockReturnValue(throwError(() => ({ status: 0 })));
    component.url = 'https://example.com';

    component.submit();

    expect(component.errorMessage).toBe('Não foi possível criar o link agora. Verifique se a API está rodando e tente novamente.');
  });

  it('copies the generated short URL', async () => {
    component.result = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: null,
    };
    Object.defineProperty(navigator, 'clipboard', {
      configurable: true,
      value: { writeText: vi.fn().mockResolvedValue(undefined) },
    });

    component.copyShortUrl();
    await Promise.resolve();

    expect(navigator.clipboard.writeText).toHaveBeenCalledWith('http://localhost:8080/r/abc123');
    expect(component.copied).toBe(true);
  });
});
