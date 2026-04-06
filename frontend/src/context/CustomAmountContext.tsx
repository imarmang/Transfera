import { createContext, use, useState, PropsWithChildren } from 'react';

type CustomAmountContextType = {
  confirmedAmount: string | null;
  setConfirmedAmount: (amount: string | null) => void;
  hasLinkedAccount: boolean;
  setHasLinkedAccount: (value: boolean) => void;
};

const CustomAmountContext = createContext<CustomAmountContextType>({
  confirmedAmount: null,
  setConfirmedAmount: () => {},
  hasLinkedAccount: false,
  setHasLinkedAccount: () => {},
});

export function useCustomAmount() {
  const value = use(CustomAmountContext);
  if (!value) throw new Error('useCustomAmount must be wrapped in <CustomAmountProvider />');
  return value;
}

export function CustomAmountProvider({ children }: PropsWithChildren) {
  const [confirmedAmount, setConfirmedAmount] = useState<string | null>(null);
  const [hasLinkedAccount, setHasLinkedAccount] = useState(false);

  return (
    <CustomAmountContext.Provider value={{ confirmedAmount, setConfirmedAmount, hasLinkedAccount, setHasLinkedAccount }}>
      {children}
    </CustomAmountContext.Provider>
  );
}