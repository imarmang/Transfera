import { API_BASE } from '@/src/constants/api';

// Get the user's current balance that is on their Transfera account
export type TransferaWalletDTO = {
  accountId: string,
  walletNumber: string,
  balance: number
}

export async function getTransferaWalletRequest( token: string ): Promise<TransferaWalletDTO> {

  const res = await fetch(`${API_BASE}/api/v1/transfera-wallet`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}`},
  });

  if ( !res.ok ) throw new Error( 'Failed to load the Transfera Wallet' );

  return res.json();
}


export type AddMoneyRequestDTO = {
  linkedBankAccountId: string,
  amount: number,
}

// Add Money to the user's current balance that is on their Transfera account
export async function addMoneyTransferaWalletRequest( token: string, body: AddMoneyRequestDTO ): Promise<void> {

  const res = await fetch(`${API_BASE}/api/v1/transfera-wallet/add-money`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': "application/json",
    },
    body: JSON.stringify( body ),
  });

  if ( !res.ok ) throw new Error( 'Failed to add money to Transfera Wallet' );

  return res.json();
}

export type CashOutRequestDTO = {
  linkedBankAccountId: string,
  amount: number,
}

// Add Money to the user's current balance that is on their Transfera account
export async function cashOutMoneyTransferaWalletRequest( token: string, body: CashOutRequestDTO ): Promise<void> {

  const res = await fetch(`${API_BASE}/api/v1/transfera-wallet/cash-out`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${ token }`,
      'Content-Type': "application/json",
    },
    body: JSON.stringify( body ),
  });

  if ( !res.ok ) throw new Error( 'Failed to Cash Out money to your linked account' );

  return res.json();
}