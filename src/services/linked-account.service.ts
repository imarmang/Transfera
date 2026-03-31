import { API_BASE } from '../constants/api';

export type LinkedBankAccountDTO = {
  id: string;
  bankName: string;
  accountHolderName: string;
  lastFourDigitsAccountNumber: string;
  routingNumber: string;
  accountType: string;
};

export type CreateLinkedBankAccountRequestDTO = {
  bankName: string;
  accountHolderName: string;
  accountNumber: string;
  routingNumber: string;
  accountType: string;
};

// Get all the linked bank accounts
export async function getLinkedBankAccountRequest(token: string): Promise<LinkedBankAccountDTO[]> {
  const res = await fetch(`${API_BASE}/api/v1/linked-bank-account`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}` },
  });

  if (res.status === 404) return [];
  if (!res.ok) throw new Error('Failed to load linked accounts');

  return res.json();
}

// Link a bank account
export async function createLinkedAccountRequest(
  token: string,
  request: CreateLinkedBankAccountRequestDTO
): Promise<LinkedBankAccountDTO> {
  const res = await fetch(`${API_BASE}/api/v1/linked-bank-account`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(request),
  });

  if (res.status === 409) throw new Error('This account is already linked.');
  if (!res.ok) throw new Error('Failed to link account');

  return res.json();
}
