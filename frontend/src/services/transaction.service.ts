import { API_BASE } from '@/src/constants/api';

export type TransactionType = 'ADD_MONEY' | 'CASH_OUT' | 'SEND' | 'RECEIVED';
export type TransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'DECLINED';
export type MoneyRequestStatus = 'PENDING' | 'APPROVED' | 'DECLINED';

export type TransactionDTO = {
  transactionId: string;
  createdAt: string;
  amount: number;
  transactionType: TransactionType;
  transactionStatus: TransactionStatus;
  bankName: string | null;
  lastFourDigits: string | null;
  peerName: string | null;
  moneyRequestId: string | null;
};

export type MoneyRequestDTO = {
  moneyRequestId: string;
  createdAt: string;
  amount: number;
  note: string | null;
  status: MoneyRequestStatus;
  requester: string;
  requestee: string;
};

export type ActivityFeedDTO = {
  pendingRequests: MoneyRequestDTO[];
  transactions: TransactionDTO[];
};

export type CreateMoneyRequestDTO = {
  recipientUsername: string;
  amount: number;
  note: string;
};

export type RespondToMoneyRequestDTO = {
  moneyRequestId: string;
  response: 'APPROVED' | 'DECLINED';
};

export async function getTransactionHistoryRequest( token: string ): Promise<ActivityFeedDTO> {
  const res = await fetch( `${ API_BASE }/api/v1/transaction/history`, {
    method: 'GET',
    headers: { Authorization: `Bearer ${ token }` },
  } );

  if ( !res.ok ) throw new Error( 'Failed to load transaction history.' );

  return res.json();
}

export async function createMoneyRequestService( token: string, body: CreateMoneyRequestDTO ): Promise<MoneyRequestDTO> {
  const res = await fetch( `${ API_BASE }/api/v1/transaction/request`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${ token }`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify( body ),
  } );

  if ( !res.ok ) {
    const data = await res.json();
    throw new Error( data?.message ?? 'Failed to create money request.' );
  }

  return res.json();
}

export async function respondToMoneyRequestService( token: string, body: RespondToMoneyRequestDTO ): Promise<void> {
  const res = await fetch( `${ API_BASE }/api/v1/transaction/request/respond`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${ token }`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify( body ),
  } );

  if ( !res.ok ) {
    const data = await res.json();
    throw new Error( data?.message ?? 'Failed to respond to money request.' );
  }
}