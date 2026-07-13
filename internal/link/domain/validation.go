package domain

import (
	"errors"
	"net/url"
)

func validateTargetURL(targetURL string) error {
	if targetURL == "" {
		return errors.New("targetURL is required")
	}

	parsed, err := url.ParseRequestURI(targetURL)

	if err != nil {
		return errors.New("targetURL is invalid")
	}

	if parsed.Scheme != "http" && parsed.Scheme != "https" {
		return errors.New("targetURL must use http or https")
	}

	if parsed.Host == "" {
		return errors.New("targetURL host is required")
	}
	return nil
}
