/**
 * OpenCPX Model Classes
 */

import {
  VERSION,
  CompliancePosture,
  FrameworkStatus,
  ControlStatus,
  Organization,
  Framework,
  Control,
  EvidenceRef,
  PostureData,
} from './types';

export class Posture {
  version: string = VERSION;
  timestamp: Date;
  compliance_posture: CompliancePosture;
  organization?: Organization;
  frameworks: Framework[] = [];
  evidence_refs?: (string | EvidenceRef)[];
  extensions?: Record<string, unknown>;

  constructor(posture: CompliancePosture = CompliancePosture.UNKNOWN) {
    this.timestamp = new Date();
    this.compliance_posture = posture;
  }

  /**
   * Set the organization information
   */
  setOrganization(org: Organization): this {
    this.organization = org;
    return this;
  }

  /**
   * Add a framework to the posture
   */
  addFramework(framework: Framework): this {
    this.frameworks.push(framework);
    return this;
  }

  /**
   * Set the compliance posture
   */
  setPosture(posture: CompliancePosture): this {
    this.compliance_posture = posture;
    return this;
  }

  /**
   * Add a custom extension
   */
  addExtension(key: string, value: unknown): this {
    if (!this.extensions) {
      this.extensions = {};
    }
    this.extensions[key] = value;
    return this;
  }

  /**
   * Calculate overall posture based on frameworks
   */
  calculateOverallPosture(): CompliancePosture {
    if (this.frameworks.length === 0) {
      return CompliancePosture.UNKNOWN;
    }

    const allCompliant = this.frameworks.every(
      (f) => f.status === FrameworkStatus.COMPLIANT
    );
    const anyCompliant = this.frameworks.some(
      (f) => f.status === FrameworkStatus.COMPLIANT
    );

    if (allCompliant) {
      return CompliancePosture.COMPLIANT;
    }
    if (anyCompliant) {
      return CompliancePosture.PARTIALLY_COMPLIANT;
    }
    return CompliancePosture.NON_COMPLIANT;
  }

  /**
   * Convert to plain object
   */
  toObject(): PostureData {
    const result: PostureData = {
      version: this.version,
      timestamp: this.timestamp.toISOString(),
      compliance_posture: this.compliance_posture,
      frameworks: this.frameworks,
    };

    if (this.organization) {
      result.organization = this.organization;
    }

    if (this.evidence_refs && this.evidence_refs.length > 0) {
      result.evidence_refs = this.evidence_refs;
    }

    if (this.extensions && Object.keys(this.extensions).length > 0) {
      result.extensions = this.extensions;
    }

    return result;
  }

  /**
   * Convert to JSON string
   */
  toJSON(indent?: number): string {
    return JSON.stringify(this.toObject(), null, indent);
  }
}

/**
 * Helper function to create a new Framework
 */
export function createFramework(
  name: string,
  status: FrameworkStatus,
  score: number
): Framework {
  return {
    name,
    status,
    score,
    controls: [],
  };
}

/**
 * Helper function to create a new Control
 */
export function createControl(id: string, status: ControlStatus): Control {
  return {
    id,
    status,
  };
}

// Re-export enums and types for convenience
export {
  CompliancePosture,
  FrameworkStatus,
  ControlStatus,
  Organization,
  Framework,
  Control,
  EvidenceRef,
};
