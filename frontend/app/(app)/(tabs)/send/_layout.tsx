import { Stack } from 'expo-router';

export default function SendLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="index" />
      <Stack.Screen name="pay" options={{ presentation: 'fullScreenModal' }} />
    </Stack>
  );
}