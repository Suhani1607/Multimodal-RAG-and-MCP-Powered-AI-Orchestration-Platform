# Spring Boot Orchestrator API

Requirements: Java 21 and Maven (or use the included Maven wrapper if added later).

Run from this directory:

```bash
mvn spring-boot:run
```

Health endpoints:
- `http://localhost:4000/health`
- `http://localhost:4000/api/v1/system/status`

This is the initial API scaffold. MCP client connections, task planning, execution traces,
and persistence workflows are intentionally incremental milestones rather than completed features.
