package generator

import (
	"context"
	"strings"
	"testing"
)

func TestRandomCodeGeneratorGenerateReturnsCodeWithConfiguredLength(t *testing.T) {
	ctx := context.Background()
	generator, err := NewBase62CodeGenerator(defaultCodeLength)

	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}

	code, err := generator.Generate(ctx)

	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}

	if len(code) != generator.length {
		t.Fatalf("expected code length %d, got %d", generator.length, len(code))
	}
}

func TestRandomCodeGeneratorGenerateReturnsOnlyBase62Characters(t *testing.T) {
	ctx := context.Background()
	generator, err := NewBase62CodeGenerator(defaultCodeLength)

	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}

	code, err := generator.Generate(ctx)

	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}

	for _, char := range code {
		if !strings.ContainsRune(base62Alphabet, char) {
			t.Fatalf("expected only base62 characters, got invalid char %q in code %q", char, code)
		}
	}
}

func TestNewRandomCodeGeneratorRejectsInvalidLength(t *testing.T) {
	_, err := NewBase62CodeGenerator(0)

	if err == nil {
		t.Fatalf("expected error, got nil")
	}
}
