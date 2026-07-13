package domain

import (
	"errors"
	"github.com/google/uuid"
	"net/url"
	"time"
)

type LinkID uuid.UUID

type LinkStatus string

const (
	LinkStatusActive   LinkStatus = "active"
	LinkStatusInactive LinkStatus = "inactive"
)

type Link struct {
	ID        LinkID
	Code      string
	TargetURL string
	Status    LinkStatus
	CreatedAt time.Time
	ExpiresAt time.Time
}

const DefaultLinkStatus = LinkStatusActive
const DefaultLinkTTL = 7 * 24 * time.Hour

func NewLink(id LinkID, code string, targetURL string, now time.Time) (Link, error) {
	if uuid.UUID(id) == uuid.Nil {
		return Link{}, errors.New("id is required")
	}

	if code == "" {
		return Link{}, errors.New("code is required")
	}

	if targetURL == "" {
		return Link{}, errors.New("targetURL is required")
	}

	if now.IsZero() {
		return Link{}, errors.New("now is required")
	}

	parsed, err := url.ParseRequestURI(targetURL)

	if err != nil {
		return Link{}, errors.New("targetURL is invalid")
	}

	if parsed.Scheme != "http" && parsed.Scheme != "https" {
		return Link{}, errors.New("targetURL must use http or https")
	}

	if parsed.Host == "" {
		return Link{}, errors.New("targetURL host is required")
	}

	return Link{
		ID:        id,
		Code:      code,
		TargetURL: targetURL,
		Status:    DefaultLinkStatus,
		CreatedAt: now,
		ExpiresAt: now.Add(DefaultLinkTTL),
	}, nil
}
