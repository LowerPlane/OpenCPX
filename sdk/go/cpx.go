// Package cpx provides types and utilities for implementing OpenCPX endpoints.
package cpx

import (
	"encoding/json"
	"time"
)

// Version is the current OpenCPX schema version
const Version = "v1"

// CompliancePosture represents the overall compliance status
type CompliancePosture string

const (
	PostureCompliant          CompliancePosture = "compliant"
	PosturePartiallyCompliant CompliancePosture = "partially_compliant"
	PostureNonCompliant       CompliancePosture = "non_compliant"
	PostureUnknown            CompliancePosture = "unknown"
)

// FrameworkStatus represents the compliance state for a framework
type FrameworkStatus string

const (
	StatusCompliant    FrameworkStatus = "compliant"
	StatusPartial      FrameworkStatus = "partial"
	StatusNonCompliant FrameworkStatus = "non_compliant"
)

// ControlStatus represents the compliance state for a control
type ControlStatus string

const (
	ControlCompliant    ControlStatus = "compliant"
	ControlPartial      ControlStatus = "partial"
	ControlNonCompliant ControlStatus = "non_compliant"
)

// Posture represents the complete OpenCPX compliance posture
type Posture struct {
	Version           string                 `json:"version"`
	Timestamp         time.Time              `json:"timestamp"`
	CompliancePosture CompliancePosture      `json:"compliance_posture"`
	Organization      *Organization          `json:"organization,omitempty"`
	Frameworks        []Framework            `json:"frameworks"`
	EvidenceRefs      []interface{}          `json:"evidence_refs,omitempty"`
	Extensions        map[string]interface{} `json:"extensions,omitempty"`
}

// Organization represents the organization information
type Organization struct {
	Name    string `json:"name"`
	Domain  string `json:"domain,omitempty"`
	Contact string `json:"contact,omitempty"`
}

// Framework represents a compliance framework evaluation
type Framework struct {
	Name           string          `json:"name"`
	Version        string          `json:"version,omitempty"`
	Status         FrameworkStatus `json:"status"`
	Score          float64         `json:"score"`
	LastAudit      string          `json:"last_audit,omitempty"`
	Auditor        string          `json:"auditor,omitempty"`
	ReportRef      string          `json:"report_ref,omitempty"`
	CertificateRef string          `json:"certificate_ref,omitempty"`
	Controls       []Control       `json:"controls,omitempty"`
}

// Control represents a single compliance control
type Control struct {
	ID              string        `json:"id"`
	Title           string        `json:"title,omitempty"`
	Status          ControlStatus `json:"status"`
	Reason          string        `json:"reason,omitempty"`
	RemediationDate string        `json:"remediation_date,omitempty"`
	EvidenceRefs    []string      `json:"evidence_refs,omitempty"`
}

// EvidenceRef represents a reference to evidence with metadata
type EvidenceRef struct {
	Type        string `json:"type,omitempty"`
	Description string `json:"description,omitempty"`
	URL         string `json:"url"`
	Expires     string `json:"expires,omitempty"`
	Hash        string `json:"hash,omitempty"`
	SizeBytes   int64  `json:"size_bytes,omitempty"`
}

// NewPosture creates a new Posture with default values
func NewPosture() *Posture {
	return &Posture{
		Version:    Version,
		Timestamp:  time.Now().UTC(),
		Frameworks: []Framework{},
	}
}

// AddFramework adds a framework to the posture
func (p *Posture) AddFramework(f Framework) *Posture {
	p.Frameworks = append(p.Frameworks, f)
	return p
}

// SetOrganization sets the organization information
func (p *Posture) SetOrganization(org Organization) *Posture {
	p.Organization = &org
	return p
}

// SetPosture sets the overall compliance posture
func (p *Posture) SetPosture(posture CompliancePosture) *Posture {
	p.CompliancePosture = posture
	return p
}

// AddExtension adds a custom extension to the posture
func (p *Posture) AddExtension(key string, value interface{}) *Posture {
	if p.Extensions == nil {
		p.Extensions = make(map[string]interface{})
	}
	p.Extensions[key] = value
	return p
}

// ToJSON converts the posture to JSON bytes
func (p *Posture) ToJSON() ([]byte, error) {
	return json.Marshal(p)
}

// ToJSONIndent converts the posture to formatted JSON bytes
func (p *Posture) ToJSONIndent() ([]byte, error) {
	return json.MarshalIndent(p, "", "  ")
}

// CalculateOverallPosture calculates the overall posture based on frameworks
func (p *Posture) CalculateOverallPosture() CompliancePosture {
	if len(p.Frameworks) == 0 {
		return PostureUnknown
	}

	allCompliant := true
	anyCompliant := false

	for _, f := range p.Frameworks {
		if f.Status == StatusCompliant {
			anyCompliant = true
		} else {
			allCompliant = false
		}
	}

	if allCompliant {
		return PostureCompliant
	}
	if anyCompliant {
		return PosturePartiallyCompliant
	}
	return PostureNonCompliant
}

// NewFramework creates a new Framework with required fields
func NewFramework(name string, status FrameworkStatus, score float64) Framework {
	return Framework{
		Name:     name,
		Status:   status,
		Score:    score,
		Controls: []Control{},
	}
}

// AddControl adds a control to the framework
func (f *Framework) AddControl(c Control) *Framework {
	f.Controls = append(f.Controls, c)
	return f
}

// NewControl creates a new Control with required fields
func NewControl(id string, status ControlStatus) Control {
	return Control{
		ID:     id,
		Status: status,
	}
}
