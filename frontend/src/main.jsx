import React, { useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

const API = import.meta.env.VITE_API_URL || "http://localhost:8080/api";
const initialProject = {
  title: "",
  description: "",
  skills: "",
  category: "Web Development",
  maxMembers: 4,
  ownerId: "",
};

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok)
    throw new Error(data.message || "Something went wrong. Please try again.");
  return data;
}
const skillList = (text) =>
  (text || "")
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);
const date = (value) =>
  value
    ? new Date(value).toLocaleDateString(undefined, {
        month: "short",
        day: "numeric",
        year: "numeric",
      })
    : "";

function App() {
  const [page, setPage] = useState("dashboard");
  const [dashboard, setDashboard] = useState({
    projectCount: 0,
    studentCount: 0,
    teamCount: 0,
    pendingCount: 0,
    requestCount: 0,
    milestoneCount: 0,
    recentProjects: [],
  });
  const [projects, setProjects] = useState([]);
  const [students, setStudents] = useState([]);
  const [selected, setSelected] = useState(null);
  const [notice, setNotice] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const show = (message, isError = false) => {
    setError(isError ? message : "");
    setNotice(isError ? "" : message);
    setTimeout(() => {
      setNotice("");
      setError("");
    }, 4500);
  };
  const refresh = async () => {
    try {
      setLoading(true);
      const [dash, allProjects, allStudents] = await Promise.all([
        request("/dashboard"),
        request("/projects"),
        request("/students"),
      ]);
      setDashboard(dash);
      setProjects(allProjects);
      setStudents(allStudents);
    } catch (e) {
      setError(
        `Could not reach the API. Start the Spring Boot server first. (${e.message})`,
      );
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    refresh();
  }, []);
  const openProject = async (id) => {
    try {
      const data = await request(`/projects/${id}`);
      setSelected(data);
      setPage("details");
    } catch (e) {
      show(e.message, true);
    }
  };
  const nav = (target) => {
    setPage(target);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };
  return (
    <div>
      <header>
        <div className="brand" onClick={() => nav("dashboard")}>
          <span>◆</span> CampusConnect
        </div>
        <nav>
          {[
            ["dashboard", "Dashboard"],
            ["projects", "Explore Projects"],
            ["post", "Post an Idea"],
            ["approvals", "Approval Desk"],
          ].map(([id, label]) => (
            <button
              key={id}
              className={page === id ? "active" : ""}
              onClick={() => nav(id)}
            >
              {label}
            </button>
          ))}
        </nav>
      </header>
      {(notice || error) && (
        <div className={`toast ${error ? "error" : ""}`}>{notice || error}</div>
      )}
      <main>
        {loading ? (
          <div className="loading">Loading your portal…</div>
        ) : (
          <>
            {page === "dashboard" && (
              <Dashboard
                dashboard={dashboard}
                onExplore={() => nav("projects")}
                onOpen={openProject}
              />
            )}
            {page === "projects" && (
              <Projects projects={projects} onOpen={openProject} />
            )}
            {page === "post" && (
              <PostProject
                students={students}
                onCreated={async (p) => {
                  await refresh();
                  show(`“${p.title}” was submitted for mentor approval.`);
                  nav("dashboard");
                }}
              />
            )}
            {page === "approvals" && (
              <ApprovalDesk onChanged={refresh} show={show} />
            )}
            {page === "details" && selected && (
              <ProjectDetails
                data={selected}
                onBack={() => nav("projects")}
                onUpdate={openProject}
                show={show}
              />
            )}
          </>
        )}
      </main>
      <footer>CampusConnect · Build better projects, together.</footer>
    </div>
  );
}

