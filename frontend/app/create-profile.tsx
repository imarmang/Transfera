import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  View,
  Text,
  TextInput,
  Pressable,
} from "react-native";
import { useState } from "react";
import { colors } from "@/src/themes/colors";
import { useSession } from "@/src/context/AuthContext";
import { createProfileRequest } from "@/src/services/auth.service";

export default function CreateProfile() {
  const [userName, setUserName] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { session, setHasProfile, signOut } = useSession();

  async function onCreateProfile() {
    setError("");

    if (!userName || !firstName || !lastName || !phoneNumber) {
      setError("Please fill in all fields.");
      console.log("Validation failed - missing fields");
      return;
    }

    if (!session) {
      setError("No active session.");
      console.log("No session found");
      return;
    }

    console.log("Session token:", session);

    try {
      setLoading(true);
      console.log("Sending createProfileRequest...");
      await createProfileRequest(
        session,
        userName,
        firstName,
        lastName,
        phoneNumber,
      );
      console.log("Profile created successfully");
    } catch (e: any) {
      setError(e?.message ?? "Failed to create profile. Please try again.");
      return;
    } finally {
      setLoading(false);
    }
    setHasProfile(true);
  }
  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
      <ScrollView
        contentContainerStyle={styles.scroll}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.container}>
          <View style={styles.topBar}>
            <Pressable onPress={signOut}>
              <Text style={styles.logoutText}>Log out</Text>
            </Pressable>
          </View>
          <View style={styles.header}>
            <Text style={styles.brand}>Transfera</Text>
            <Text style={styles.subtitle}>Set up your profile</Text>
          </View>

          <View style={styles.card}>
            <Text style={styles.label}>Username</Text>
            <TextInput
              value={userName}
              onChangeText={setUserName}
              placeholder="johndoe"
              placeholderTextColor={colors.subtitleText}
              autoCapitalize="none"
              autoCorrect={false}
              style={styles.input}
            />
            <Text style={[styles.label, { marginTop: 12 }]}>First Name</Text>
            <TextInput
              value={firstName}
              onChangeText={setFirstName}
              placeholder="John"
              placeholderTextColor={colors.subtitleText}
              style={styles.input}
            />
            <Text style={[styles.label, { marginTop: 12 }]}>Last Name</Text>
            <TextInput
              value={lastName}
              onChangeText={setLastName}
              placeholder="Doe"
              placeholderTextColor={colors.subtitleText}
              style={styles.input}
            />
            <Text style={[styles.label, { marginTop: 12 }]}>Phone Number</Text>
            <TextInput
              value={phoneNumber}
              onChangeText={setPhoneNumber}
              placeholder="+1 555 000 0000"
              placeholderTextColor={colors.subtitleText}
              keyboardType="phone-pad"
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
              onPress={onCreateProfile}
              disabled={loading}
            >
              <Text style={styles.primaryButtonText}>
                {loading ? "Setting up..." : "Continue"}
              </Text>
            </Pressable>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  topBar: { alignItems: "flex-end", paddingTop: 16 },

  logoutText: {
    fontSize: 14,
    fontWeight: "600",
    color: colors.error,
  },
  scroll: { flexGrow: 1 },
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
  primaryButton: {
    marginTop: 16,
    height: 52,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.primary,
  },
  primaryButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
  primaryButtonText: {
    color: colors.primaryText,
    fontSize: 16,
    fontWeight: "800",
  },
});
