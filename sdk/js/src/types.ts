/**
 * OpenCPX Type Definitions
 */

export const VERSION = 'v1';

export enum CompliancePosture {
  COMPLIANT = 'compliant',
  PARTIALLY_COMPLIANT = 'partially_compliant',
  NON_COMPLIANT = 'non_compliant',
  UNKNOWN = 'unknown',
}

export enum FrameworkStatus {
  COMPLIANT = 'compliant',
  PARTIAL = 'partial',
  NON_COMPLIANT = 'non_compliant',
}

export enum ControlStatus {
  COMPLIANT = 'compliant',
  PARTIAL = 'partial',
  NON_COMPLIANT = 'non_compliant',
}

export interface Organization {
  name: string;
  domain?: string;
  contact?: string;
}

export interface EvidenceRef {
  type?: string;
  description?: string;
  url: string;
  expires?: string;
  hash?: string;
  size_bytes?: number;
}

export interface Control {
  id: string;
  title?: string;
  status: ControlStatus;
  reason?: string;
  remediation_date?: string;
  evidence_refs?: string[];
}

export interface Framework {
  name: string;
  version?: string;
  status: FrameworkStatus;
  score: number;
  last_audit?: string;
  auditor?: string;
  report_ref?: string;
  certificate_ref?: string;
  controls?: Control[];
}

export interface PostureData {
  version: string;
  timestamp: string;
  compliance_posture: CompliancePosture;
  organization?: Organization;
  frameworks: Framework[];
  evidence_refs?: (string | EvidenceRef)[];
  extensions?: Record<string, unknown>;
}

export type PostureProvider = () => Posture | Promise<Posture>;

// Re-export Posture class type
import { Posture } from './models';
export { Posture };
