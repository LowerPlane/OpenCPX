# OpenCPX Working Groups

Open governance model inspired by CNCF and OpenTelemetry SIGs.

## Overview

OpenCPX is developed through working groups - focused teams that drive specific aspects of the project. This distributed governance model ensures:

- **Expertise**: Domain experts lead their areas
- **Scalability**: Work is distributed across teams
- **Transparency**: All decisions happen in the open
- **Inclusivity**: Anyone can participate

## Active Working Groups

### Schema Working Group

**Mission**: Define and evolve the OpenCPX schema while maintaining backward compatibility.

**Responsibilities**:
- Schema versioning and evolution
- Extension mechanism design
- Control mapping standards
- Validation requirements

**Meetings**: Bi-weekly, Tuesdays at 10:00 AM PT

**Current Focus**:
- v1 schema refinements
- Extension registry design
- Control mapping standardization

### SDK Working Group

**Mission**: Develop and maintain official SDKs that provide consistent, high-quality implementations.

**Responsibilities**:
- SDK development (Go, Python, JavaScript)
- API design consistency
- Performance optimization
- Documentation and examples

**Meetings**: Bi-weekly, Wednesdays at 9:00 AM PT

**Current Focus**:
- Core SDK stabilization
- Additional framework integrations
- Performance benchmarking

### Integrations Working Group

**Mission**: Enable seamless integration between OpenCPX and compliance platforms.

**Responsibilities**:
- Platform adapter development
- Vendor coordination
- Integration patterns
- Reference implementations

**Meetings**: Monthly, First Thursday at 11:00 AM PT

**Current Focus**:
- LowerPlane integration
- Vanta integration
- Drata integration
- Prometheus exporter

### Documentation Working Group

**Mission**: Create and maintain high-quality documentation that enables adoption.

**Responsibilities**:
- User guides and tutorials
- API documentation
- Example maintenance
- Translation coordination

**Meetings**: Monthly, Second Friday at 10:00 AM PT

**Current Focus**:
- Quickstart guide
- Use case documentation
- SDK documentation

## Working Group Structure

### Roles

**Chair**: Runs meetings, sets agenda, represents group to maintainers
- Term: 6 months, renewable
- Selection: Nominated by group, approved by maintainers

**Members**: Regular participants who contribute actively
- No formal term
- Join by attending and contributing

### Decision Making

1. **Consensus** - Most decisions are made by consensus
2. **Voting** - For contentious issues, simple majority
3. **Escalation** - Unresolved issues go to maintainers

### Meetings

- Open to all (unless security-sensitive)
- Recorded with consent
- Notes published publicly
- Action items tracked in GitHub

## Joining a Working Group

### How to Join

1. **Attend a meeting** - Check the [schedule](../../community/meetings/)
2. **Introduce yourself** - Share your background and interests
3. **Start contributing** - Pick up action items or open issues
4. **Stay engaged** - Regular participation builds influence

### Expectations

- **Attend regularly** - At least 50% of meetings
- **Contribute** - Code, reviews, discussions, or documentation
- **Communicate** - Let the group know if you'll be absent
- **Follow Code of Conduct** - Respectful, inclusive behavior

### No Experience Needed

Working groups welcome newcomers! We have:
- Beginner-friendly issues
- Mentorship from experienced members
- Patience with learning curves

## Proposing a New Working Group

If you identify a need for a new working group:

1. **Write a charter** - Mission, scope, initial members
2. **Get sponsors** - Two or more maintainers
3. **Present to maintainers** - Explain the need
4. **Community feedback** - Two-week review period
5. **Launch** - If approved, start organizing

### Charter Template

```markdown
# [Working Group Name]

## Mission
One sentence describing the group's purpose.

## Scope
- In scope: ...
- Out of scope: ...

## Initial Members
- Chair: [Name]
- Members: [Names]

## Meeting Cadence
[Frequency, day, time]

## Success Metrics
How will we measure impact?
```

## Working Group Lifecycle

### Formation
- Charter approved
- Initial members identified
- First meeting scheduled

### Active
- Regular meetings
- Consistent output
- Growing membership

### Maintenance
- Stable deliverables
- Reduced meeting frequency
- Focus on maintenance

### Sunset
- Mission accomplished or obsolete
- Work transferred to another group
- Formal closure by maintainers

## Communication

### Synchronous
- Working group meetings
- Ad-hoc discussions as needed

### Asynchronous
- GitHub Discussions (primary)
- GitHub Issues (work tracking)
- Meeting notes (published)

## Relationship to Maintainers

Working groups have autonomy within their scope. Maintainers:
- Approve new working groups
- Resolve escalated conflicts
- Make cross-cutting decisions
- Ensure alignment with project goals

## Resources

- [Meeting Schedule](../../community/meetings/)
- [Governance Overview](../../community/GOVERNANCE.md)
- [Contributing Guide](../../community/CONTRIBUTING.md)
