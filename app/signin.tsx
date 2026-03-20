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
import { router } from 'expo-router';
import { useSession } from '@/src/context/AuthContext';
import { colors } from '@/src/themes/colors';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faLock, faLockOpen } from '@fortawesome/free-solid-svg-icons';
import { GoogleSignin } from '@react-native-google-signin/google-signin';
import { useEffect } from 'react';
import Svg, { Path } from 'react-native-svg';

export default function SignIn() {
  const [error, setError] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [loadingEmail, setLoadingEmail] = useState(false);
  const [loadingGoogle, setLoadingGoogle] = useState(false);

  const { signIn, signInWithGoogle } = useSession();

  useEffect(() => {
    GoogleSignin.configure({
      webClientId: process.env.EXPO_PUBLIC_GOOGLE_WEB_CLIENT_ID,
      iosClientId: process.env.EXPO_PUBLIC_GOOGLE_IOS_CLIENT_ID,
    });
  }, []);

  async function onSignIn() {
    setError('');
    console.log('onSignIn triggered');
    console.log('Fields:', { email, password: password ? '***' : 'empty' });

    if (!email || !password) {
      setError('Please enter your email and password');
      console.log('Validation failed - missing fields');
      return;
    }

    try {
      setLoadingEmail(true);
      console.log('Calling signIn...');
      await signIn(email, password);
      console.log('signIn successful, navigating to /register');
      router.replace('/');
    } catch (e) {
      console.log('signIn error:', e);
      setError('Login failed. Check your email and password.');
    } finally {
      setLoadingEmail(false);
    }
  }

  async function onGoogleSignIn() {
    setError('');

    try {
      setLoadingGoogle(true);
      console.log('Calling the onGoogleSignIn...');

      // Disables the button and shows a loading state so the user knows something is happening.
      await GoogleSignin.hasPlayServices();
      // Checks that Google Play Services are available on the device. Required by the library even on iOS.
      const userInfo = await GoogleSignin.signIn();

      const idToken = userInfo.data?.idToken;

      if (!idToken) throw new Error('No ID token received from Google');
      await signInWithGoogle(idToken);
      router.replace('/');
    } catch (e: any) {
      console.log('signInWithGoogle error:', e);
      if (e.message?.includes('User not found')) {
        setError('No account found. Please create one below.');
      } else {
        setError('Login failed. Check your network.');
      }
    } finally {
      setLoadingGoogle(false);
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
          <Text style={styles.subtitle}>Sign In to Continue</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.label}>Email</Text>
          <TextInput
            value={email}
            onChangeText={setEmail}
            placeholder="you@example.com"
            placeholderTextColor={colors.subtitleText}
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
            style={styles.input}
          />

          <Text style={[styles.label, { marginTop: 12 }]}>Password</Text>
          <View style={styles.passwordContainer}>
            <TextInput
              value={password}
              onChangeText={setPassword}
              placeholder="••••••••"
              placeholderTextColor={colors.subtitleText}
              secureTextEntry={!showPassword}
              autoCapitalize="none"
              autoCorrect={false}
              textContentType="oneTimeCode"
              style={styles.passwordInput}
            />
            <Pressable onPress={() => setShowPassword(!showPassword)} style={styles.eyeButton}>
              <FontAwesomeIcon
                icon={showPassword ? faLockOpen : faLock}
                size={18}
                color={colors.subtitleText}
              />
            </Pressable>
          </View>
          {error ? (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : null}
          <Pressable
            style={[styles.primaryButton, loadingEmail && styles.primaryButtonDisabled]}
            onPress={onSignIn}
            disabled={loadingEmail}
          >
            <Text style={styles.primaryButtonText}>
              {loadingEmail ? 'Signing in...' : 'Sign in'}
            </Text>
          </Pressable>

          <Pressable onPress={() => router.push('/forgot_password')} style={styles.linkButton}>
            <Text style={styles.linkText}>Forgot your password?</Text>
          </Pressable>
        </View>

        <View style={styles.divider}>
          <View style={styles.dividerLine} />
          <Text style={styles.dividerText}>or</Text>
          <View style={styles.dividerLine} />
        </View>

        <Pressable
          onPress={onGoogleSignIn}
          disabled={loadingGoogle}
          style={({ pressed }) => [
            styles.googleButton,
            pressed && styles.googleButtonPressed,
            loadingGoogle && styles.googleButtonDisabled,
          ]}
        >
          <View style={styles.googleButtonContent}>
            <Svg width={24} height={24} viewBox="0 0 48 48">
              <Path
                fill="#EA4335"
                d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
              />
              <Path
                fill="#4285F4"
                d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
              />
              <Path
                fill="#FBBC05"
                d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
              />
              <Path
                fill="#34A853"
                d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
              />
              <Path fill="none" d="M0 0h48v48H0z" />
            </Svg>
            <Text style={styles.googleButtonText}>
              {loadingGoogle ? 'Signing in...' : 'Continue with Google'}
            </Text>
          </View>
        </Pressable>
        <Pressable onPress={() => router.replace('/register')} style={styles.registerButton}>
          <Text style={styles.registerText}>
            {"Don't have an account?"} <Text style={styles.registerTextBold}>Create One</Text>
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

  registerButton: {
    alignItems: 'center',
    paddingVertical: 8,
  },
  registerText: {
    fontSize: 14,
    color: colors.bodyText,
  },
  registerTextBold: {
    fontWeight: '800',
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
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
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.input,
    marginTop: 8,
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
  googleButtonPressed: {
    backgroundColor: '#F5F5F5',
  },
  googleButtonDisabled: {
    opacity: 0.6,
  },
  googleButtonContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
  },
  googleButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: 'black',
    textAlign: 'center',
  },
});
