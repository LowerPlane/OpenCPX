# Contributing to OpenCPX

Thank you for your interest in contributing to OpenCPX! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Documentation](#documentation)
- [Getting Help](#getting-help)

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md). We are committed to providing a welcoming and inclusive experience for everyone.

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check existing issues to avoid duplicates. When creating a bug report, include:

- A clear, descriptive title
- Steps to reproduce the issue
- Expected behavior
- Actual behavior
- Your environment (OS, language version, SDK version)
- Relevant logs or error messages

### Suggesting Features

Feature requests are welcome! Please include:

- A clear description of the feature
- The problem it solves
- Example use cases
- Any alternative solutions you've considered

### Contributing Code

1. **Fork the repository** and create your branch from `main`
2. **Write tests** for any new functionality
3. **Follow coding standards** for the language you're working in
4. **Update documentation** as needed
5. **Submit a pull request**

### Contributing Documentation

Documentation improvements are highly valued! This includes:

- Fixing typos or unclear explanations
- Adding examples
- Improving guides
- Translating documentation

## Development Setup

### Prerequisites

- Git
- Go 1.21+ (for Go SDK)
- Python 3.9+ (for Python SDK)
- Node.js 18+ (for JavaScript SDK)

### Clone the Repository

```bash
git clone https://github.com/opencpx/OpenCPX.git
cd OpenCPX
```

### Setting Up SDKs

**Go SDK:**
```bash
cd sdk/go
go mod download
go test ./...
```

**Python SDK:**
```bash
cd sdk/python
pip install -e .
python -m pytest
```

**JavaScript SDK:**
```bash
cd sdk/js
npm install
npm run build
npm test
```

## Pull Request Process

### Branch Naming

Use descriptive branch names:
- `feature/add-yaml-support`
- `fix/control-validation`
- `docs/update-quickstart`
- `refactor/simplify-handlers`

### Commit Messages

Write clear, concise commit messages:

```
Add YAML format support for /cpx endpoint

- Implement YAML serialization
- Add format query parameter handling
- Update documentation with examples
```

Follow these guidelines:
- Use imperative mood ("Add" not "Added")
- First line: 50 characters or less
- Body: Wrap at 72 characters
- Explain what and why, not how

### Pull Request Description

Include in your PR description:

1. **Summary** - What does this PR do?
2. **Motivation** - Why is this change needed?
3. **Changes** - Key changes made
4. **Testing** - How was this tested?
5. **Related Issues** - Links to related issues

### Review Process

1. All PRs require at least one approval from a maintainer
2. CI checks must pass
3. Address review feedback promptly
4. Squash commits when requested

## Coding Standards

### General Guidelines

- Write clear, self-documenting code
- Add comments for complex logic
- Follow the principle of least surprise
- Keep functions focused and small

### Go

- Follow [Effective Go](https://golang.org/doc/effective_go)
- Run `go fmt` and `go vet`
- Use meaningful variable names
- Handle errors explicitly

### Python

- Follow [PEP 8](https://pep8.org/)
- Use type hints
- Write docstrings for public functions
- Run `black` and `flake8`

### TypeScript/JavaScript

- Follow the existing style
- Use TypeScript for new code
- Run `eslint` and `prettier`
- Export types alongside implementations

## Documentation

### Writing Style

- Use clear, simple language
- Provide concrete examples
- Keep paragraphs short
- Use headers to organize content

### Code Examples

- Ensure examples are complete and runnable
- Include expected output when helpful
- Test examples before submitting

### API Documentation

- Document all public functions and types
- Include parameter descriptions
- Note any exceptions or errors
- Provide usage examples

## Getting Help

### Questions

- Check existing documentation
- Search closed issues
- Ask in GitHub Discussions

### Contact

- GitHub Issues: Technical questions and bug reports
- GitHub Discussions: General questions and ideas

## Recognition

Contributors will be recognized in:
- Release notes
- CONTRIBUTORS file
- Project documentation

Thank you for contributing to OpenCPX!
