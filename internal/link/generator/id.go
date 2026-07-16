package generator

import (
	"github.com/JVitoroliv3ira/fancy-url-shortener/internal/link/domain"
	"github.com/google/uuid"
)

type UUIDLinkIDGenerator struct{}

func NewUUIDLinkIDGenerator() UUIDLinkIDGenerator {
	return UUIDLinkIDGenerator{}
}

func (g UUIDLinkIDGenerator) Generate() domain.LinkID {
	return domain.LinkID(uuid.New())
}
