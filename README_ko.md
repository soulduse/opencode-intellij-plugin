# OpenCode IntelliJ 플러그인

[![Build](https://github.com/soulduse/opencode-intellij-plugin/workflows/Build/badge.svg)](https://github.com/soulduse/opencode-intellij-plugin/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[English](README.md)

JetBrains IDE를 위한 **OpenCode** 통합 플러그인입니다. 오픈소스 AI 코딩 에이전트를 IDE에서 직접 사용하세요.

이 플러그인은 오픈소스 AI 코딩 에이전트인 [OpenCode](https://opencode.ai)와의 원활한 통합을 제공하여, IDE를 벗어나지 않고도 AI 지원 코딩을 활용할 수 있습니다.

## 기능

- **빠른 실행**: `Cmd+Esc` (Mac) 또는 `Ctrl+Esc` (Windows/Linux)로 OpenCode 열기
- **컨텍스트 인식**: 현재 선택 영역 또는 파일을 OpenCode와 자동 공유
- **파일 참조**: `Cmd+Option+K` (Mac) 또는 `Ctrl+Alt+K` (Windows/Linux)로 줄 번호가 포함된 파일 참조 삽입
- **세션 관리**: 여러 대화 세션 생성 및 관리
- **Plan/Build 모드**: 계획 모드와 빌드 모드 간 전환
- **다국어 지원**: 영어 및 한국어 UI

## 스크린샷

<!-- 스크린샷 추가 예정 -->

## 요구 사항

- IntelliJ IDEA 2023.3 이상 (또는 기타 JetBrains IDE)
- [OpenCode CLI](https://opencode.ai) 설치 필요

## 설치

### JetBrains Marketplace에서 설치

1. JetBrains IDE를 엽니다
2. `Settings/Preferences` → `Plugins` → `Marketplace`로 이동
3. "OpenCode" 검색
4. `Install` 클릭

### 수동 설치

1. [Releases](https://github.com/soulduse/opencode-intellij-plugin/releases)에서 최신 버전 다운로드
2. `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`로 이동
3. 다운로드한 `.zip` 파일 선택

## 사용법

### 빠른 시작

1. OpenCode CLI 설치: `npm install -g opencode-ai`
2. `Cmd+Esc` (Mac) 또는 `Ctrl+Esc` (Windows/Linux)로 OpenCode 패널 열기
3. OpenCode와 대화 시작!

### 키보드 단축키

| 동작 | macOS | Windows/Linux |
|------|-------|---------------|
| OpenCode 열기 | `Cmd+Esc` | `Ctrl+Esc` |
| 새 세션 | `Cmd+Shift+Esc` | `Ctrl+Shift+Esc` |
| 파일 참조 삽입 | `Cmd+Option+K` | `Ctrl+Alt+K` |

### 설정

`Settings/Preferences` → `Tools` → `OpenCode`에서 설정:

- **OpenCode 명령어**: opencode 실행 파일 경로
- **서버 포트**: OpenCode 서버 포트 번호 (기본값: 4096)
- **서버 자동 시작**: IDE 시작 시 자동으로 서버 시작
- **테마**: 라이트/다크/시스템
- **언어**: English/한국어

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

### 테스트

```bash
./gradlew test
```

## 기여

기여를 환영합니다! 가이드라인은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요.

## 관련 프로젝트

- [OpenCode](https://github.com/anomalyco/opencode) - 오픈소스 AI 코딩 에이전트
- [OpenCode VS Code 확장](https://opencode.ai/docs/ide/) - VS Code 확장 프로그램

## 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 감사의 말

- 훌륭한 AI 코딩 에이전트를 만든 [OpenCode](https://opencode.ai) 팀
- IntelliJ Platform SDK를 제공하는 [JetBrains](https://www.jetbrains.com/)
- [Claude Code JetBrains 플러그인](https://plugins.jetbrains.com/plugin/27310-claude-code-beta-)에서 영감을 받음

---

**참고**: 이 플러그인은 OpenCode 팀과 제휴하거나 보증받지 않았습니다. 커뮤니티 주도의 오픈소스 프로젝트입니다.
