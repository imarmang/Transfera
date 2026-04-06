import { Stack } from 'expo-router';

export default function ProfileLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="index" />
      <Stack.Screen name="linked-bank-accounts" />
      <Stack.Screen
        name="link-account-modal"
        options={{ presentation: 'fullScreenModal' }}
      />
    </Stack>
  );
}