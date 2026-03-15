import { API_BASE } from '@/src/constants/api';

// returning the session token
export async function loginRequest(email: string, password: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message ?? 'Invalid email or password');
  }

  if (!data.token) throw new Error('No token returned from the server');
  return data.token;
}

// user registration
export async function registerRequest(email: string, password: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) throw new Error(data?.message ?? 'Registration failed');

  if (!data.token) throw new Error('No token returned from the server');
  return data.token;
}

export async function logoutRequest(token: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/logout`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message ?? 'Logout failed');
  }

  return data.message;
}

export async function getProfileRequest(token: string): Promise<boolean> {
  const res = await fetch(`${API_BASE}/api/v1/profiles/me`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (res.status === 404) return false;

  if (res.ok) return true;

  throw new Error('Failed to load the profile');
}

export async function createProfileRequest(
  token: string,
  userName: string,
  firstName: string,
  lastName: string,
  phoneNumber: string
): Promise<void> {
  const res = await fetch(`${API_BASE}/api/v1/profiles`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({
      userName,
      firstName,
      lastName,
      phoneNumber,
    }),
  });

  if (!res.ok) {
    const data = await res.json();
    throw new Error(data?.message ?? 'Failed to create profile.');
  }
}

export async function forgotPasswordRequest(email: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth/forgot-password`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email }),
  });

  const data = await res.json();

  if (!res.ok)
    throw new Error(
      data?.message ?? 'forgotPasswordRequest(): Something went wrong. Please try again'
    );

  return data.message;
}
