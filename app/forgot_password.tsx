import { colors } from '@/src/themes/colors';
import {
  KeyboardAvoidingView,
  View,
  Text,
  Platform,
  StyleSheet,
  TextInput,
  Pressable,
} from 'react-native';
import { useState } from 'react';
import { router } from 'expo-router';

export default function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function onReset() {
    setError('');
    if (!email) {
      setError('Please enter your email address');
      return;
    }
    try {
      setLoading(true);
      setSent(true);
    } catch {
      setError('Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <View style={styles.container}>
        <View style={styles.header}>
          <Text style={styles.brand}>Transfera</Text>
          <Text style={styles.subtitle}>Reset your password</Text>
        </View>

        <View style={styles.card}>
          {sent ? (
            <View style={styles.sentBox}>
              <Text style={styles.sentTitle}>Check your email</Text>
              <Text style={styles.sentText}>We sent a password reset link to {email}</Text>
            </View>
          ) : (
            <>
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

              {error ? (
                <View style={styles.errorBox}>
                  <Text style={styles.errorText}>{error}</Text>
                </View>
              ) : null}

              <Pressable
                style={[styles.primaryButton, loading && styles.primaryButtonDisabled]}
                onPress={onReset}
                disabled={loading}
              >
                <Text style={styles.primaryButtonText}>
                  {loading ? 'Sending...' : 'Send Reset Link'}
                </Text>
              </Pressable>
            </>
          )}
        </View>

        <Pressable onPress={() => router.back()} style={styles.backButton}>
          <Text style={styles.backText}>← Back to Sign In</Text>
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    justifyContent: 'center',
    gap: 16,
    backgroundColor: colors.background,
  },
  header: { alignItems: 'center', gap: 6 },
  brand: { fontSize: 36, fontWeight: '800' },
  subtitle: { fontSize: 16, color: colors.subtitleText },

  card: { borderRadius: 18, padding: 16, backgroundColor: colors.card },
  label: { fontSize: 14, fontWeight: '700' },
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
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  primaryButtonDisabled: { backgroundColor: colors.primaryDisabled },
  primaryButtonText: { color: colors.primaryText, fontSize: 16, fontWeight: '800' },
  errorBox: {
    marginTop: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: { color: colors.error, fontSize: 14, fontWeight: '600' },
  sentBox: { alignItems: 'center', gap: 8, paddingVertical: 12 },
  sentTitle: { fontSize: 20, fontWeight: '800' },
  sentText: { fontSize: 14, color: colors.subtitleText, textAlign: 'center' },
  backButton: { alignItems: 'center', paddingVertical: 8 },
  backText: { fontSize: 14, color: colors.bodyText },
});
