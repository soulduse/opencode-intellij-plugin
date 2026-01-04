# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2026-01-04

### Added
- Markdown rendering for AI responses with proper formatting
- Code block syntax highlighting with language detection
- Copy button for code blocks (one-click copy to clipboard)
- SSE (Server-Sent Events) streaming for real-time response updates
- Streaming message panel with live text updates
- Tool use indicator showing when AI is using tools
- Abort button to cancel ongoing requests (Escape key support)
- Loading indicator with animated dots during AI processing
- User-friendly error messages for common issues

### Improved
- Retry logic with exponential backoff for network errors
- Better error handling with specific messages for different error types
- Smoother scrolling behavior during message streaming
- Enhanced UI feedback during loading states

### Technical
- Added MarkdownRenderer component using CommonMark library
- Added SSEClient for Server-Sent Events handling
- Added RetryUtils for retry logic and error message formatting
- StreamingMessagePanel for real-time UI updates

## [0.1.0] - 2026-01-04

### Added
- Initial release
- Basic OpenCode integration with JetBrains IDEs
- Tool window with chat interface
- Session management (create, list, switch sessions)
- Quick launch action (`Cmd+Esc` / `Ctrl+Esc`)
- New session action (`Cmd+Shift+Esc` / `Ctrl+Shift+Esc`)
- Insert file reference action (`Cmd+Option+K` / `Ctrl+Alt+K`)
- Settings panel with configuration options
- Multi-language support (English, Korean)
- Plan/Build mode toggle
- Auto-start server option
- Connection status indicator

### Technical
- Ktor HTTP client for OpenCode server communication
- Kotlinx serialization for JSON parsing
- IntelliJ Platform UI components
- Project-level service for server management

[Unreleased]: https://github.com/soulduse/opencode-intellij-plugin/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/soulduse/opencode-intellij-plugin/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/soulduse/opencode-intellij-plugin/releases/tag/v0.1.0
