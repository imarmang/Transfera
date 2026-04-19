import { API_BASE } from '@/src/constants/api';

export type TransactionType = 'ADD_MONEY' | 'CASH_OUT' | 'SEND' | 'RECEIVED';
export type TransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED';

export type TransactionDTO = {
  transactionId: string;
  createdAt: string;
  amount: number;
  transactionType: TransactionType;
  transactionStatus: TransactionStatus;

  // USED ONLY WHEN WE CASHOUT OR ADD MONEY
  bankName: string | null;
  lastFourDigits: string | null;

  // USED WHEN WE SEND OR RECEIVE MONEY
  peerName: string;
};

export async function getTransactionHistoryRequest( token: string ) : Promise<TransactionDTO[]> {

  const res = await fetch( `${ API_BASE }/api/v1/transaction/history`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${ token }` },
    } );

  if ( !res.ok ) throw new Error( 'Failed to load transaction history' );

  return res.json();
}