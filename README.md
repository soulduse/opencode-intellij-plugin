# OpenCode IntelliJ Plugin

[![Build](https://github.com/soulduse/opencode-intellij-plugin/workflows/Build/badge.svg)](https://github.com/soulduse/opencode-intellij-plugin/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[한국어](README_ko.md)

<!-- Plugin description -->
**OpenCode** integration for JetBrains IDEs. Use the open source AI coding agent directly in your IDE.

This plugin provides seamless integration with [OpenCode](https://opencode.ai), the open source AI coding agent, allowing you to leverage AI assistance for coding tasks without leaving your IDE.
<!-- Plugin description end -->

## Features

- **Quick Launch**: Use `Cmd+Esc` (Mac) or `Ctrl+Esc` (Windows/Linux) to open OpenCode
- **Context Awareness**: Automatically share your current selection or file with OpenCode
- **File References**: Insert file references with line numbers using `Cmd+Option+K` (Mac) or `Ctrl+Alt+K` (Windows/Linux)
- **Session Management**: Create and manage multiple conversation sessions
- **Plan/Build Modes**: Switch between planning and building modes
- **Multi-language Support**: English and Korean UI

## Screenshots

<!-- Add screenshots here -->

## Requirements

- IntelliJ IDEA 2023.3 or later (or other JetBrains IDEs)
- [OpenCode CLI](https://opencode.ai) installed

## Installation

### From JetBrains Marketplace

1. Open your JetBrains IDE
2. Go to `Settings/Preferences` → `Plugins` → `Marketplace`
3. Search for "OpenCode"
4. Click `Install`

### Manual Installation

1. Download the latest release from [Releases](https://github.com/soulduse/opencode-intellij-plugin/releases)
2. Go to `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
3. Select the downloaded `.zip` file

## Usage

### Quick Start

1. Install OpenCode CLI: `npm install -g opencode-ai`
2. Open the OpenCode panel using `Cmd+Esc` (Mac) or `Ctrl+Esc` (Windows/Linux)
3. Start chatting with OpenCode!

### Keyboard Shortcuts

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| Focus OpenCode | `Cmd+Esc` | `Ctrl+Esc` |
| New Session | `Cmd+Shift+Esc` | `Ctrl+Shift+Esc` |
| Insert File Reference | `Cmd+Option+K` | `Ctrl+Alt+K` |

### Settings

Go to `Settings/Preferences` → `Tools` → `OpenCode` to configure:

- **OpenCode Command**: Path to the opencode executable
- **Server Port**: Port number for OpenCode server (default: 4096)
- **Auto Start Server**: Automatically start server when IDE opens
- **Theme**: Light/Dark/System
- **Language**: English/Korean

## Development

### Prerequisites

- JDK 17+
- IntelliJ IDEA (Community or Ultimate)

### Building

```bash
./gradlew build
```

### Running in Development

```bash
./gradlew runIde
```

### Testing

```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Related Projects

- [OpenCode](https://github.com/anomalyco/opencode) - The open source AI coding agent
- [OpenCode VS Code Extension](https://opencode.ai/docs/ide/) - VS Code extension

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [OpenCode](https://opencode.ai) team for the amazing AI coding agent
- [JetBrains](https://www.jetbrains.com/) for the IntelliJ Platform SDK
- Inspired by the [Claude Code JetBrains Plugin](https://plugins.jetbrains.com/plugin/27310-claude-code-beta-)

---

**Note**: This plugin is not affiliated with or endorsed by the OpenCode team. It is a community-driven open source project.