function Dashboard({ dashboard, onExplore, onOpen }) {
  const safeDashboard = dashboard || {
    projectCount: 0,
    studentCount: 0,
    teamCount: 0,
    pendingCount: 0,
    requestCount: 0,
    milestoneCount: 0,
    recentProjects: [],
  };

  const cards = [
    [
      "Open projects",
      safeDashboard.projectCount,
      "Browse ideas waiting for teammates",
    ],
    ["Students", safeDashboard.studentCount, "Ready to collaborate"],
    ["Teams", safeDashboard.teamCount, "Groups building together"],
    [
      "Awaiting approval",
      safeDashboard.pendingCount,
      "Ideas for mentor review",
    ],
    ["Join requests", safeDashboard.requestCount, "Connections made"],
    ["Milestones", safeDashboard.milestoneCount, "Goals being tracked"],
  ];

  return (
    <>
      <section className="hero">
        <div>
          <p className="eyebrow">STUDENT COLLABORATION HUB</p>
          <h1>
            Turn your next idea
            <br />
            into a great team.
          </h1>
          <p>
            Discover meaningful projects, find talented classmates, and keep
            every milestone moving.
          </p>
          <button className="primary" onClick={onExplore}>
            Explore projects <b>→</b>
          </button>
        </div>
        <div className="hero-card">
          <span className="spark">✦</span>
          <strong>Make your mark.</strong>
          <p>One idea can start something brilliant.</p>
          <div className="avatars">
            ● ● ● ● <small>creative minds</small>
          </div>
        </div>
      </section>

      <section className="stats">
        {cards.map(([label, value, description]) => (
          <article key={label}>
            <span>{value}</span>
            <div>
              <strong>{label}</strong>
              <p>{description}</p>
            </div>
          </article>
        ))}
      </section>

      <section className="section-head">
        <div>
          <p className="eyebrow">FRESH OPPORTUNITIES</p>
          <h2>Recently posted projects</h2>
        </div>
        <button className="link" onClick={onExplore}>
          View all projects →
        </button>
      </section>

      <div className="project-grid">
        {(safeDashboard.recentProjects || []).map((p) => (
          <ProjectCard key={p.id} project={p} onOpen={onOpen} />
        ))}
      </div>
    </>
  );
}

function ProjectCard({ project, onOpen }) {
  return (
    <article className="project-card">
      <div className="card-top">
        <span className="category">{project.category}</span>
        <span className={`badge ${project.status.toLowerCase()}`}>
          {project.status}
        </span>
      </div>
      <h3>{project.title}</h3>
      <p>{project.description}</p>
      <div className="chips">
        {skillList(project.skills)
          .slice(0, 3)
          .map((s) => (
            <span key={s}>{s}</span>
          ))}
      </div>
      <div className="card-bottom">
        <small>Led by {project.owner.name}</small>
        <button onClick={() => onOpen(project.id)}>View project →</button>
      </div>
    </article>
  );
}

function Projects({ projects, onOpen }) {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const visible = useMemo(
    () =>
      projects
        .filter((p) => !status || p.status === status)
        .filter((p) =>
          `${p.title} ${p.description} ${p.skills} ${p.category}`
            .toLowerCase()
            .includes(query.toLowerCase()),
        ),
    [projects, query, status],
  );
  return (
    <>
      <section className="title-area">
        <p className="eyebrow">COLLABORATE & CREATE</p>
        <h1>Explore projects</h1>
        <p>
          Find an idea that sparks your curiosity and bring your skills to the
          team.
        </p>
      </section>
      <div className="filters">
        <input
          aria-label="Search projects"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by project, skill, or topic…"
        />
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">All statuses</option>
          <option>Open</option>
          <option>In Progress</option>
          <option>Completed</option>
        </select>
      </div>
      <p className="result-count">
        {visible.length} project{visible.length !== 1 ? "s" : ""} found
      </p>
      <div className="project-grid">
        {visible.map((p) => (
          <ProjectCard key={p.id} project={p} onOpen={onOpen} />
        ))}
      </div>
      {!visible.length && (
        <div className="empty">
          No projects match that search. Try a different word or filter.
        </div>
      )}
    </>
  );
}

