import { useEffect, useState } from "react";
import {
  Activity, Boxes, CheckCircle2, CircleDashed, Database, FileText,
  FolderKanban, Network, Server, ShieldCheck, Workflow
} from "lucide-react";

type Status = {
  service: string;
  status: string;
  dependencies?: { rag?: { reachable: boolean; status?: number; error?: string } };
};

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:4000";

function App() {
  const [system, setSystem] = useState<Status | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    fetch(`${API_BASE}/api/v1/system/status`)
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error("One or more backend services are unavailable.");
        return data as Status;
      })
      .then((data) => { if (active) setSystem(data); })
      .catch((err: unknown) => {
        if (active) setError(err instanceof Error ? err.message : "Could not connect to API.");
      });
    return () => { active = false; };
  }, []);

  const ragOnline = system?.dependencies?.rag?.reachable === true;

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand"><div className="brand-mark"><Network size={20}/></div><div><strong>TraceRAG</strong><small>RAG + MCP Control Center</small></div></div>
        <div className="nav-label">WORKSPACE</div>
        <button className="nav-item active"><FolderKanban size={17}/> Projects <span className="nav-count">1</span></button>
        <button className="nav-item"><FileText size={17}/> Documents</button>
        <button className="nav-item"><Workflow size={17}/> RAG pipeline</button>
        <button className="nav-item"><Boxes size={17}/> MCP servers</button>
        <button className="nav-item"><Activity size={17}/> Execution traces</button>
        <div className="sidebar-bottom"><ShieldCheck size={16}/><span>Local-first workspace</span></div>
      </aside>

      <main className="main">
        <header className="topbar"><div><span className="eyebrow">WORKSPACE / OVERVIEW</span><h1>Project control center</h1></div><div className="local-pill"><span className="pulse"/> LOCAL DEVELOPMENT</div></header>

        <section className="welcome">
          <div className="welcome-copy"><span className="overline">MULTIMODAL INTELLIGENCE · MCP ORCHESTRATION</span><h2>From documents to<br/><em>observable AI workflows.</em></h2><p>Process complex documents, orchestrate multiple MCP servers, and inspect every tool call behind the result.</p><button className="primary" onClick={() => document.getElementById("milestones")?.scrollIntoView({ behavior: "smooth" })}>Explore project foundation <span>↗</span></button></div>
          <div className="orb-wrap"><div className="orb"><div className="orb-core"><Network size={44}/></div><span className="orb-node node-a"><FileText size={17}/></span><span className="orb-node node-b"><Database size={17}/></span><span className="orb-node node-c"><Workflow size={17}/></span></div><div className="orb-caption">DOCUMENTS <span>→</span> TOOLS <span>→</span> EVIDENCE</div></div>
        </section>

        <section className="section-heading"><div><span className="eyebrow">SYSTEM STATUS</span><h3>Service health</h3></div><span className="subtle">Live check on page load</span></section>
        <section className="status-grid">
          <StatusCard icon={<Server/>} name="Node.js API" detail="Express · Port 4000" state={error ? "Check connection" : system ? "Connected" : "Checking…"} good={!error && !!system}/>
          <StatusCard icon={<Boxes/>} name="Python RAG" detail="FastAPI · Port 8000" state={system ? (ragOnline ? "Connected" : "Unavailable") : "Checking…"} good={ragOnline}/>
          <StatusCard icon={<Database/>} name="PostgreSQL" detail="pgvector · Port 5433" state="Not checked by browser" good={false}/>
        </section>
        {error && <div className="notice"><strong>Backend not connected yet.</strong> Start the Node API and Python RAG service using the README commands. <span>{error}</span></div>}

        <section className="section-heading" id="milestones"><div><span className="eyebrow">BUILD PLAN</span><h3>Implementation milestones</h3></div><span className="subtle">Milestone 1 · Foundation</span></section>
        <section className="milestone-grid">
          <Milestone n="01" title="Service foundation" desc="Frontend, API, RAG service and local database." state="IN PROGRESS" icon={<Server/>}/>
          <Milestone n="02" title="Multimodal ingestion" desc="Partition, chunk, summarize, embed and inspect." state="PLANNED" icon={<FileText/>}/>
          <Milestone n="03" title="MCP orchestration" desc="Discover tools and trace cross-server execution." state="PLANNED" icon={<Workflow/>}/>
        </section>
        <footer><span>TRACERAG / ENGINEERING PREVIEW</span><span>Built for transparent AI workflows</span></footer>
      </main>
    </div>
  );
}

function StatusCard({ icon, name, detail, state, good }: { icon: React.ReactNode; name: string; detail: string; state: string; good: boolean }) {
  return <article className="status-card"><div className="card-icon">{icon}</div><div className="card-text"><strong>{name}</strong><span>{detail}</span><div className="card-state">{good ? <CheckCircle2 size={14}/> : <CircleDashed size={14}/>} {state}</div></div></article>;
}

function Milestone({ n, title, desc, state, icon }: { n: string; title: string; desc: string; state: string; icon: React.ReactNode }) {
  return <article className="milestone"><div className="milestone-top"><span>{n}</span>{icon}</div><h4>{title}</h4><p>{desc}</p><div className="milestone-state">{state}</div></article>;
}

export default App;
