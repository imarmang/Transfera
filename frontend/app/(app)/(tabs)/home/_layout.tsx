import { CustomAmountProvider } from '@/src/context/CustomAmountContext';
import { Stack } from 'expo-router';

export default function HomeLayout() {
  return (
    <CustomAmountProvider>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="index" />
        <Stack.Screen name="add-money-modal" />
        <Stack.Screen
          name="custom-amount-screen"
          options={{
            presentation: 'fullScreenModal',
          }}
        />
      </Stack>
    </CustomAmountProvider>
  );
}