function PostProject({ students, onCreated }) {
  const [form, setForm] = useState(initialProject);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });
  const submit = async (e) => {
    e.preventDefault();
    setError("");
    if (form.description.trim().length < 20)
      return setError("Please write at least 20 characters about the project.");
    if (!form.ownerId) return setError("Please choose a project owner.");
    try {
      setSending(true);
      const saved = await request("/projects", {
        method: "POST",
        body: JSON.stringify({
          ...form,
          maxMembers: Number(form.maxMembers),
          ownerId: Number(form.ownerId),
        }),
      });
      onCreated(saved);
    } catch (e) {
      setError(e.message);
    } finally {
      setSending(false);
    }
  };
  return (
    <section className="form-wrap">
      <div className="title-area">
        <p className="eyebrow">START SOMETHING NEW</p>
        <h1>Post a project idea</h1>
        <p>Give students enough detail to decide if they are the right fit.</p>
      </div>
      <form onSubmit={submit}>
        {error && <p className="form-error">{error}</p>}
        <label>
          Project title
          <input
            name="title"
            value={form.title}
            onChange={change}
            required
            maxLength="100"
            placeholder="e.g. Smart campus navigation"
          />
        </label>
        <label>
          What are you building?
          <textarea
            name="description"
            value={form.description}
            onChange={change}
            required
            minLength="20"
            maxLength="2000"
            placeholder="Describe the problem, your solution, and what the team will work on."
          />
        </label>
        <div className="two-col">
          <label>
            Category
            <select name="category" value={form.category} onChange={change}>
              <option>Web Development</option>
              <option>Mobile App</option>
              <option>Education</option>
              <option>AI / Data</option>
              <option>Social Impact</option>
            </select>
          </label>
          <label>
            Ideal team size
            <input
              name="maxMembers"
              type="number"
              min="2"
              max="10"
              value={form.maxMembers}
              onChange={change}
              required
            />
          </label>
        </div>
        <label>
          Skills needed
          <input
            name="skills"
            value={form.skills}
            onChange={change}
            required
            maxLength="250"
            placeholder="e.g. React, Java, UI/UX (separate with commas)"
          />
        </label>
        <label>
          Project owner
          <select
            name="ownerId"
            value={form.ownerId}
            onChange={change}
            required
          >
            <option value="">Select yourself</option>
            {students.map((s) => (
              <option key={s.id} value={s.id}>
                {s.name} · {s.skills}
              </option>
            ))}
          </select>
        </label>
        <button className="primary wide" disabled={sending}>
          {sending ? "Posting…" : "Post project idea →"}
        </button>
      </form>
    </section>
  );
}

