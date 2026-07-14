package domain

import (
	"testing"
)

func TestValidateTargetURL(t *testing.T) {
	tests := []struct {
		name      string
		targetURL string
		wantErr   string
	}{
		{
			name:      "requires target url",
			targetURL: "",
			wantErr:   "targetURL is required",
		},
		{
			name:      "rejects invalid target url",
			targetURL: "://bad-url",
			wantErr:   "targetURL is invalid",
		},
		{
			name:      "requires http or https scheme",
			targetURL: "ftp://localhost:8080",
			wantErr:   "targetURL must use http or https",
		},
		{
			name:      "requires host",
			targetURL: "https:///path",
			wantErr:   "targetURL host is required",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			err := validateTargetURL(tt.targetURL)

			if err == nil {
				t.Fatalf("expected error %q, got nil", tt.wantErr)
			}

			if err.Error() != tt.wantErr {
				t.Fatalf("expected error %q, got %q", tt.wantErr, err.Error())
			}
		})
	}
}
