"""OpenCPX data models"""

from dataclasses import dataclass, field, asdict
from datetime import datetime
from enum import Enum
from typing import Optional, Any
import json


class CompliancePosture(str, Enum):
    """Overall compliance status"""
    COMPLIANT = "compliant"
    PARTIALLY_COMPLIANT = "partially_compliant"
    NON_COMPLIANT = "non_compliant"
    UNKNOWN = "unknown"


class FrameworkStatus(str, Enum):
    """Compliance state for a framework"""
    COMPLIANT = "compliant"
    PARTIAL = "partial"
    NON_COMPLIANT = "non_compliant"


class ControlStatus(str, Enum):
    """Compliance state for a control"""
    COMPLIANT = "compliant"
    PARTIAL = "partial"
    NON_COMPLIANT = "non_compliant"


@dataclass
class Organization:
    """Organization information"""
    name: str
    domain: Optional[str] = None
    contact: Optional[str] = None

    def to_dict(self) -> dict:
        result = {"name": self.name}
        if self.domain:
            result["domain"] = self.domain
        if self.contact:
            result["contact"] = self.contact
        return result


@dataclass
class EvidenceRef:
    """Reference to evidence with metadata"""
    url: str
    type: Optional[str] = None
    description: Optional[str] = None
    expires: Optional[str] = None
    hash: Optional[str] = None
    size_bytes: Optional[int] = None

    def to_dict(self) -> dict:
        result = {"url": self.url}
        if self.type:
            result["type"] = self.type
        if self.description:
            result["description"] = self.description
        if self.expires:
            result["expires"] = self.expires
        if self.hash:
            result["hash"] = self.hash
        if self.size_bytes:
            result["size_bytes"] = self.size_bytes
        return result


@dataclass
class Control:
    """Single compliance control"""
    id: str
    status: ControlStatus
    title: Optional[str] = None
    reason: Optional[str] = None
    remediation_date: Optional[str] = None
    evidence_refs: list[str] = field(default_factory=list)

    def to_dict(self) -> dict:
        result = {
            "id": self.id,
            "status": self.status.value if isinstance(self.status, ControlStatus) else self.status,
        }
        if self.title:
            result["title"] = self.title
        if self.reason:
            result["reason"] = self.reason
        if self.remediation_date:
            result["remediation_date"] = self.remediation_date
        if self.evidence_refs:
            result["evidence_refs"] = self.evidence_refs
        return result


@dataclass
class Framework:
    """Compliance framework evaluation"""
    name: str
    status: FrameworkStatus
    score: float
    version: Optional[str] = None
    last_audit: Optional[str] = None
    auditor: Optional[str] = None
    report_ref: Optional[str] = None
    certificate_ref: Optional[str] = None
    controls: list[Control] = field(default_factory=list)

    def add_control(self, control: Control) -> "Framework":
        """Add a control to this framework"""
        self.controls.append(control)
        return self

    def to_dict(self) -> dict:
        result = {
            "name": self.name,
            "status": self.status.value if isinstance(self.status, FrameworkStatus) else self.status,
            "score": self.score,
        }
        if self.version:
            result["version"] = self.version
        if self.last_audit:
            result["last_audit"] = self.last_audit
        if self.auditor:
            result["auditor"] = self.auditor
        if self.report_ref:
            result["report_ref"] = self.report_ref
        if self.certificate_ref:
            result["certificate_ref"] = self.certificate_ref
        if self.controls:
            result["controls"] = [c.to_dict() for c in self.controls]
        return result


@dataclass
class Posture:
    """Complete OpenCPX compliance posture"""
    compliance_posture: CompliancePosture = CompliancePosture.UNKNOWN
    version: str = "v1"
    timestamp: Optional[datetime] = None
    organization: Optional[Organization] = None
    frameworks: list[Framework] = field(default_factory=list)
    evidence_refs: list[Any] = field(default_factory=list)
    extensions: dict[str, Any] = field(default_factory=dict)

    def __post_init__(self):
        if self.timestamp is None:
            self.timestamp = datetime.utcnow()

    def add_framework(self, framework: Framework) -> "Posture":
        """Add a framework to the posture"""
        self.frameworks.append(framework)
        return self

    def set_organization(self, org: Organization) -> "Posture":
        """Set organization information"""
        self.organization = org
        return self

    def add_extension(self, key: str, value: Any) -> "Posture":
        """Add a custom extension"""
        self.extensions[key] = value
        return self

    def calculate_overall_posture(self) -> CompliancePosture:
        """Calculate overall posture based on frameworks"""
        if not self.frameworks:
            return CompliancePosture.UNKNOWN

        all_compliant = all(f.status == FrameworkStatus.COMPLIANT for f in self.frameworks)
        any_compliant = any(f.status == FrameworkStatus.COMPLIANT for f in self.frameworks)

        if all_compliant:
            return CompliancePosture.COMPLIANT
        if any_compliant:
            return CompliancePosture.PARTIALLY_COMPLIANT
        return CompliancePosture.NON_COMPLIANT

    def to_dict(self) -> dict:
        """Convert posture to dictionary"""
        result = {
            "version": self.version,
            "timestamp": self.timestamp.isoformat() + "Z" if self.timestamp else None,
            "compliance_posture": self.compliance_posture.value if isinstance(self.compliance_posture, CompliancePosture) else self.compliance_posture,
            "frameworks": [f.to_dict() for f in self.frameworks],
        }
        if self.organization:
            result["organization"] = self.organization.to_dict()
        if self.evidence_refs:
            result["evidence_refs"] = [
                e.to_dict() if isinstance(e, EvidenceRef) else e
                for e in self.evidence_refs
            ]
        if self.extensions:
            result["extensions"] = self.extensions
        return result

    def to_json(self, indent: Optional[int] = None) -> str:
        """Convert posture to JSON string"""
        return json.dumps(self.to_dict(), indent=indent)
