import { createContext, PropsWithChildren, use } from "react";
import { useStorageState } from "@/src/hooks/useStorageState";

import {
  loginRequest,
  registerRequest,
  logoutRequest,
} from "@/src/services/auth.service";

type AuthContextType = {
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  session?: string | null; // store JWT here
  isLoading: boolean;
};

const AuthContext = createContext<AuthContextType>({
  signIn: async () => {},
  signUp: async () => {},
  signOut: async () => {},
  session: null,
  isLoading: false,
});

export function useSession() {
  const value = use(AuthContext);
  if (!value)
    throw new Error("useSession must be wrapped in <SessionProvider />");
  return value;
}

export function SessionProvider({ children }: PropsWithChildren) {
  const [[isLoading, session], setSession] = useStorageState("session");

  return (
    <AuthContext.Provider
      value={{
        session,
        isLoading,

        signIn: async (email, password) => {
          const token = await loginRequest(email, password);
          setSession(token);
        },

        signUp: async (email, password) => {
          const token = await registerRequest(email, password);
          setSession(token);
        },

        signOut: async () => {
          // TODO the signOut now will always set the session to null but I need to handle the token expiration case in the future
          if (session) await logoutRequest(session);
          setSession(null);
        },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
