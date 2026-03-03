import { createContext, PropsWithChildren, use } from "react";
import { useStorageState } from "@/useStorageState";

type AuthContextType = {
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  session?: string | null; // store JWT here
  isLoading: boolean;
};

const AuthContext = createContext<AuthContextType>({
  signIn: async () => {},
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
          // TESTING
          const response = await fetch("http://localhost:8080/auth/login", {
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
          const token = data?.token;
          if (!token) throw new Error("No token returned from server");

          setSession(token);
        },

        signOut: async () => {
          if (session) {
            try {
              console.log(
                "[Auth] POST /auth/logout tokenPrefix:",
                session.slice(0, 20),
              );

              const res = await fetch("http://localhost:8080/auth/logout", {
                method: "POST",
                headers: { Authorization: `Bearer ${session}` },
              });

              const data = await res.json().catch(() => null);

              console.log("[Auth] logout status:", res.status);
              console.log("[Auth] logout body:", data);
            } catch (e) {
              console.log("[Auth] logout request failed:", e);
            }
          } else {
            console.log("[Auth] signOut called but session is null");
          }

          setSession(null);
        },
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
