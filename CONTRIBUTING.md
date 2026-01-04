# Contributing to OpenCode IntelliJ Plugin

Thank you for your interest in contributing to the OpenCode IntelliJ Plugin! This document provides guidelines and information for contributors.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/soulduse/opencode-intellij-plugin/issues)
2. If not, create a new issue with:
   - Clear title describing the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Environment details (IDE version, OS, OpenCode version)
   - Screenshots if applicable

### Suggesting Features

1. Check existing [Issues](https://github.com/soulduse/opencode-intellij-plugin/issues) for similar suggestions
2. Create a new issue with the `enhancement` label
3. Describe the feature and its use case
4. If possible, provide mockups or examples

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Write/update tests if applicable
5. Ensure the build passes: `./gradlew build`
6. Commit with a clear message: `git commit -m "Add: description of changes"`
7. Push to your fork: `git push origin feature/my-feature`
8. Create a Pull Request

## Development Setup

### Prerequisites

- JDK 17 or later
- IntelliJ IDEA (Community or Ultimate)
- Git

### Getting Started

```bash
# Clone the repository
git clone https://github.com/soulduse/opencode-intellij-plugin.git
cd opencode-intellij-plugin

# Build the project
./gradlew build

# Run in development mode
./gradlew runIde
```

### Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/github/soulduse/intellij/opencode/
│   │       ├── actions/         # IDE actions (shortcuts)
│   │       ├── services/        # Business logic
│   │       ├── toolwindow/      # UI components
│   │       ├── settings/        # Settings management
│   │       ├── model/           # Data models
│   │       └── listeners/       # Event listeners
│   └── resources/
│       ├── META-INF/            # Plugin configuration
│       ├── icons/               # Plugin icons
│       └── messages/            # i18n bundles
└── test/
    └── kotlin/                  # Test files
```

### Coding Standards

- Use Kotlin for all new code
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Commit Messages

Use clear, descriptive commit messages:

- `Add: feature description` - New features
- `Fix: bug description` - Bug fixes
- `Update: what was updated` - Updates to existing features
- `Refactor: what was refactored` - Code refactoring
- `Docs: what was documented` - Documentation changes
- `Test: what was tested` - Test additions/changes

### Testing

- Write unit tests for new functionality
- Ensure all tests pass before submitting PR
- Run tests with: `./gradlew test`

## Release Process

Releases are automated via GitHub Actions when a new tag is pushed:

```bash
git tag v0.1.0
git push origin v0.1.0
```

## Getting Help

- Create an issue for questions
- Join the OpenCode community on [Discord](https://opencode.ai/discord)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to make OpenCode IntelliJ Plugin better! 🚀
