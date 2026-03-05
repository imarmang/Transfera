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
import { useSession } from "@/src/context/AuthContext";
import { colors } from "@/src/themes/colors"; // <-- from the guide

export default function SignIn() {
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const { signIn } = useSession();

  // async function onSignIn() {
  //   setError("");
  //
  //   if (!email || !password) {
  //     setError("Please enter your email and password");
  //     return;
  //   }
  //   try {
  //     setLoading(true);
  //     await signIn(email, password); // pass credentials to AuthContext.tsx
  //     router.replace("/");
  //   } catch (e) {
  //     setError("Login failed. Check your email and password.");
  //   } finally {
  //     setLoading(false);
  //   }
  // }
  async function onSignIn() {
    setError("");
    console.log("onSignIn triggered");
    console.log("Fields:", { email, password: password ? "***" : "empty" });

    if (!email || !password) {
      setError("Please enter your email and password");
      console.log("Validation failed - missing fields");
      return;
    }

    try {
      setLoading(true);
      console.log("Calling signIn...");
      await signIn(email, password);
      console.log("signIn successful, navigating to /register");
      router.replace("/");
    } catch (e) {
      console.log("signIn error:", e);
      setError("Login failed. Check your email and password.");
    } finally {
      setLoading(false);
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

          {error ? (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : null}
          <Pressable
            style={[
              styles.primaryButton,
              loading && styles.primaryButtonDisabled,
            ]}
            onPress={onSignIn}
            disabled={loading}
          >
            <Text style={styles.primaryButtonText}>
              {loading ? "Signing in..." : "Sign in"}
            </Text>
          </Pressable>

          <Pressable
            onPress={() => setError("Feature not yet implemented")}
            style={styles.linkButton}
          >
            <Text style={styles.linkText}>Forgot your password?</Text>
          </Pressable>
        </View>

        <View style={styles.divider}>
          <View style={styles.dividerLine} />
          <Text style={styles.dividerText}>or</Text>
          <View style={styles.dividerLine} />
        </View>

        <Pressable
          onPress={() => setError("Google Sign in is not yet implemented.")}
          style={styles.googleButton}
        >
          <Text style={styles.googleButtonText}>🇬Sign in with Google</Text>
        </Pressable>
        <Pressable
          onPress={() => router.replace("/register")}
          style={styles.registerButton}
        >
          <Text style={styles.registerText}>
            {"Don't have an account?"}{" "}
            <Text style={styles.registerTextBold}>Create One</Text>
          </Text>
        </Pressable>
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
    backgroundColor: colors.background,
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
    color: colors.subtitleText,
  },
  card: {
    borderRadius: 18,
    padding: 16,
    backgroundColor: colors.card,
  },
  label: {
    fontSize: 14,
    fontWeight: "700",
  },
  input: {
    height: 48,
    borderRadius: 12,
    paddingHorizontal: 12,
    backgroundColor: colors.input,
    fontSize: 16,
    marginTop: 6,
  },
  primaryButton: {
    marginTop: 16,
    height: 52,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.primary,
  },
  primaryButtonDisabled: { backgroundColor: colors.primaryDisabled },
  primaryButtonText: {
    color: colors.primaryText,
    fontSize: 16,
    fontWeight: "800",
  },
  linkButton: {
    paddingVertical: 12,
    alignItems: "center",
  },
  linkText: {
    fontSize: 14,
    color: colors.bodyText,
  },
  errorBox: {
    marginTop: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: {
    color: colors.error,
    fontSize: 14,
    fontWeight: "600",
  },

  registerButton: {
    alignItems: "center",
    paddingVertical: 8,
  },
  registerText: {
    fontSize: 14,
    color: colors.bodyText,
  },
  registerTextBold: {
    fontWeight: "800",
  },
  divider: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginTop: 12,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: colors.input,
  },
  dividerText: {
    fontSize: 13,
    color: colors.bodyText,
  },
  googleButton: {
    height: 52,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.input,
    marginTop: 8,
  },
  googleButtonText: {
    fontSize: 16,
    fontWeight: "600",
  },
});
