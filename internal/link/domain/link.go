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

	if now.IsZero() {
		return Link{}, errors.New("now is required")
	}

	if err := validateTargetURL(targetURL); err != nil {
		return Link{}, err
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

func (l Link) IsActive() bool {
	return l.Status == LinkStatusActive
}

func (l Link) IsExpired(now time.Time) bool {
	return !now.Before(l.ExpiresAt)
}

func (l Link) IsAvailable(now time.Time) bool {
	return l.IsActive() && !l.IsExpired(now)
}

func (l *Link) Activate(now time.Time) error {
	if l.IsActive() {
		return errors.New("link is already active")
	}

	if l.IsExpired(now) {
		return errors.New("link is expired")
	}

	l.Status = LinkStatusActive
	return nil
}

func (l *Link) Deactivate(now time.Time) error {
	if !l.IsActive() {
		return errors.New("link is already inactive")
	}

	if l.IsExpired(now) {
		return errors.New("link is expired")
	}

	l.Status = LinkStatusInactive
	return nil
}
