package ports

import "github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/domain"

type IDGenerator interface {
	Generate() domain.LinkID
}
