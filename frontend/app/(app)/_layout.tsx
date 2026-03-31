import { Stack } from 'expo-router';

export default function App_Layout() {
  return (
    <Stack>
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      <Stack.Screen name="profile/index" options={{ headerShown: false, presentation: 'card' }} />
      <Stack.Screen name="profile/linked-bank-accounts" options={{ headerShown: false }} />
      <Stack.Screen name="send" options={{ headerShown: false, presentation: 'fullScreenModal' }} />
    </Stack>
  );
}
