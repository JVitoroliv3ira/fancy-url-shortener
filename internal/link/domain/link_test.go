package domain

import (
	"github.com/google/uuid"
	"testing"
	"time"
)

func TestNewLinkValidation(t *testing.T) {
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
