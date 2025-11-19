using System.Text.Json;
using System.Text.Json.Serialization;

namespace OpenCPX;

/// <summary>
/// Overall compliance status.
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum CompliancePosture
{
    [JsonPropertyName("compliant")]
    Compliant,
    [JsonPropertyName("partially_compliant")]
    PartiallyCompliant,
    [JsonPropertyName("non_compliant")]
    NonCompliant,
    [JsonPropertyName("unknown")]
    Unknown
}

/// <summary>
/// Compliance state for a framework.
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum FrameworkStatus
{
    [JsonPropertyName("compliant")]
    Compliant,
    [JsonPropertyName("partial")]
    Partial,
    [JsonPropertyName("non_compliant")]
    NonCompliant
}

/// <summary>
/// Compliance state for a control.
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum ControlStatus
{
    [JsonPropertyName("compliant")]
    Compliant,
    [JsonPropertyName("partial")]
    Partial,
    [JsonPropertyName("non_compliant")]
    NonCompliant
}

/// <summary>
/// Organization information.
/// </summary>
public class Organization
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("domain")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Domain { get; set; }

    [JsonPropertyName("contact")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Contact { get; set; }

    public Organization() { }

    public Organization(string name, string? domain = null, string? contact = null)
    {
        Name = name;
        Domain = domain;
        Contact = contact;
    }
}

/// <summary>
/// Single compliance control.
/// </summary>
public class Control
{
    [JsonPropertyName("id")]
    public string Id { get; set; } = string.Empty;

    [JsonPropertyName("title")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Title { get; set; }

    [JsonPropertyName("status")]
    public ControlStatus Status { get; set; }

    [JsonPropertyName("reason")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Reason { get; set; }

    [JsonPropertyName("remediation_date")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? RemediationDate { get; set; }

    [JsonPropertyName("evidence_refs")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public List<string>? EvidenceRefs { get; set; }

    public Control() { }

    public Control(string id, ControlStatus status, string? title = null)
    {
        Id = id;
        Status = status;
        Title = title;
    }

    public Control AddEvidenceRef(string evidenceRef)
    {
        EvidenceRefs ??= new List<string>();
        EvidenceRefs.Add(evidenceRef);
        return this;
    }
}

/// <summary>
/// Compliance framework evaluation.
/// </summary>
public class Framework
{
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("version")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Version { get; set; }

    [JsonPropertyName("status")]
    public FrameworkStatus Status { get; set; }

    [JsonPropertyName("score")]
    public double Score { get; set; }

    [JsonPropertyName("last_audit")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? LastAudit { get; set; }

    [JsonPropertyName("auditor")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Auditor { get; set; }

    [JsonPropertyName("report_ref")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? ReportRef { get; set; }

    [JsonPropertyName("certificate_ref")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? CertificateRef { get; set; }

    [JsonPropertyName("controls")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public List<Control>? Controls { get; set; }

    public Framework() { }

    public Framework(string name, FrameworkStatus status, double score)
    {
        Name = name;
        Status = status;
        Score = score;
    }

    public Framework AddControl(Control control)
    {
        Controls ??= new List<Control>();
        Controls.Add(control);
        return this;
    }
}

/// <summary>
/// Complete OpenCPX compliance posture.
/// </summary>
public class Posture
{
    public const string VERSION = "v1";

    [JsonPropertyName("version")]
    public string Version { get; set; } = VERSION;

    [JsonPropertyName("timestamp")]
    public DateTime Timestamp { get; set; }

    [JsonPropertyName("compliance_posture")]
    public CompliancePosture CompliancePostureStatus { get; set; }

    [JsonPropertyName("organization")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public Organization? Organization { get; set; }

    [JsonPropertyName("frameworks")]
    public List<Framework> Frameworks { get; set; } = new();

    [JsonPropertyName("evidence_refs")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public List<object>? EvidenceRefs { get; set; }

    [JsonPropertyName("extensions")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public Dictionary<string, object>? Extensions { get; set; }

    public Posture()
    {
        Timestamp = DateTime.UtcNow;
        CompliancePostureStatus = CompliancePosture.Unknown;
    }

    public Posture(CompliancePosture posture)
    {
        Timestamp = DateTime.UtcNow;
        CompliancePostureStatus = posture;
    }

    public Posture SetPosture(CompliancePosture posture)
    {
        CompliancePostureStatus = posture;
        return this;
    }

    public Posture SetOrganization(Organization org)
    {
        Organization = org;
        return this;
    }

    public Posture AddFramework(Framework framework)
    {
        Frameworks.Add(framework);
        return this;
    }

    public Posture AddExtension(string key, object value)
    {
        Extensions ??= new Dictionary<string, object>();
        Extensions[key] = value;
        return this;
    }

    public Posture AddEvidenceRef(object evidenceRef)
    {
        EvidenceRefs ??= new List<object>();
        EvidenceRefs.Add(evidenceRef);
        return this;
    }

    /// <summary>
    /// Calculate overall posture based on frameworks.
    /// </summary>
    public CompliancePosture CalculateOverallPosture()
    {
        if (Frameworks.Count == 0)
        {
            return CompliancePosture.Unknown;
        }

        bool allCompliant = Frameworks.All(f => f.Status == FrameworkStatus.Compliant);
        bool anyCompliant = Frameworks.Any(f => f.Status == FrameworkStatus.Compliant);

        if (allCompliant)
        {
            return CompliancePosture.Compliant;
        }
        if (anyCompliant)
        {
            return CompliancePosture.PartiallyCompliant;
        }
        return CompliancePosture.NonCompliant;
    }

    /// <summary>
    /// Convert to JSON string.
    /// </summary>
    public string ToJson()
    {
        return JsonSerializer.Serialize(this, GetJsonOptions());
    }

    /// <summary>
    /// Convert to formatted JSON string.
    /// </summary>
    public string ToJsonPretty()
    {
        var options = GetJsonOptions();
        options.WriteIndented = true;
        return JsonSerializer.Serialize(this, options);
    }

    private static JsonSerializerOptions GetJsonOptions()
    {
        return new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower,
            DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull
        };
    }
}
