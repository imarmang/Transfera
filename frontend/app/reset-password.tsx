import {
  KeyboardAvoidingView,
  Pressable,
  Text,
  TextInput,
  View,
  StyleSheet,
  Platform,
} from 'react-native';
import { useState } from 'react';
import { router, Stack } from 'expo-router';
import { colors } from '@/src/themes/colors';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faChevronLeft, faLock, faLockOpen } from '@fortawesome/free-solid-svg-icons';

export default function ResetPassword() {
  const [newPassword, setNewPassword] = useState( '' );
  const [confirmPassword, setConfirmPassword] = useState( '' );
  const [showNew, setShowNew] = useState( false );
  const [showConfirm, setShowConfirm] = useState( false );
  const [error, setError] = useState( '' );
  const [loading, setLoading] = useState( false );

  function handleReset() {
    setError( '' );
    if ( !newPassword || !confirmPassword ) {
      setError( 'Please fill in all fields.' );
      return;
    }
    if ( newPassword !== confirmPassword ) {
      setError( 'Passwords do not match.' );
      return;
    }
    // TODO: wire up API call
    console.log( 'Reset password with:', newPassword );
  }

  return (
    <>
      <Stack.Screen options={{ headerShown: false }} />
      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <View style={styles.container}>

          {/* Header */}
          <View style={styles.header}>
            <Text style={styles.brand}>Transfera</Text>
            <Text style={styles.subtitle}>Reset your password</Text>
          </View>

          {/* Card */}
          <View style={styles.card}>

            <Text style={styles.label}>New Password</Text>
            <View style={styles.passwordContainer}>
              <TextInput
                value={newPassword}
                onChangeText={setNewPassword}
                placeholder="••••••••"
                placeholderTextColor={colors.subtitleText}
                secureTextEntry={!showNew}
                autoCapitalize="none"
                autoCorrect={false}
                textContentType="oneTimeCode"
                style={styles.passwordInput}
              />
              <Pressable onPress={() => setShowNew( !showNew )} style={styles.eyeButton}>
                <FontAwesomeIcon
                  icon={showNew ? faLockOpen : faLock}
                  size={18}
                  color={colors.subtitleText}
                />
              </Pressable>
            </View>

            <Text style={[styles.label, { marginTop: 12 }]}>Confirm Password</Text>
            <View style={styles.passwordContainer}>
              <TextInput
                value={confirmPassword}
                onChangeText={setConfirmPassword}
                placeholder="••••••••"
                placeholderTextColor={colors.subtitleText}
                secureTextEntry={!showConfirm}
                autoCapitalize="none"
                autoCorrect={false}
                textContentType="oneTimeCode"
                style={styles.passwordInput}
              />
              <Pressable onPress={() => setShowConfirm( !showConfirm )} style={styles.eyeButton}>
                <FontAwesomeIcon
                  icon={showConfirm ? faLockOpen : faLock}
                  size={18}
                  color={colors.subtitleText}
                />
              </Pressable>
            </View>

            {/* Error */}
            {!!error && (
              <View style={styles.errorBox}>
                <Text style={styles.errorText}>{error}</Text>
              </View>
            )}

            {/* Button */}
            <Pressable
              style={[styles.primaryButton, loading && styles.primaryButtonDisabled]}
              onPress={handleReset}
              disabled={loading}
            >
              <Text style={styles.primaryButtonText}>
                {loading ? 'Resetting...' : 'Reset Password'}
              </Text>
            </Pressable>

            {/* Back to sign in */}
            <Pressable onPress={() => router.replace( '/signin' )} style={styles.linkButton}>
              <Text style={styles.linkText}>Back to Sign In</Text>
            </Pressable>

          </View>
        </View>
      </KeyboardAvoidingView>
    </>
  );
}

const styles = StyleSheet.create( {
  container: {
    flex: 1,
    padding: 20,
    justifyContent: 'center',
    gap: 16,
    backgroundColor: colors.background,
  },
  header: {
    alignItems: 'center',
    gap: 6,
  },
  brand: {
    fontSize: 36,
    fontWeight: '800',
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
    fontWeight: '700',
  },
  passwordContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.input,
    borderRadius: 12,
    marginTop: 6,
  },
  passwordInput: {
    flex: 1,
    height: 48,
    paddingHorizontal: 12,
    fontSize: 16,
  },
  eyeButton: {
    paddingHorizontal: 12,
    height: 48,
    alignItems: 'center',
    justifyContent: 'center',
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
  primaryButtonText: {
    color: colors.primaryText,
    fontSize: 16,
    fontWeight: '800',
  },
  linkButton: {
    paddingVertical: 12,
    alignItems: 'center',
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
    fontWeight: '600',
  },
} );