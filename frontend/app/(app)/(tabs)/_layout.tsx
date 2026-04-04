import { faHouse, faArrowUp, faClockRotateLeft } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { Tabs } from 'expo-router';

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
        name="home/home"
        options={{
          title: 'Home',
          tabBarIcon: ({ color }) => <FontAwesomeIcon icon={faHouse} size={20} color={color} />,
        }}
      />

      {/* Add Money Modal */}
      <Tabs.Screen
        name="home/add-money-modal"
        options={{ href: null }}
      />

      {/* Route the user to link their account */}
      <Tabs.Screen
        name="home/no-linked-account-modal"
        options={{ href: null }}
      />
      <Tabs.Screen
        name="index"
        options={{
          title: 'Send',
          tabBarIcon: ({ color }) => <FontAwesomeIcon icon={faArrowUp} size={20} color={color} />,
        }}
      />
      <Tabs.Screen
        name="activity"
        options={{
          title: 'Activity',
          tabBarIcon: ({ color }) => (
            <FontAwesomeIcon icon={faClockRotateLeft} size={20} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
