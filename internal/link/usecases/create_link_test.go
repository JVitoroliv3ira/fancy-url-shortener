package usecases

import (
	"context"
	"errors"
	"github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/domain"
	"github.com/google/uuid"
	"testing"
	"time"
)

func TestCreateLinkExecuteCreatesLink(t *testing.T) {
	ctx := context.Background()
	now := time.Date(2026, 7, 15, 12, 0, 0, 0, time.UTC)
	id := domain.LinkID(uuid.New())
	code := "aBcDeF"

	repository := &fakeLinkRepository{}
	codeGenerator := &fakeCodeGenerator{code: code}
	idGenerator := &fakeIDGenerator{id: id}
	clock := &fixedClock{now: now}

	uc := NewCreateLink(repository, codeGenerator, idGenerator, clock)

	_, err := uc.Execute(ctx, CreateLinkInput{TargetURL: "http://localhost:8080"})

	if err != nil {
		t.Fatalf("expected nil erro, but got %v", err)
	}

	if !repository.called {
		t.Fatalf("expected repository to be called")
	}

	if !codeGenerator.called {
		t.Fatalf("expected codeGenerator to be called")
	}

	if !idGenerator.called {
		t.Fatalf("expected idGenerator to be called")
	}

	if !clock.called {
		t.Fatalf("expected clock to be called")
	}

	if repository.link.ID != id {
		t.Fatalf("expected saved link ID %v, but got %v", id, repository.link.ID)
	}

	if repository.link.Code != code {
		t.Fatalf("expected saved link code %q, but got %q", code, repository.link.Code)
	}

	if repository.link.Status != domain.LinkStatusActive {
		t.Fatalf("expected saved link Status %q, got %q", domain.LinkStatusActive, repository.link.Status)
	}

	if !repository.link.CreatedAt.Equal(now) {
		t.Fatalf("expected saved link CreatedAt %v, got %v", now, repository.link.CreatedAt)
	}

	expectedExpiresAt := now.Add(domain.DefaultLinkTTL)
	if !repository.link.ExpiresAt.Equal(expectedExpiresAt) {
		t.Fatalf("expected saved link ExpiresAt %v, got %v", expectedExpiresAt, repository.link.ExpiresAt)
	}
}

func TestCreateLinkExecuteReturnsCodeGeneratorError(t *testing.T) {
	ctx := context.Background()
	wantErr := errors.New("code generator failed")

	repository := &fakeLinkRepository{}
	codeGenerator := &fakeCodeGenerator{err: wantErr}
	idGenerator := &fakeIDGenerator{id: domain.LinkID(uuid.New())}
	clock := &fixedClock{now: time.Date(2026, 7, 15, 12, 0, 0, 0, time.UTC)}

	uc := NewCreateLink(repository, codeGenerator, idGenerator, clock)

	_, err := uc.Execute(ctx, CreateLinkInput{TargetURL: "http://localhost:8080"})

	if err == nil {
		t.Fatalf("expected err, got nil")
	}

	if err.Error() != wantErr.Error() {
		t.Fatalf("expected error %v, got %v", wantErr, err)
	}

	if !codeGenerator.called {
		t.Fatalf("expected codeGenerator to be called")
	}

	if repository.called {
		t.Fatalf("expected repository not to be called")
	}
}

func TestCreateLinkExecuteReturnsDomainValidationError(t *testing.T) {
	ctx := context.Background()
	wantErr := errors.New("targetURL is required")

	repository := &fakeLinkRepository{}
	codeGenerator := &fakeCodeGenerator{code: "aBcDeF"}
	idGenerator := &fakeIDGenerator{id: domain.LinkID(uuid.New())}
	clock := &fixedClock{now: time.Date(2026, 7, 15, 12, 0, 0, 0, time.UTC)}

	uc := NewCreateLink(repository, codeGenerator, idGenerator, clock)

	_, err := uc.Execute(ctx, CreateLinkInput{TargetURL: ""})

	if err == nil {
		t.Fatalf("expected err, got nil")
	}

	if err.Error() != wantErr.Error() {
		t.Fatalf("expected error %v, got %v", wantErr, err)
	}

	if !codeGenerator.called {
		t.Fatalf("expected codeGenerator to be called")
	}

	if repository.called {
		t.Fatalf("expected repository not to be called")
	}
}

func TestCreateLinkExecuteReturnsRepositoryError(t *testing.T) {
	ctx := context.Background()
	wantErr := errors.New("link repository failed")

	repository := &fakeLinkRepository{err: wantErr}
	codeGenerator := &fakeCodeGenerator{code: "aBcDeF"}
	idGenerator := &fakeIDGenerator{id: domain.LinkID(uuid.New())}
	clock := &fixedClock{now: time.Date(2026, 7, 15, 12, 0, 0, 0, time.UTC)}

	uc := NewCreateLink(repository, codeGenerator, idGenerator, clock)

	_, err := uc.Execute(ctx, CreateLinkInput{TargetURL: "http://localhost:8080"})

	if err == nil {
		t.Fatalf("expected err, got nil")
	}

	if err.Error() != wantErr.Error() {
		t.Fatalf("expected error %v, got %v", wantErr, err)
	}

	if !codeGenerator.called {
		t.Fatalf("expected codeGenerator to be called")
	}

	if !repository.called {
		t.Fatalf("expected repository to be called")
	}
}

type fakeCodeGenerator struct {
	called bool
	code   string
	err    error
}

func (g *fakeCodeGenerator) Generate(ctx context.Context) (string, error) {
	g.called = true
	return g.code, g.err
}

type fakeIDGenerator struct {
	called bool
	id     domain.LinkID
}

func (g *fakeIDGenerator) Generate() domain.LinkID {
	g.called = true
	return g.id
}

type fakeLinkRepository struct {
	called bool
	link   domain.Link
	err    error
}

func (r *fakeLinkRepository) Create(ctx context.Context, link domain.Link) error {
	r.called = true
	r.link = link
	return r.err
}

type fixedClock struct {
	called bool
	now    time.Time
}

func (c *fixedClock) Now() time.Time {
	c.called = true
	return c.now
}
