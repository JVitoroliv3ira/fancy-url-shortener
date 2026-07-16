package generator

import (
	"context"
	"crypto/rand"
	"errors"
	"math/big"
)

const defaultCodeLength = 6
const base62Alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

type Base62CodeGenerator struct {
	length int
}

func NewBase62CodeGenerator(length int) (Base62CodeGenerator, error) {
	if length <= 0 {
		return Base62CodeGenerator{}, errors.New("code length must be positive")
	}

	return Base62CodeGenerator{}, nil
}

func (g Base62CodeGenerator) Generate(ctx context.Context) (string, error) {
	code := make([]byte, g.length)

	for i := range code {
		n, err := rand.Int(rand.Reader, big.NewInt(int64(len(base62Alphabet))))

		if err != nil {
			return "", err
		}

		code[i] = base62Alphabet[n.Int64()]
	}

	return string(code), nil
}
