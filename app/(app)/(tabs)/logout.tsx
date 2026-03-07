import { View, Text, Pressable, StyleSheet } from "react-native";
import { router } from "expo-router";
import { useSession } from "@/src/context/AuthContext";

export default function Logout() {
  const { signOut, session } = useSession();

  return (
    <View style={styles.container}>
      <Pressable
        style={styles.button}
        onPress={async () => {
          console.log("[Logout] Button pressed");
          console.log("[Logout] session exists?", !!session);
          console.log("[Logout] token prefix:", session?.slice(0, 20));

          try {
            console.log("[Logout] calling signOut()...");
            await signOut();
            console.log("[Logout] signOut() finished ✅");
          } catch (e) {
            console.log("[Logout] signOut() failed ❌", e);
          }

          console.log("[Logout] navigating to /signin");
          router.replace("/signin");
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
