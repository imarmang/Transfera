import {
  KeyboardAvoidingView,
  Pressable,
  Text,
  TextInput,
  View,
  StyleSheet,
} from "react-native";
import { useState } from "react";

export default function Index() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      // behavior={Platform.OS === "ios" ? "padding" : undefined}
    >
      <View>
        <View>
          <Text>Transfera</Text>
          <Text>Sign In to Continue</Text>
        </View>

        <View>
          <Text style={styles.label}>Email</Text>
          <TextInput
            value={email}
            onChangeText={setEmail}
            placeholder="you@example.com"
            placeholderTextColor="black"
            keyboardType="email-address"
            style={styles.input}
          />

          <Text>Password</Text>
          <TextInput
            value={password}
            onChangeText={setPassword}
            placeholder="••••••••"
            placeholderTextColor="black"
            secureTextEntry
            style={styles.input}
          />
        </View>

        <Pressable>
          <Text>Sign In</Text>
        </Pressable>
        <Pressable
          onPress={() => console.log("Forgot your password")}
        ></Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  input: {
    height: 48,
    borderRadius: 12,
    paddingHorizontal: 12,
    backgroundColor: "white",
    fontSize: 16,
  },

  label: { fontSize: 14, fontWeight: "700" },
});
