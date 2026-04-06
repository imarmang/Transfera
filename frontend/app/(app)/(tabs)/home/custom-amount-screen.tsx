import { useEffect, useRef, useState } from 'react';
import {
  Animated,
  View,
  Text,
  StyleSheet,
  Pressable,
  TextInput,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { router } from 'expo-router';
import { colors } from '@/src/themes/colors';
import { useCustomAmount } from '@/src/context/CustomAmountContext';
import NoLinkedAccountModal from './no-linked-account-modal';

export default function CustomAmountScreen() {
  const { setConfirmedAmount, hasLinkedAccount } = useCustomAmount();
  const [input, setInput] = useState('');
  const [showNoAccount, setShowNoAccount] = useState(false);
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const slideAnim = useRef(new Animated.Value(60)).current;
  const inputRef = useRef<TextInput>(null);

  useEffect(() => {
    inputRef.current?.focus();
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 250,
        useNativeDriver: true,
      }),
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: 280,
        useNativeDriver: true,
      }),
    ]).start();
  }, []);

  function handleConfirm() {
    const parsed = parseFloat(input);
    if (!input || isNaN(parsed) || parsed <= 0) return;
    if (!hasLinkedAccount) {
      setShowNoAccount(true);
      return;
    }
    setConfirmedAmount(input);
    router.back();
  }

  function handleClose() {
    router.back();
  }

  const displayAmount = input ? `$${input}` : '$0';
  const hasValidAmount = !!input && parseFloat(input) > 0;

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <Animated.View style={[styles.inner, { opacity: fadeAnim, transform: [{ translateY: slideAnim }] }]}>

        {/* Header */}
        <View style={styles.header}>
          <Pressable onPress={handleClose} style={styles.closeButton}>
            <Text style={styles.closeText}>✕</Text>
          </Pressable>
          <View style={styles.headerCenter}>
            <Text style={styles.headerTitle}>Add Money</Text>
            <Text style={styles.headerSubtitle}>Enter an amount</Text>
          </View>
          <View style={styles.closeButton} />
        </View>

        {/* Amount display — tapping refocuses keyboard */}
        <Pressable style={styles.amountContainer} onPress={() => inputRef.current?.focus()}>
          <Text
            style={[styles.amountText, input.length > 4 && styles.amountTextSmaller]}
            numberOfLines={1}
            adjustsFontSizeToFit
          >
            {displayAmount}
          </Text>
          <TextInput
            ref={inputRef}
            style={styles.hiddenInput}
            value={input}
            onChangeText={(text) => {
              const cleaned = text.replace(/[^0-9.]/g, '');
              const parts = cleaned.split('.');
              if (parts.length > 2) return;
              if (parts[1] && parts[1].length > 2) return;
              if (parts[0].length > 6) return;
              setInput(cleaned);
            }}
            keyboardType="decimal-pad"
            caretHidden
            blurOnSubmit={false}
            onBlur={() => inputRef.current?.focus()}
          />
        </Pressable>

        {/* Confirm button */}
        <View style={styles.confirmRow}>
          <Pressable
            style={[styles.confirmButton, !hasValidAmount && styles.confirmButtonDisabled]}
            onPress={handleConfirm}
            disabled={!hasValidAmount}
          >
            <Text style={styles.confirmButtonText}>
              {hasValidAmount ? `Add ${displayAmount}` : 'Add'}
            </Text>
          </Pressable>
        </View>

        {/* No linked account prompt */}
        <NoLinkedAccountModal
          visible={showNoAccount}
          onClose={() => setShowNoAccount(false)}
          onCloseParent={handleClose}
        />

      </Animated.View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  inner: { flex: 1 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 16,
  },
  closeButton: { width: 40, height: 40, alignItems: 'center', justifyContent: 'center' },
  closeText: { fontSize: 18, color: colors.subtitleText, fontWeight: '600' },
  headerCenter: { flex: 1, alignItems: 'center' },
  headerTitle: { fontSize: 17, fontWeight: '700', color: colors.bodyText },
  headerSubtitle: { fontSize: 13, color: colors.subtitleText, marginTop: 2 },
  amountContainer: {
    flex: 1, alignItems: 'center', justifyContent: 'center', paddingHorizontal: 32,
  },
  amountText: {
    fontSize: 72, fontWeight: '800', color: colors.bodyText, letterSpacing: -2,
  },
  amountTextSmaller: { fontSize: 52 },
  hiddenInput: { position: 'absolute', opacity: 0, width: 1, height: 1 },
  confirmRow: { paddingHorizontal: 24, paddingBottom: 16 },
  confirmButton: {
    height: 54, borderRadius: 14,
    backgroundColor: colors.primary,
    alignItems: 'center', justifyContent: 'center',
  },
  confirmButtonDisabled: { backgroundColor: colors.primaryDisabled },
  confirmButtonText: { fontSize: 17, fontWeight: '800', color: colors.primaryText },
});