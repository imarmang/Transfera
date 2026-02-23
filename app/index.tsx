import { Link } from "expo-router";
import { Text } from "react-native";

export default function Index() {
  return (
    <Link href="/signin">
      <Text>Go to Sign In</Text>
    </Link>
  );
}
