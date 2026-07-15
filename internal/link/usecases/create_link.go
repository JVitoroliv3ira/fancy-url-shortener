package usecases

import (
	"context"
	"time"

	"github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/domain"
	"github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/ports"
)

type Clock interface {
	Now() time.Time
}

type CreateLinkInput struct {
	TargetURL string
}

type CreateLinkOutput struct {
	ID        domain.LinkID
	Code      string
	ExpiresAt time.Time
}

type CreateLink struct {
	repository    ports.LinkRepository
	codeGenerator ports.CodeGenerator
	idGenerator   ports.IDGenerator
	clock         Clock
}

func (uc CreateLink) Execute(ctx context.Context, input CreateLinkInput) (CreateLinkOutput, error) {
	id := uc.idGenerator.Generate()

	code, err := uc.codeGenerator.Generate(ctx)
	if err != nil {
		return CreateLinkOutput{}, err
	}

	link, err := domain.NewLink(id, code, input.TargetURL, uc.clock.Now())
	if err != nil {
		return CreateLinkOutput{}, err
	}

	if err := uc.repository.Create(ctx, link); err != nil {
		return CreateLinkOutput{}, err
	}

	return CreateLinkOutput{
		ID:        link.ID,
		Code:      link.Code,
		ExpiresAt: link.ExpiresAt,
	}, nil
}
