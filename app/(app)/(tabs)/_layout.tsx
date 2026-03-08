import { Tabs } from 'expo-router';
import { Text } from 'react-native';

export default function TabsLayout() {
  return (
    <Tabs
      initialRouteName="index"
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: '#1A1A1A',
          borderTopWidth: 0,
        },
        tabBarActiveTintColor: 'white',
        tabBarInactiveTintColor: 'rgba(255,255,255,0.4)',
      }}
    >
      {/* Home Page */}
      <Tabs.Screen
        name="home"
        options={{
          title: 'Home',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 20 }}>🏠</Text>,
        }}
      />
      {/* Index money */}
      <Tabs.Screen
        name="index"
        options={{
          title: 'Index',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 20 }}>↑</Text>,
        }}
      />
      {/* Recent Activity */}
      <Tabs.Screen
        name="activity"
        options={{
          title: 'Activity',
          tabBarIcon: ({ color }) => <Text style={{ color, fontSize: 20 }}>🕐</Text>,
        }}
      />

      {/*  Log out */}
      <Tabs.Screen
        name="logout"
        options={{
          href: null,
        }}
      />
    </Tabs>
  );
}
