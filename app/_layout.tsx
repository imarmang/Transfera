import { Stack } from "expo-router";
import { SessionProvider, useSession } from "@/src/context/AuthContext";
import { SplashScreenController } from "@/src/splash";

export default function RootLayout() {
  return (
    <SessionProvider>
      <SplashScreenController />
      <RootNavigator />
    </SessionProvider>
  );
}

function RootNavigator() {
  const { session, isLoading, hasProfile } = useSession();

  if (isLoading) return null;
  return (
    <Stack>
      {/* Logged-Out Area */}
      <Stack.Protected guard={!session}>
        <Stack.Screen name="signin" options={{ headerShown: false }} />
        <Stack.Screen name="register" options={{ headerShown: false }} />
      </Stack.Protected>

      {/* Fully Authenticated but has profile Area */}
      <Stack.Protected guard={!!session && !hasProfile}>
        <Stack.Screen name="create-profile" options={{ headerShown: false }} />
      </Stack.Protected>

      {/* Fully Authenticated and has profile Area */}
      <Stack.Protected guard={!!session && hasProfile}>
        <Stack.Screen name="(app)" options={{ headerShown: false }} />
      </Stack.Protected>
    </Stack>
  );
}
