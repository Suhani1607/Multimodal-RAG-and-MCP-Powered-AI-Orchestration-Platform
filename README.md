# Multimodal RAG + MCP Orchestrator

Local-first starter scaffold for a multimodal RAG application with a TypeScript MCP host/backend.

## Current milestone

This scaffold provides:
- React + Vite + TypeScript frontend
- Express + TypeScript API
- FastAPI RAG service
- PostgreSQL with pgvector via Docker Compose
- Shared initial API contract
- Health checks for the API and RAG service

MCP servers and document ingestion are intentionally not implemented yet. They will be added in later milestones.

## Prerequisites

- Node.js 20+
- npm
- Python 3.11+
- Docker Desktop / Docker Engine with Compose

## 1. Configure environment

Copy `.env.example` to `.env` in the repository root. The current services use local defaults, so no model API key is required for this milestone.

## 2. Start PostgreSQL

```bash
docker compose -f infra/docker-compose.yml up -d
```

PostgreSQL listens on `localhost:5433` to avoid conflicting with an existing local PostgreSQL instance.

## 3. Start the Node API

```bash
cd services/api
npm install
npm run dev
```

API health check: http://localhost:4000/health

## 4. Start the Python RAG API

In a second terminal:

```bash
cd services/rag
python -m venv .venv
# Windows PowerShell:
.venv\Scripts\Activate.ps1
# macOS/Linux:
# source .venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

RAG health check: http://localhost:8000/health
API docs: http://localhost:8000/docs

## 5. Start the frontend

In a third terminal:

```bash
cd apps/web
npm install
npm run dev
```

Open the Vite URL shown in the terminal (normally http://localhost:5173).

## Initial acceptance checks

- Frontend loads.
- `http://localhost:4000/health` returns JSON with status `ok`.
- `http://localhost:8000/health` returns JSON with status `ok`.
- PostgreSQL is healthy: `docker compose -f infra/docker-compose.yml ps`.

## Planned next steps

1. Add persistent project/document/job schema.
2. Add document upload endpoint and job status contract.
3. Implement PDF partitioning and chunk inspection in Python.
4. Add the Document RAG MCP server using the official MCP SDK.
5. Add trace persistence and live execution timeline.
6. Add Analysis and Filesystem MCP servers.
