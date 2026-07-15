package domain

import (
	"github.com/google/uuid"
	"testing"
	"time"
)

func TestNewLinkValidatesInput(t *testing.T) {
	now := time.Date(2026, 7, 13, 12, 0, 0, 0, time.UTC)
	validID := LinkID(uuid.New())

	tests := []struct {
		name      string
		id        LinkID
		code      string
		targetURL string
		now       time.Time
		wantErr   string
	}{
		{
			name:      "requires id",
			id:        LinkID(uuid.Nil),
			code:      "aBcDeF",
			targetURL: "http://localhost:8080",
			now:       now,
			wantErr:   "id is required",
		},
		{
			name:      "requires code",
			id:        validID,
			code:      "",
			targetURL: "http://localhost:8080",
			now:       now,
			wantErr:   "code is required",
		},
		{
			name:      "requires target url",
			id:        validID,
			code:      "aBCDeF",
			targetURL: "",
			now:       now,
			wantErr:   "targetURL is required",
		},
		{
			name:      "requires now",
			id:        validID,
			code:      "aBCDeF",
			targetURL: "http://localhost:8080",
			now:       time.Time{},
			wantErr:   "now is required",
		},
		{
			name:      "rejects invalid target url",
			id:        validID,
			code:      "aBCDeF",
			targetURL: "://bad-url",
			now:       now,
			wantErr:   "targetURL is invalid",
		},
		{
			name:      "requires http or https target url",
			id:        validID,
			code:      "aBCDeF",
			targetURL: "ftp://localhost:8080",
			now:       now,
			wantErr:   "targetURL must use http or https",
		},
		{
			name:      "requires target url host",
			id:        validID,
			code:      "aBCDeF",
			targetURL: "https:///path",
			now:       now,
			wantErr:   "targetURL host is required",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			_, err := NewLink(tt.id, tt.code, tt.targetURL, tt.now)

			if err == nil {
				t.Fatalf("expected error %q, got nil", tt.wantErr)
			}

			if err.Error() != tt.wantErr {
				t.Fatalf("expected error %q, got %q", tt.wantErr, err.Error())
			}
		})
	}
}

func TestLinkActivateValidatesStatus(t *testing.T) {
	now := time.Date(2026, 7, 13, 12, 0, 0, 0, time.UTC)

	tests := []struct {
		name       string
		status     LinkStatus
		now        time.Time
		expiresAt  time.Time
		wantErr    string
		wantStatus LinkStatus
	}{
		{
			name:       "rejects already active link",
			status:     LinkStatusActive,
			now:        now,
			expiresAt:  now.Add(time.Hour),
			wantErr:    "link is already active",
			wantStatus: LinkStatusActive,
		},
		{
			name:       "rejects expired link",
			status:     LinkStatusActive,
			now:        now,
			expiresAt:  now.Add(-time.Hour),
			wantErr:    "link is expired",
			wantStatus: LinkStatusActive,
		},
		{
			name:       "activates inactive link",
			status:     LinkStatusInactive,
			now:        now,
			expiresAt:  now.Add(time.Hour),
			wantErr:    "",
			wantStatus: LinkStatusActive,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			link := Link{
				Status:    tt.status,
				ExpiresAt: tt.expiresAt,
			}

			err := link.Activate(tt.now)

			if tt.wantErr != "" {
				if err == nil {
					t.Fatalf("expected error %q, got nil", tt.wantErr)
				}

				if err.Error() != tt.wantErr {
					t.Fatalf("expected error %q, got %q", tt.wantErr, err.Error())
				}
			} else if err != nil {
				t.Fatalf("expected nil error, got %q", err.Error())
			}

			if link.Status != tt.wantStatus {
				t.Fatalf("expected status %q, got %q", tt.wantStatus, link.Status)
			}
		})
	}
}

func TestLinkDeactivateValidatesStatus(t *testing.T) {
	now := time.Date(2026, 7, 13, 12, 0, 0, 0, time.UTC)

	tests := []struct {
		name       string
		status     LinkStatus
		now        time.Time
		expiresAt  time.Time
		wantErr    string
		wantStatus LinkStatus
	}{
		{
			name:       "rejects already inactive link",
			status:     LinkStatusInactive,
			now:        now,
			expiresAt:  now.Add(time.Hour),
			wantErr:    "link is already inactive",
			wantStatus: LinkStatusInactive,
		},
		{
			name:       "rejects expired link",
			status:     LinkStatusInactive,
			now:        now,
			expiresAt:  now.Add(-time.Hour),
			wantErr:    "link is expired",
			wantStatus: LinkStatusInactive,
		},
		{
			name:       "deactivates active link",
			status:     LinkStatusActive,
			now:        now,
			expiresAt:  now.Add(time.Hour),
			wantErr:    "",
			wantStatus: LinkStatusInactive,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			link := Link{
				Status:    tt.status,
				ExpiresAt: tt.expiresAt,
			}

			err := link.Deactivate(tt.now)

			if tt.wantErr != "" {
				if err == nil {
					t.Fatalf("expected error %q, got nil", tt.wantErr)
				}

				if err.Error() != tt.wantErr {
					t.Fatalf("expected error %q, got %q", tt.wantErr, err.Error())
				}
			} else if err != nil {
				t.Fatalf("expected nil error, got %q", err.Error())
			}

			if link.Status != tt.wantStatus {
				t.Fatalf("expected status %q, got %q", tt.wantStatus, link.Status)
			}
		})
	}
}
