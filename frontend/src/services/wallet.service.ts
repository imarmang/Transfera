import { API_BASE } from '@/src/constants/api';

export type TransferaWalletDTO = {
  accountId: string,
  walletNumber: string,
  balance: number
}

// Get the user's current balance that is on their Transfera account
export async function getTransferaWalletRequest( token: string ): Promise<TransferaWalletDTO> {

  const res = await fetch(`${API_BASE}/api/v1/transfera-wallet`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${token}`},
  });

  if ( !res.ok ) throw new Error( 'Failed to load the Transfera Wallet' );

  return res.json();
}