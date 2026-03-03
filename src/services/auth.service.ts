import { API_BASE } from "@/src/constants/api";

// returning the session token
export async function loginRequest(
  email: string,
  password: string,
): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message ?? "Invalid email or password");
  }

  if (!data.token) throw new Error("No token returned from the server");
  return data.token;
}

// user registration
export async function registerRequest(
  email: string,
  password: string,
): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) throw new Error(data?.message ?? "Registration failed");

  if (!data.token) throw new Error("No token returned from the server");
  return data.token;
}

export async function logoutRequest(token: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/logout`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message ?? "Logout failed");
  }

  return data.message;
}
