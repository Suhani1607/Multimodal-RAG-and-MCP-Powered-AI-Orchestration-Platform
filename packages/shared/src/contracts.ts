export type HealthResponse = {
  status: "ok" | "error";
  service: string;
  timestamp: string;
};

export type JobStatus =
  | "queued"
  | "processing"
  | "completed"
  | "failed";

export type DocumentElementType =
  | "title"
  | "text"
  | "table"
  | "image"
  | "list"
  | "other";

export interface SourceReference {
  documentId: string;
  pageNumber?: number;
  chunkId?: string;
  elementType?: DocumentElementType;
}

export interface ExecutionTraceEntry {
  id: string;
  taskId: string;
  sequence: number;
  serverName: string;
  toolName: string;
  status: "running" | "completed" | "failed";
  startedAt: string;
  finishedAt?: string;
  durationMs?: number;
  input?: unknown;
  output?: unknown;
  error?: string;
  sourceReferences?: SourceReference[];
}
