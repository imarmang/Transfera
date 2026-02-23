import { Link } from "expo-router";
import { Text } from "react-native";

export default function Index() {
  return (
    <Link href="/logout">
      <Text>Go to Logout</Text>
    </Link>
  );
}
