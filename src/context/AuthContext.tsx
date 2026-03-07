import {
  createContext,
  PropsWithChildren,
  use,
  useEffect,
  useState,
} from "react";
import { useStorageState } from "@/src/hooks/useStorageState";

import {
  loginRequest,
  registerRequest,
  logoutRequest,
  getProfileRequest,
} from "@/src/services/auth.service";

type AuthContextType = {
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  session?: string | null; // store JWT here
  isLoading: boolean;
  isProfileLoading: boolean;
  hasProfile: boolean;
  setHasProfile: (value: boolean) => void;
};

const AuthContext = createContext<AuthContextType>({
  signIn: async () => {},
  signUp: async () => {},
  signOut: async () => {},
  session: null,
  isLoading: false,
  isProfileLoading: false,
  hasProfile: false,
  setHasProfile: () => {},
});

export function useSession() {
  const value = use(AuthContext);
  if (!value)
    throw new Error("useSession must be wrapped in <SessionProvider />");
  return value;
}

export function SessionProvider({ children }: PropsWithChildren) {
  const [[isLoading, session], setSession] = useStorageState("session");
  const [hasProfile, setHasProfile] = useState(false);
  const [isProfileLoading, setIsProfileLoading] = useState(false);

  useEffect(() => {
    if (isLoading || !session) return;

    setIsProfileLoading(true);
    getProfileRequest(session)
      .then((result) => {
        setHasProfile(result);
        setIsProfileLoading(false);
      })
      .catch(() => {
        // Token is invalid/expired - clear the session
        setSession(null);
        setHasProfile(false);
        setIsProfileLoading(false);
      });
  }, [session, isLoading, setSession]);

  return (
    <AuthContext.Provider
      value={{
        session,
        isLoading,
        isProfileLoading,
        hasProfile,
        setHasProfile,

        signIn: async (email, password) => {
          const token = await loginRequest(email, password);
          const profileExists = await getProfileRequest(token);
          setHasProfile(profileExists);
          setSession(token);
        },

        signUp: async (email, password) => {
          const token = await registerRequest(email, password);
          setHasProfile(false);
          setSession(token);
        },

        signOut: async () => {
          // TODO the signOut now will always set the session to null but I need to handle the token expiration case in the future
          if (session) await logoutRequest(session);
          setHasProfile(false);
          setSession(null);
        },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
