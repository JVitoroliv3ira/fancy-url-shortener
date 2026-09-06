import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { UrlShortenerService } from './url-shortener.service';

describe('UrlShortenerService', () => {
  let service: UrlShortenerService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), UrlShortenerService],
    });

    service = TestBed.inject(UrlShortenerService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('posts the URL creation request to the backend', () => {
    const response = {
      code: 'abc123',
      shortUrl: 'http://localhost:8080/r/abc123',
      originalUrl: 'https://example.com',
      expiresAt: '2027-01-01T00:00:00Z',
    };

    service.createShortUrl({ url: 'https://example.com', expiresAt: '2027-01-01T00:00:00.000Z' })
      .subscribe((result) => expect(result).toEqual(response));

    const request = http.expectOne('http://localhost:8080/api/v1/urls');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      url: 'https://example.com',
      expiresAt: '2027-01-01T00:00:00.000Z',
    });

    request.flush(response);
  });
});
