import { View, Text, Pressable, StyleSheet } from "react-native";
import { router } from "expo-router";
import { useSession } from "@/ctx";

export default function Logout() {
  const { signOut } = useSession();

  return (
    <View style={styles.container}>
      <Pressable
        style={styles.button}
        onPress={() => {
          signOut(); // clears SecureStore "session"
          router.replace("/signin"); // optional, guards will redirect anyway
        }}
      >
        <Text style={styles.text}>Sign Out</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: "center", alignItems: "center" },
  button: {
    paddingHorizontal: 18,
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: "black",
  },
  text: { color: "white", fontWeight: "800", fontSize: 16 },
});