function ApprovalDesk({ onChanged, show }) {
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const load = async () => {
    try {
      setLoading(true);
      setPending(await request("/projects/pending"));
    } catch (e) {
      show(e.message, true);
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    load();
  }, []);
  const decide = async (id, decision) => {
    try {
      await request(`/projects/${id}/approval`, {
        method: "PATCH",
        body: JSON.stringify({ decision }),
      });
      await load();
      await onChanged();
      show(
        decision === "Open"
          ? "Project approved and published."
          : "Project rejected.",
      );
    } catch (e) {
      show(e.message, true);
    }
  };
  return (
    <>
      <section className="title-area">
        <p className="eyebrow">MENTOR / ADMIN VIEW</p>
        <h1>Approval desk</h1>
        <p>
          Review new project ideas. Approved projects become visible to students
          and can receive join requests.
        </p>
      </section>
      {loading ? (
        <div className="loading">Loading submitted ideas…</div>
      ) : pending.length ? (
        <div className="approval-list">
          {pending.map((p) => (
            <article className="approval-card" key={p.id}>
              <div>
                <div className="card-top">
                  <span className="category">{p.category}</span>
                  <span className="badge pending">Pending Approval</span>
                </div>
                <h2>{p.title}</h2>
                <p>{p.description}</p>
                <div className="chips">
                  {skillList(p.skills).map((s) => (
                    <span key={s}>{s}</span>
                  ))}
                </div>
                <small>
                  Submitted by {p.owner.name} · Team size {p.maxMembers}
                </small>
              </div>
              <div className="approval-actions">
                <button
                  className="secondary"
                  onClick={() => decide(p.id, "Rejected")}
                >
                  Reject
                </button>
                <button
                  className="primary"
                  onClick={() => decide(p.id, "Open")}
                >
                  Approve & publish
                </button>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <div className="empty">
          All caught up—there are no projects waiting for approval.
        </div>
      )}
    </>
  );
}

function ProjectDetails({ data, onBack, onUpdate, show }) {
  const { project, milestones } = data;
  const [join, setJoin] = useState({
    studentName: "",
    studentEmail: "",
    message: "",
  });
  const [milestone, setMilestone] = useState({ title: "", dueDate: "" });
  const [mentors, setMentors] = useState([]);
  const [message, setMessage] = useState("");
  const submitJoin = async (e) => {
    e.preventDefault();
    setMessage("");
    try {
      await request("/join-requests", {
        method: "POST",
        body: JSON.stringify({ ...join, projectId: project.id }),
      });
      setMessage("Join request sent! The project lead can now review it.");
      setJoin({ studentName: "", studentEmail: "", message: "" });
    } catch (e) {
      setMessage(e.message);
    }
  };
  const findMentors = async () => {
    try {
      setMentors(
        await request(
          `/mentors/recommendations?skills=${encodeURIComponent(project.skills)}`,
        ),
      );
    } catch (e) {
      show(e.message, true);
    }
  };
  const addMilestone = async (e) => {
    e.preventDefault();
    try {
      await request("/milestones", {
        method: "POST",
        body: JSON.stringify({ ...milestone, projectId: project.id }),
      });
      setMilestone({ title: "", dueDate: "" });
      onUpdate(project.id);
      show("Milestone added.");
    } catch (e) {
      show(e.message, true);
    }
  };
  const changeStatus = async (id, status) => {
    try {
      await request(`/milestones/${id}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status }),
      });
      onUpdate(project.id);
    } catch (e) {
      show(e.message, true);
    }
  };
  return (
    <>
      <button className="back" onClick={onBack}>
        ← Back to projects
      </button>
      <section className="detail-hero">
        <div>
          <div className="card-top">
            <span className="category">{project.category}</span>
            <span className="badge open">{project.status}</span>
          </div>
          <h1>{project.title}</h1>
          <p>{project.description}</p>
          <div className="chips">
            {skillList(project.skills).map((s) => (
              <span key={s}>{s}</span>
            ))}
          </div>
          <small>
            Project lead: <b>{project.owner.name}</b> · Looking for up to{" "}
            {project.maxMembers} members
          </small>
        </div>
      </section>
      <div className="detail-grid">
        <div>
          <section className="panel">
            <h2>Milestones</h2>
            {milestones.length ? (
              <div className="milestones">
                {milestones.map((m) => (
                  <div className="milestone" key={m.id}>
                    <div>
                      <b>{m.title}</b>
                      <small>Due {date(m.dueDate)}</small>
                    </div>
                    <select
                      value={m.status}
                      onChange={(e) => changeStatus(m.id, e.target.value)}
                    >
                      <option>Not Started</option>
                      <option>In Progress</option>
                      <option>Completed</option>
                    </select>
                  </div>
                ))}
              </div>
            ) : (
              <p className="muted">No milestones yet.</p>
            )}
            <form className="inline-form" onSubmit={addMilestone}>
              <input
                required
                placeholder="Add a milestone"
                value={milestone.title}
                onChange={(e) =>
                  setMilestone({ ...milestone, title: e.target.value })
                }
              />
              <input
                required
                type="date"
                value={milestone.dueDate}
                onChange={(e) =>
                  setMilestone({ ...milestone, dueDate: e.target.value })
                }
              />
              <button className="secondary">Add</button>
            </form>
          </section>
          <section className="panel mentor">
            <div>
              <h2>Need guidance?</h2>
              <p>
                Get mentor suggestions based on the skills this project needs.
              </p>
            </div>
            <button className="secondary" onClick={findMentors}>
              Find mentors
            </button>
            {mentors.map((m) => (
              <div className="mentor-card" key={m.id}>
                <b>{m.name}</b>
                <span>{m.expertise}</span>
                <a href={`mailto:${m.email}`}>Contact mentor →</a>
              </div>
            ))}
          </section>
        </div>
        <aside className="panel join">
          <p className="eyebrow">INTERESTED?</p>
          <h2>Join this team</h2>
          <p>Introduce yourself and tell the lead how you can help.</p>
          {message && (
            <p className={message.includes("sent") ? "success" : "form-error"}>
              {message}
            </p>
          )}
          <form onSubmit={submitJoin}>
            <label>
              Your name
              <input
                required
                value={join.studentName}
                onChange={(e) =>
                  setJoin({ ...join, studentName: e.target.value })
                }
                placeholder="Your full name"
              />
            </label>
            <label>
              Email
              <input
                required
                type="email"
                value={join.studentEmail}
                onChange={(e) =>
                  setJoin({ ...join, studentEmail: e.target.value })
                }
                placeholder="you@college.edu"
              />
            </label>
            <label>
              Why are you interested?
              <textarea
                required
                minLength="10"
                value={join.message}
                onChange={(e) => setJoin({ ...join, message: e.target.value })}
                placeholder="Share your relevant skills or ideas…"
              />
            </label>
            <button className="primary wide">Send join request</button>
          </form>
        </aside>
      </div>
    </>
  );
}

createRoot(document.getElementById("root")).render(<App />);
