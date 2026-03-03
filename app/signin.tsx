import {
  KeyboardAvoidingView,
  Pressable,
  Text,
  TextInput,
  View,
  StyleSheet,
  Platform,
} from "react-native";
import { useState } from "react";
import { router } from "expo-router";
import { useSession } from "@/src/context/AuthContext"; // <-- from the guide

export default function SignIn() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const { signIn } = useSession();

  async function onSignIn() {
    try {
      await signIn(email, password); // pass credentials to AuthContext.tsx
      router.replace("/");
    } catch (e) {
      alert("Login failed. Check your email and password.");
    }
  }

  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.brand}>Transfera</Text>
          <Text style={styles.subtitle}>Sign In to Continue</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.label}>Email</Text>
          <TextInput
            value={email}
            onChangeText={setEmail}
            placeholder="you@example.com"
            placeholderTextColor="black"
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
            style={styles.input}
          />

          <Text style={[styles.label, { marginTop: 12 }]}>Password</Text>
          <TextInput
            value={password}
            onChangeText={setPassword}
            placeholder="••••••••"
            placeholderTextColor="black"
            secureTextEntry
            autoCapitalize="none"
            autoCorrect={false}
            style={styles.input}
          />

          <Pressable style={styles.primaryButton} onPress={onSignIn}>
            <Text style={styles.primaryButtonText}>Sign In</Text>
          </Pressable>

          <Pressable
            onPress={() => console.log("Forgot your password")}
            style={styles.linkButton}
          >
            <Text style={styles.linkText}>Forgot your password?</Text>
          </Pressable>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    justifyContent: "center",
    gap: 16,
  },
  header: {
    alignItems: "center",
    gap: 6,
  },
  brand: {
    fontSize: 36,
    fontWeight: "800",
  },
  subtitle: {
    fontSize: 16,
    color: "grey",
  },
  card: {
    borderRadius: 18,
    padding: 16,
    backgroundColor: "#F2F2F2",
  },
  label: {
    fontSize: 14,
    fontWeight: "700",
  },
  input: {
    height: 48,
    borderRadius: 12,
    paddingHorizontal: 12,
    backgroundColor: "white",
    fontSize: 16,
    marginTop: 6,
  },
  primaryButton: {
    marginTop: 16,
    height: 52,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "black",
  },
  primaryButtonText: {
    color: "white",
    fontSize: 16,
    fontWeight: "800",
  },
  linkButton: {
    paddingVertical: 12,
    alignItems: "center",
  },
  linkText: {
    fontSize: 14,
    color: "black",
  },
});
