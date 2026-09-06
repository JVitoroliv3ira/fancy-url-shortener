import { NgIf } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CreateShortUrlResponse, UrlShortenerService } from './url-shortener.service';

@Component({
  imports: [FormsModule, NgIf],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  url = '';
  expiresAt = '';
  loading = false;
  copied = false;
  errorMessage = '';
  result: CreateShortUrlResponse | null = null;

  constructor(
    private readonly urlShortener: UrlShortenerService,
    private readonly changeDetector: ChangeDetectorRef,
  ) {}

  submit(): void {
    this.errorMessage = '';
    this.copied = false;
    this.result = null;

    const trimmedUrl = this.url.trim();
    if (!trimmedUrl) {
      this.errorMessage = 'Informe uma URL para encurtar.';
      return;
    }

    if (!this.isValidUrl(trimmedUrl)) {
      this.errorMessage = 'Informe uma URL válida, incluindo http:// ou https://.';
      return;
    }

    this.loading = true;
    this.urlShortener.createShortUrl({
      url: trimmedUrl,
      expiresAt: this.expiresAt ? new Date(this.expiresAt).toISOString() : null,
    }).subscribe({
      next: (response) => {
        this.result = response;
        this.loading = false;
        this.changeDetector.detectChanges();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.error?.message || 'Não foi possível criar o link agora. Verifique se a API está rodando e tente novamente.';
        this.loading = false;
        this.changeDetector.detectChanges();
      },
    });
  }

  copyShortUrl(): void {
    if (!this.result?.shortUrl) {
      return;
    }

    navigator.clipboard.writeText(this.result.shortUrl).then(() => {
      this.copied = true;
    });
  }

  formatExpiration(value: string | null): string {
    if (!value) {
      return 'Sem expiração definida';
    }

    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(new Date(value));
  }

  private isValidUrl(value: string): boolean {
    try {
      const parsed = new URL(value);
      return parsed.protocol === 'http:' || parsed.protocol === 'https:';
    } catch {
      return false;
    }
  }
}
