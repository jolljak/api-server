## MINA PROJECT ##

```mermaid
graph TB
    subgraph "Frontend"
        A[React.js + Electron]
    end

    subgraph "Backend"
        B[Spring Boot API Server]
        C[FastAPI AI Server]
    end

    subgraph "AI Models"
        D[Whisper STT]
        E[Pyannote Diarization]
        F[Ollama LLM]
    end

    subgraph "External Services"
        G[AWS S3]
        H[MySQL/MSSQL]
        I[Jira/Notion/GitHub]
    end

    A -->|REST API| B
    A -->|Audio Upload| C
    B --> H
    B --> G
    C --> D


## Backend API Server (Spring Boot)

### 기술 스택

| 기술 | 용도 |
| --- | --- |
| Spring Boot 3.5.6 | 메인 프레임워크 |
| Spring Security + OAuth2 | 인증/인가 |
| MyBatis 3.0.5 | 데이터베이스 매핑 |
| JWT (jjwt 0.11.5) | 토큰 기반 인증 |
| AWS SDK S3 | 파일 스토리지 |
| Swagger UI | API 문서화 |

### 패키지 구조

```
com.tuk.mina
├── api/
│   ├── ctl/          # Controllers (8개 도메인)
│   │   ├── mcp/      # MCP 연동
│   │   ├── note/     # 노트 관리
│   │   ├── project/  # 프로젝트
│   │   ├── record/   # 녹음
│   │   ├── service/  # 서비스
│   │   ├── task/     # 업무
│   │   ├── team/     # 팀
│   │   └── user/     # 사용자
│   └── svc/          # Services
├── config/           # 설정 (Security, AWS, Swagger)
├── dao/              # Data Access Objects
├── dto/              # Data Transfer Objects
├── vo/               # Value Objects
└── util/             # 유틸리티

```

### 지원 데이터베이스

- **MSSQL**


    C --> E
    C --> F
    C -->|MCP| I

```
