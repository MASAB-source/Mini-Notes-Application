import { useState, useEffect } from "react";

// One helper for every backend call. Adds the JWT when we have one.
async function api(path, { method = "GET", body, token } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (token) headers.Authorization = "Bearer " + token;

  const res = await fetch(path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (res.status === 403 && token) throw new Error("SESSION_EXPIRED");

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) throw new Error((data && data.error) || "Request failed (" + res.status + ")");
  return data;
}

const box = { border: "1px solid #ccc", padding: 16, marginBottom: 16 };
const field = { display: "block", width: "100%", boxSizing: "border-box", margin: "4px 0", padding: 8, font: "inherit" };

function AuthForm({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [form, setForm] = useState({ fullName: "", email: "", password: "" });
  const [error, setError] = useState("");

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  async function submit(e) {
    e.preventDefault();
    setError("");
    try {
      const payload = isRegister
        ? form
        : { email: form.email, password: form.password };
      const data = await api("/api/auth/" + (isRegister ? "register" : "login"), {
        method: "POST",
        body: payload,
      });
      onLogin(data.accessToken, form.email);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <form style={box} onSubmit={submit}>
      <h2>{isRegister ? "Register" : "Log in"}</h2>
      {isRegister && (
        <input style={field} name="fullName" placeholder="Full name" value={form.fullName} onChange={change} required />
      )}
      <input style={field} name="email" type="email" placeholder="Email" value={form.email} onChange={change} required />
      <input style={field} name="password" type="password" placeholder="Password" value={form.password} onChange={change} required />
      <p style={{ color: "#b00020", minHeight: 20 }}>{error}</p>
      <button type="submit">{isRegister ? "Register" : "Log in"}</button>{" "}
      <button type="button" onClick={() => { setIsRegister(!isRegister); setError(""); }}>
        {isRegister ? "Have an account? Log in" : "Need an account? Register"}
      </button>
    </form>
  );
}

function Notes({ token, email, onLogout }) {
  const [notes, setNotes] = useState([]);
  const [form, setForm] = useState({ title: "", content: "" });
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  // Run an API call; log out automatically if the token has expired.
  async function call(path, options) {
    try {
      return await api(path, { ...options, token });
    } catch (err) {
      if (err.message === "SESSION_EXPIRED") onLogout();
      else setError(err.message);
      return undefined;
    }
  }

  async function load() {
    const data = await call("/api/notes");
    if (data) setNotes(data);
  }

  useEffect(() => { load(); }, []);

  async function save(e) {
    e.preventDefault();
    setError("");
    const path = editingId ? "/api/notes/" + editingId : "/api/notes";
    await call(path, { method: editingId ? "PUT" : "POST", body: form });
    cancelEdit();
    load();
  }

  async function remove(id) {
    setError("");
    await call("/api/notes/" + id, { method: "DELETE" });
    load();
  }

  function startEdit(n) {
    setEditingId(n.id);
    setForm({ title: n.title, content: n.content || "" });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm({ title: "", content: "" });
  }

  return (
    <div>
      <p>
        Logged in as {email} <button onClick={onLogout}>Log out</button>
      </p>

      <form style={box} onSubmit={save}>
        <h2>{editingId ? "Edit note" : "New note"}</h2>
        <input style={field} placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
        <textarea style={field} rows={3} placeholder="Content" value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })} />
        <button type="submit">{editingId ? "Update note" : "Save note"}</button>{" "}
        {editingId && <button type="button" onClick={cancelEdit}>Cancel edit</button>}
      </form>

      <p style={{ color: "#b00020" }}>{error}</p>

      {notes.length === 0 && <p>No notes yet. Add one above.</p>}
      {notes.map((n) => (
        <div key={n.id} style={box}>
          <h3 style={{ margin: "0 0 4px" }}>{n.title}</h3>
          <p style={{ whiteSpace: "pre-wrap", margin: "0 0 8px" }}>{n.content}</p>
          <button onClick={() => startEdit(n)}>Edit</button>{" "}
          <button onClick={() => remove(n.id)}>Delete</button>
        </div>
      ))}
    </div>
  );
}

export default function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [email, setEmail] = useState(localStorage.getItem("email"));

  function login(t, e) {
    localStorage.setItem("token", t);
    localStorage.setItem("email", e);
    setToken(t);
    setEmail(e);
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    setToken(null);
    setEmail(null);
  }

  return (
    <div style={{ fontFamily: "system-ui, sans-serif", maxWidth: 560, margin: "2rem auto", padding: "0 1rem" }}>
      <h1>Notes app</h1>
      {token ? <Notes token={token} email={email} onLogout={logout} /> : <AuthForm onLogin={login} />}
    </div>
  );
}
