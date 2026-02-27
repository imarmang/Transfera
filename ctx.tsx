import { createContext, PropsWithChildren, use } from "react";
import { useStorageState } from "@/useStorageState";
import { fetch } from "expo/fetch";

const AuthContext = createContext<{
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => void;
  session?: string | null;
  isLoading: boolean;
}>({
  signIn: async () => {},
  signOut: () => null,
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
        signIn: async (email, password) => {
          // TESTING
          const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
          });

          const data = await response.json(); // read ONCE

          console.log("STATUS:", response.status);
          console.log("BODY:", data);

          if (!response.ok) {
            throw new Error(data?.message ?? "Invalid email or password");
          }

          // Your backend returns userDTO
          setSession(data.userDTO.id); // or store token later
        },

        signOut: () => {
          setSession(null);
        },
        session,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
