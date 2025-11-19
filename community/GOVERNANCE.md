# OpenCPX Governance

This document describes the governance model for OpenCPX.

## Overview

OpenCPX is a community-governed project focused on creating an open standard for compliance posture exchange. We aim to be vendor-neutral, transparent, and community-driven.

## Principles

1. **Openness** - All discussions, decisions, and development happen in the open
2. **Transparency** - Decision-making processes are clear and documented
3. **Inclusivity** - We welcome participation from all interested parties
4. **Meritocracy** - Contributions and expertise drive influence
5. **Neutrality** - No single vendor controls the standard

## Roles

### Contributors

Anyone who contributes to the project in any form:
- Code contributions
- Documentation improvements
- Bug reports and feature requests
- Participation in discussions
- Helping others in the community

### Committers

Contributors who have demonstrated ongoing commitment and expertise. Committers can:
- Merge pull requests
- Triage issues
- Guide architectural decisions in their area

Committers are nominated by existing committers and approved by maintainers.

### Maintainers

Experienced committers who take responsibility for the project's direction. Maintainers:
- Define project roadmap
- Make final decisions on disputed issues
- Manage releases
- Onboard new committers

### Working Groups

Focused teams that drive specific aspects of the project:

#### Schema Working Group
- Defines and evolves the CPX schema
- Reviews proposed extensions
- Maintains backward compatibility
- Meets bi-weekly

#### SDK Working Group
- Develops and maintains official SDKs
- Ensures consistency across languages
- Reviews SDK contributions
- Meets bi-weekly

#### Integrations Working Group
- Develops integrations with compliance platforms
- Coordinates with vendors (Vanta, Drata, etc.)
- Documents integration patterns
- Meets monthly

#### Documentation Working Group
- Maintains project documentation
- Creates tutorials and guides
- Improves user experience
- Meets monthly

## Decision Making

### Consensus-Based

Most decisions are made through consensus:
1. Proposal is made (issue, PR, or discussion)
2. Community provides feedback
3. Proposer addresses concerns
4. Maintainers assess consensus
5. Decision is documented

### Voting

For significant decisions without consensus:
- Maintainers vote
- Simple majority wins
- Ties are broken by project lead

### Lazy Consensus

For routine decisions:
- Proposal is made with a deadline
- If no objections, proposal passes
- Typically 3-5 business days

## Proposal Process

### Minor Changes

For bug fixes, small improvements:
1. Create issue or PR
2. Get one approval
3. Merge

### Significant Changes

For new features, API changes:
1. Create proposal issue
2. Discuss with community
3. Address feedback
4. Get two approvals
5. Merge

### Major Changes

For schema changes, architectural decisions:
1. Write proposal document
2. Present to working group
3. Community review period (2 weeks minimum)
4. Working group vote
5. Maintainer approval
6. Document decision

## Release Process

1. **Feature freeze** - No new features, only bug fixes
2. **Release candidate** - Community testing period
3. **Final release** - Version tagged and published
4. **Announcement** - Blog post, social media

### Versioning

We follow [Semantic Versioning](https://semver.org/):
- MAJOR: Breaking changes
- MINOR: New features, backward compatible
- PATCH: Bug fixes, backward compatible

## Communication

### Primary Channels

- **GitHub Issues** - Bug reports, feature requests
- **GitHub Discussions** - General questions, ideas
- **Working Group Meetings** - Video calls for active work

### Meetings

- Working group meetings are recorded and published
- Meeting notes are posted to GitHub
- Calendar is publicly available

## Conflict Resolution

1. Discuss in the open (issue, PR, discussion)
2. Involve working group lead
3. Escalate to maintainers if needed
4. Maintainers make final decision

## Changes to Governance

This governance document can be amended by:
1. Proposal from any maintainer
2. Two-week community review
3. Maintainer vote (2/3 majority)

## Current Maintainers

See [MAINTAINERS.md](MAINTAINERS.md) for the current list.

## License

OpenCPX is released under the MIT License. All contributions are made under this license.
