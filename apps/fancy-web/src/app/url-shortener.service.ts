import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface CreateShortUrlRequest {
  url: string;
  expiresAt: string | null;
}

export interface CreateShortUrlResponse {
  code: string;
  shortUrl: string;
  originalUrl: string;
  expiresAt: string | null;
}

export interface ApiErrorResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class UrlShortenerService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/urls';

  constructor(private readonly http: HttpClient) {}

  createShortUrl(request: CreateShortUrlRequest): Observable<CreateShortUrlResponse> {
    return this.http.post<CreateShortUrlResponse>(this.apiUrl, request);
  }
}
