package ports

import (
	"context"

	"github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/domain"
)

type LinkRepository interface {
	Create(ctx context.Context, link domain.Link) error
}
