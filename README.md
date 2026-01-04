# OpenCode IntelliJ Plugin

[![Build](https://github.com/soulduse/opencode-intellij-plugin/workflows/Build/badge.svg)](https://github.com/soulduse/opencode-intellij-plugin/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<p align="center">
  <a href="#english">English</a> | <a href="#korean">한국어</a>
</p>

---

<a id="english"></a>

## English

<!-- Plugin description -->
**OpenCode** integration for JetBrains IDEs. Use the open source AI coding agent directly in your IDE.

This plugin provides seamless integration with [OpenCode](https://opencode.ai), the open source AI coding agent, allowing you to leverage AI assistance for coding tasks without leaving your IDE.
<!-- Plugin description end -->

### Features

- **Real-time Streaming**: See AI responses as they're generated with SSE streaming
- **Markdown Rendering**: Beautiful formatting with syntax-highlighted code blocks
- **Copy Code**: One-click copy button for all code blocks
- **Quick Launch**: Use `Cmd+Esc` (Mac) or `Ctrl+Esc` (Windows/Linux) to open OpenCode
- **Context Awareness**: Automatically share your current selection or file with OpenCode
- **File References**: Insert file references with line numbers using `Cmd+Option+K`
- **Session Management**: Create and manage multiple conversation sessions
- **Abort Requests**: Cancel ongoing AI requests with the abort button or `Escape` key
- **Plan/Build Modes**: Switch between planning and building modes
- **Multi-language Support**: English and Korean UI

### Requirements

- IntelliJ IDEA 2023.3 or later (or other JetBrains IDEs)
- [OpenCode CLI](https://opencode.ai) installed

### Installation

#### From JetBrains Marketplace (Coming Soon)

1. Open your JetBrains IDE
2. Go to `Settings/Preferences` → `Plugins` → `Marketplace`
3. Search for "OpenCode"
4. Click `Install`

#### Manual Installation

1. Download the latest release from [Releases](https://github.com/soulduse/opencode-intellij-plugin/releases)
2. Go to `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
3. Select the downloaded `.zip` file
4. Restart the IDE

#### Build from Source

```bash
git clone https://github.com/soulduse/opencode-intellij-plugin.git
cd opencode-intellij-plugin
./gradlew buildPlugin
# Plugin ZIP will be in build/distributions/
```

### Usage

#### Quick Start

1. **Install OpenCode CLI**:
   ```bash
   npm install -g @anthropics/opencode
   # or
   brew install opencode  # macOS
   ```

2. **Configure API Key** (if required):
   ```bash
   export ANTHROPIC_API_KEY=your-api-key
   ```

3. **Open Plugin**: Press `Cmd+Esc` (Mac) or `Ctrl+Esc` (Windows/Linux)

4. **Start Chatting**: Type your question and press `Enter`!

#### Keyboard Shortcuts

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| Focus OpenCode | `Cmd+Esc` | `Ctrl+Esc` |
| New Session | `Cmd+Shift+Esc` | `Ctrl+Shift+Esc` |
| Insert File Reference | `Cmd+Option+K` | `Ctrl+Alt+K` |
| Abort Request | `Escape` (while loading) | `Escape` (while loading) |
| Send Message | `Enter` | `Enter` |
| New Line | `Shift+Enter` | `Shift+Enter` |

#### Settings

Go to `Settings/Preferences` → `Tools` → `OpenCode` to configure:

| Setting | Description | Default |
|---------|-------------|---------|
| OpenCode Command | Path to the opencode executable | `opencode` |
| Server Port | Port number for OpenCode server | `4096` |
| Auto Start Server | Start server when IDE opens | `false` |
| Theme | Light/Dark/System | `System` |
| Language | UI Language | `English` |

### Development

#### Prerequisites

- JDK 17+
- IntelliJ IDEA (Community or Ultimate)

#### Building

```bash
./gradlew build
```

#### Running in Development

```bash
./gradlew runIde
```

This launches a sandbox IDE with the plugin installed for testing.

#### Testing

```bash
./gradlew test
```

### Troubleshooting

| Issue | Solution |
|-------|----------|
| "OpenCode command not found" | Install OpenCode CLI or set the correct path in Settings |
| "Cannot connect to server" | Make sure OpenCode server is running (`opencode serve`) |
| "Connection refused" | Check if the port (default: 4096) is available |
| "Request timeout" | The server might be busy; try again or abort and retry |

---

<a id="korean"></a>

## 한국어

<!-- Plugin description -->
**OpenCode** JetBrains IDE 통합 플러그인입니다. 오픈소스 AI 코딩 에이전트를 IDE에서 직접 사용하세요.

이 플러그인은 오픈소스 AI 코딩 에이전트인 [OpenCode](https://opencode.ai)와 완벽하게 통합되어, IDE를 벗어나지 않고도 AI 코딩 지원을 받을 수 있습니다.
<!-- Plugin description end -->

### 주요 기능

- **실시간 스트리밍**: SSE 스트리밍으로 AI 응답을 실시간으로 확인
- **마크다운 렌더링**: 구문 강조된 코드 블록과 함께 아름다운 포맷팅
- **코드 복사**: 모든 코드 블록에 원클릭 복사 버튼
- **빠른 실행**: `Cmd+Esc` (Mac) 또는 `Ctrl+Esc` (Windows/Linux)로 OpenCode 열기
- **컨텍스트 인식**: 현재 선택 영역이나 파일을 자동으로 OpenCode와 공유
- **파일 참조**: `Cmd+Option+K`로 줄 번호가 포함된 파일 참조 삽입
- **세션 관리**: 여러 대화 세션 생성 및 관리
- **요청 중단**: 중단 버튼 또는 `Escape` 키로 진행 중인 AI 요청 취소
- **Plan/Build 모드**: 계획 모드와 빌드 모드 간 전환
- **다국어 지원**: 영어 및 한국어 UI

### 요구 사항

- IntelliJ IDEA 2023.3 이상 (또는 다른 JetBrains IDE)
- [OpenCode CLI](https://opencode.ai) 설치 필요

### 설치 방법

#### JetBrains Marketplace에서 설치 (준비 중)

1. JetBrains IDE를 엽니다
2. `Settings/Preferences` → `Plugins` → `Marketplace`로 이동
3. "OpenCode" 검색
4. `Install` 클릭

#### 수동 설치

1. [Releases](https://github.com/soulduse/opencode-intellij-plugin/releases)에서 최신 릴리스 다운로드
2. `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`로 이동
3. 다운로드한 `.zip` 파일 선택
4. IDE 재시작

#### 소스에서 빌드

```bash
git clone https://github.com/soulduse/opencode-intellij-plugin.git
cd opencode-intellij-plugin
./gradlew buildPlugin
# 플러그인 ZIP은 build/distributions/에 생성됩니다
```

### 사용법

#### 빠른 시작

1. **OpenCode CLI 설치**:
   ```bash
   npm install -g @anthropics/opencode
   # 또는
   brew install opencode  # macOS
   ```

2. **API 키 설정** (필요한 경우):
   ```bash
   export ANTHROPIC_API_KEY=your-api-key
   ```

3. **플러그인 열기**: `Cmd+Esc` (Mac) 또는 `Ctrl+Esc` (Windows/Linux) 누르기

4. **대화 시작**: 질문을 입력하고 `Enter`를 누르세요!

#### 키보드 단축키

| 동작 | macOS | Windows/Linux |
|------|-------|---------------|
| OpenCode 열기 | `Cmd+Esc` | `Ctrl+Esc` |
| 새 세션 | `Cmd+Shift+Esc` | `Ctrl+Shift+Esc` |
| 파일 참조 삽입 | `Cmd+Option+K` | `Ctrl+Alt+K` |
| 요청 중단 | `Escape` (로딩 중) | `Escape` (로딩 중) |
| 메시지 전송 | `Enter` | `Enter` |
| 줄바꿈 | `Shift+Enter` | `Shift+Enter` |

#### 설정

`Settings/Preferences` → `Tools` → `OpenCode`에서 설정 가능:

| 설정 | 설명 | 기본값 |
|------|------|--------|
| OpenCode 명령어 | opencode 실행 파일 경로 | `opencode` |
| 서버 포트 | OpenCode 서버 포트 번호 | `4096` |
| 서버 자동 시작 | IDE 시작 시 서버 자동 시작 | `false` |
| 테마 | 라이트/다크/시스템 | `시스템` |
| 언어 | UI 언어 | `English` |

### 개발

#### 필수 조건

- JDK 17+
- IntelliJ IDEA (Community 또는 Ultimate)

#### 빌드

```bash
./gradlew build
```

#### 개발 모드 실행

```bash
./gradlew runIde
```

테스트용 샌드박스 IDE가 플러그인이 설치된 상태로 실행됩니다.

#### 테스트

```bash
./gradlew test
```

### 문제 해결

| 문제 | 해결 방법 |
|------|----------|
| "OpenCode 명령어를 찾을 수 없음" | OpenCode CLI를 설치하거나 설정에서 올바른 경로 지정 |
| "서버에 연결할 수 없음" | OpenCode 서버가 실행 중인지 확인 (`opencode serve`) |
| "연결 거부됨" | 포트(기본값: 4096)가 사용 가능한지 확인 |
| "요청 시간 초과" | 서버가 바쁠 수 있음; 다시 시도하거나 중단 후 재시도 |

---

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Related Projects

- [OpenCode](https://github.com/sst/opencode) - The open source AI coding agent
- [OpenCode Documentation](https://opencode.ai/docs) - Official documentation

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [OpenCode](https://opencode.ai) team for the amazing AI coding agent
- [JetBrains](https://www.jetbrains.com/) for the IntelliJ Platform SDK

---

**Note**: This plugin is not affiliated with or endorsed by the OpenCode team. It is a community-driven open source project.
