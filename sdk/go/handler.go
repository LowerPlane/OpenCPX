package cpx

import (
	"encoding/json"
	"net/http"
)

// Provider is a function that returns the current compliance posture
type Provider func() (*Posture, error)

// Handler creates an HTTP handler for serving the CPX endpoint
func Handler(provider Provider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return
		}

		posture, err := provider()
		if err != nil {
			http.Error(w, "Internal server error", http.StatusInternalServerError)
			return
		}

		format := r.URL.Query().Get("format")

		var data []byte
		var contentType string

		switch format {
		case "yaml":
			// For YAML support, you can add a YAML library
			// For now, we'll return JSON with a note
			w.Header().Set("Content-Type", "application/json")
			http.Error(w, `{"error": "YAML format requires gopkg.in/yaml.v3"}`, http.StatusNotImplemented)
			return
		default:
			data, err = json.MarshalIndent(posture, "", "  ")
			contentType = "application/json"
		}

		if err != nil {
			http.Error(w, "Failed to serialize posture", http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", contentType)
		w.Header().Set("X-CPX-Version", Version)
		w.WriteHeader(http.StatusOK)
		w.Write(data)
	}
}

// Middleware wraps an existing handler and adds CPX endpoint support
func Middleware(provider Provider, next http.Handler) http.Handler {
	cpxHandler := Handler(provider)

	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/cpx" {
			cpxHandler(w, r)
			return
		}
		next.ServeHTTP(w, r)
	})
}

// RegisterHandler registers the CPX handler at the /cpx path
func RegisterHandler(mux *http.ServeMux, provider Provider) {
	mux.HandleFunc("/cpx", Handler(provider))
}
