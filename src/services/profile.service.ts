import { API_BASE } from '@/src/constants/api';

export type ProfileData = {
  firstName: string;
  lastName: string;
  username: string;
  phoneNumber: string;
};

// Retursn the suer's profile information
export async function getProfileDataRequest(token: string): Promise<ProfileData> {
  const res = await fetch(`${API_BASE}/api/v1/profiles/me`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error('Failed to load profile data');

  const data = await res.json();
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    username: data.username,
    phoneNumber: data.phoneNumber,
  };
}
