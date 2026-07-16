package generator

import (
	"testing"

	"github.com/google/uuid"
)

func TestUUIDLinkIDGeneratorReturnsNonEmptyId(t *testing.T) {
	generator := NewUUIDLinkIDGenerator()

	got := generator.Generate()

	if uuid.UUID(got) == uuid.Nil {
		t.Fatalf("expected non-empty id, got nil uuid")
	}
}
