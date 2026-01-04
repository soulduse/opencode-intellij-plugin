# OpenCode IntelliJ Plugin

[![Build](https://github.com/soulduse/opencode-intellij-plugin/workflows/Build/badge.svg)](https://github.com/soulduse/opencode-intellij-plugin/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[English](README.md)

<!-- Plugin description -->
**OpenCode** JetBrains IDE 통합 플러그인입니다. 오픈소스 AI 코딩 에이전트를 IDE에서 직접 사용하세요.

이 플러그인은 오픈소스 AI 코딩 에이전트인 [OpenCode](https://opencode.ai)와 완벽하게 통합되어, IDE를 벗어나지 않고도 AI 코딩 지원을 받을 수 있습니다.
<!-- Plugin description end -->

## 주요 기능

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

## 스크린샷

<!-- 스크린샷 추가 예정 -->

## 요구 사항

- IntelliJ IDEA 2023.3 이상 (또는 다른 JetBrains IDE)
- [OpenCode CLI](https://opencode.ai) 설치 필요

## 설치 방법

### JetBrains Marketplace에서 설치 (준비 중)

1. JetBrains IDE를 엽니다
2. `Settings/Preferences` → `Plugins` → `Marketplace`로 이동
3. "OpenCode" 검색
4. `Install` 클릭

### 수동 설치

1. [Releases](https://github.com/soulduse/opencode-intellij-plugin/releases)에서 최신 릴리스 다운로드
2. `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`로 이동
3. 다운로드한 `.zip` 파일 선택
4. IDE 재시작

### 소스에서 빌드

```bash
git clone https://github.com/soulduse/opencode-intellij-plugin.git
cd opencode-intellij-plugin
./gradlew buildPlugin
# 플러그인 ZIP은 build/distributions/에 생성됩니다
```

## 사용법

### 빠른 시작

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

### 키보드 단축키

| 동작 | macOS | Windows/Linux |
|------|-------|---------------|
| OpenCode 열기 | `Cmd+Esc` | `Ctrl+Esc` |
| 새 세션 | `Cmd+Shift+Esc` | `Ctrl+Shift+Esc` |
| 파일 참조 삽입 | `Cmd+Option+K` | `Ctrl+Alt+K` |
| 요청 중단 | `Escape` (로딩 중) | `Escape` (로딩 중) |
| 메시지 전송 | `Enter` | `Enter` |
| 줄바꿈 | `Shift+Enter` | `Shift+Enter` |

### 설정

`Settings/Preferences` → `Tools` → `OpenCode`에서 설정 가능:

| 설정 | 설명 | 기본값 |
|------|------|--------|
| OpenCode 명령어 | opencode 실행 파일 경로 | `opencode` |
| 서버 포트 | OpenCode 서버 포트 번호 | `4096` |
| 서버 자동 시작 | IDE 시작 시 서버 자동 시작 | `false` |
| 테마 | 라이트/다크/시스템 | `시스템` |
| 언어 | UI 언어 | `English` |

## 개발

### 필수 조건

- JDK 17+
- IntelliJ IDEA (Community 또는 Ultimate)

### 빌드

```bash
./gradlew build
```

### 개발 모드 실행

```bash
./gradlew runIde
```

테스트용 샌드박스 IDE가 플러그인이 설치된 상태로 실행됩니다.

### 테스트

```bash
./gradlew test
```

## 문제 해결

| 문제 | 해결 방법 |
|------|----------|
| "OpenCode 명령어를 찾을 수 없음" | OpenCode CLI를 설치하거나 설정에서 올바른 경로 지정 |
| "서버에 연결할 수 없음" | OpenCode 서버가 실행 중인지 확인 (`opencode serve`) |
| "연결 거부됨" | 포트(기본값: 4096)가 사용 가능한지 확인 |
| "요청 시간 초과" | 서버가 바쁠 수 있음; 다시 시도하거나 중단 후 재시도 |

## 기여하기

기여를 환영합니다! 가이드라인은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

## 관련 프로젝트

- [OpenCode](https://github.com/sst/opencode) - 오픈소스 AI 코딩 에이전트
- [OpenCode 문서](https://opencode.ai/docs) - 공식 문서

## 라이선스

이 프로젝트는 MIT 라이선스로 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 감사의 말

- 훌륭한 AI 코딩 에이전트를 만들어주신 [OpenCode](https://opencode.ai) 팀
- IntelliJ Platform SDK를 제공해주신 [JetBrains](https://www.jetbrains.com/)

---

**참고**: 이 플러그인은 OpenCode 팀과 제휴하거나 보증받은 것이 아닙니다. 커뮤니티 주도의 오픈소스 프로젝트입니다.
