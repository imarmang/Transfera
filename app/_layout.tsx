import { Stack } from "expo-router";
import { SessionProvider, useSession } from "@/ctx";
import { SplashScreenController } from "@/splash";

export default function RootLayout() {
  return (
    <SessionProvider>
      <SplashScreenController />
      <RootNavigator />
    </SessionProvider>
  );
}

function RootNavigator() {
  const { session } = useSession();

  return (
    <Stack>
      {/* Authenticated Area */}

      {/* Logged-Out Area */}
    </Stack>
  );
}
