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

// user logs out
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

// check if the user has a profile
export async function getProfileRequest(token: string): Promise<boolean> {
  const res = await fetch(`${API_BASE}/api/v1/profiles/me`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (res.status === 404) return false;

  if (res.ok) return true;

  throw new Error('Failed to load the profile');
}

// create a user profile
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

// sending a request to change the user password
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

export async function googleAuthRequest(idToken: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth2/google`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ idToken }),
  });
  const data = await res.json();

  if (!res.ok) throw new Error(data?.message ?? 'Google sign-in failed');
  if (!data.token) throw new Error('No token returned from the server');
  return data.token;
}

export async function googleRegisterRequest(idToken: string): Promise<string> {
  const res = await fetch(`${API_BASE}/auth2/google/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ idToken }),
  });

  if (res.status === 409) throw new Error('Account already exists. Please sign in.');

  const data = await res.json();

  if (!res.ok) throw new Error(data?.message ?? 'Google sign-up failed');
  if (!data.token) throw new Error('No token returned from the server');
  return data.token;
}

// reset the user's password using the token from the email link
export async function resetPasswordRequest( token: string, newPassword: string ): Promise<string> {
  const res = await fetch( `${ API_BASE }/auth/reset-password`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token, newPassword }),
  } );

  const text = await res.text();
  const data = text ? JSON.parse( text ) : {};

  if ( !res.ok )
    throw new Error(
      data?.message ?? 'Failed to reset password. Please try again.'
    );

  return data.message;
}
