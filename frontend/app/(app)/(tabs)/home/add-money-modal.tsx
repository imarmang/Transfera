import { useEffect, useRef, useState } from 'react';
import {
  Animated,
  Modal,
  View,
  Text,
  StyleSheet,
  Pressable,
} from 'react-native';
import { router } from 'expo-router';
import { colors } from '@/src/themes/colors';
import { useCustomAmount } from '@/src/context/CustomAmountContext';
import NoLinkedAccountModal from './no-linked-account-modal';

type Props = {
  visible: boolean;
  onClose: () => void;
  onContinue: (amount: number) => void;
};

const SHEET_SLIDE_DURATION = 320;
const OVERLAY_FADE_DURATION = 200;
const OVERLAY_DELAY = 180;

export default function AddMoneyModal({ visible, onClose, onContinue }: Props) {
  const [error, setError] = useState('');
  const [amount, setAmount] = useState('');
  const [isCustomAmount, setIsCustomAmount] = useState(false);
  const [modalMounted, setModalMounted] = useState(false);
  const [showNoAccount, setShowNoAccount] = useState(false);

  const { confirmedAmount, setConfirmedAmount, hasLinkedAccount } = useCustomAmount();

  const slideAnim = useRef(new Animated.Value(500)).current;
  const overlayOpacity = useRef(new Animated.Value(0)).current;

  const QUICK_AMOUNTS = [10, 25, 50, 100, 200];

  // ─── Animation Effect ─────────────────────────────────────────────────────────
  useEffect(() => {
    if (visible) {
      slideAnim.setValue(500);
      overlayOpacity.setValue(0);
      setModalMounted(true);
      Animated.parallel([
        Animated.timing(slideAnim, {
          toValue: 0,
          duration: SHEET_SLIDE_DURATION,
          useNativeDriver: true,
        }),
        Animated.sequence([
          Animated.delay(OVERLAY_DELAY),
          Animated.timing(overlayOpacity, {
            toValue: 1,
            duration: OVERLAY_FADE_DURATION,
            useNativeDriver: true,
          }),
        ]),
      ]).start();
    } else {
      Animated.parallel([
        Animated.timing(overlayOpacity, {
          toValue: 0,
          duration: OVERLAY_FADE_DURATION,
          useNativeDriver: true,
        }),
        Animated.sequence([
          Animated.delay(100),
          Animated.timing(slideAnim, {
            toValue: 500,
            duration: SHEET_SLIDE_DURATION,
            useNativeDriver: true,
          }),
        ]),
      ]).start(() => setModalMounted(false));
    }
  }, [visible]);

  // ─── Custom Amount Effect ─────────────────────────────────────────────────────
  // Picks up the confirmed amount from CustomAmountScreen via context
  useEffect(() => {
    if (confirmedAmount !== null) {
      setAmount(confirmedAmount);
      setIsCustomAmount(true);
      setConfirmedAmount(null);
    }
  }, [confirmedAmount]);

  // ─── Handlers ────────────────────────────────────────────────────────────────

  function handleClose() {
    setAmount('');
    setError('');
    setIsCustomAmount(false);
    onClose();
  }

  function handleQuickSelect(value: number) {
    setAmount(String(value));
    setError('');
    setIsCustomAmount(false);
  }

  function handleCustomPress() {
    // Navigate immediately, close modal in background simultaneously
    router.push('/home/custom-amount-screen');
    onClose();
  }

  function handleContinue() {
    const parsed = parseFloat(amount);
    if (!amount || isNaN(parsed) || parsed <= 0) {
      setError('Please enter a valid amount.');
      return;
    }
    // Show no linked account prompt if needed
    if (!hasLinkedAccount) {
      setShowNoAccount(true);
      return;
    }
    onContinue(parsed);
  }

  // ─── Render ──────────────────────────────────────────────────────────────────
  return (
    <Modal visible={modalMounted} animationType="none" transparent onRequestClose={handleClose}>
      <View style={styles.container}>

        {/* Dim overlay */}
        <Animated.View style={[styles.overlay, { opacity: overlayOpacity }]}>
          <Pressable style={StyleSheet.absoluteFill} onPress={handleClose} />
        </Animated.View>

        {/* Handle bar */}
        <Animated.View style={{ transform: [{ translateY: slideAnim }] }}>
          <Pressable style={styles.handleContainer} onPress={handleClose}>
            <View style={styles.handle} />
          </Pressable>
        </Animated.View>

        {/* Sheet */}
        <Animated.View style={{ transform: [{ translateY: slideAnim }] }}>
          <View style={styles.sheet}>
            <Text style={styles.title}>Add Money</Text>
            <Text style={styles.subtitle}>Select or Enter an Amount</Text>

            {/* Quick amount buttons + custom amount button */}
            <View style={styles.quickAmounts}>
              {QUICK_AMOUNTS.map((value) => (
                <Pressable
                  key={value}
                  style={[
                    styles.quickButton,
                    amount === String(value) && !isCustomAmount && styles.quickButtonSelected,
                  ]}
                  onPress={() => handleQuickSelect(value)}
                >
                  <Text
                    style={[
                      styles.quickButtonText,
                      amount === String(value) && !isCustomAmount && styles.quickButtonTextSelected,
                    ]}
                  >
                    ${value}
                  </Text>
                </Pressable>
              ))}

              {/* ••• button — shows entered amount once confirmed */}
              <Pressable
                style={[styles.quickButton, isCustomAmount && styles.quickButtonSelected]}
                onPress={handleCustomPress}
              >
                <Text style={[styles.quickButtonText, isCustomAmount && styles.quickButtonTextSelected]}>
                  {isCustomAmount ? `$${amount}` : '•••'}
                </Text>
              </Pressable>
            </View>

            {/* Validation error */}
            {!!error && <Text style={styles.error}>{error}</Text>}

            {/* Confirm button — disabled until an amount is selected */}
            <Pressable
              style={[styles.addButton, !amount && styles.addButtonDisabled]}
              onPress={handleContinue}
              disabled={!amount}
            >
              <Text style={styles.addButtonText}>
                {amount ? `Add $${parseFloat(amount) || '0'}` : 'Add'}
              </Text>
            </Pressable>
          </View>
        </Animated.View>

        {/* No linked account prompt — appears on top of the sheet */}
        <NoLinkedAccountModal
          visible={showNoAccount}
          onClose={() => setShowNoAccount(false)}
          onCloseParent={handleClose}
        />

      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'flex-end' },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  handleContainer: { alignItems: 'center', paddingVertical: 10 },
  handle: {
    width: 40, height: 4, borderRadius: 2,
    backgroundColor: 'rgba(255,255,255,0.6)',
  },
  sheet: {
    backgroundColor: colors.background,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    padding: 24,
    paddingBottom: 48,
    gap: 16,
  },
  title: { fontSize: 22, fontWeight: '800', textAlign: 'center', color: colors.bodyText },
  subtitle: { fontSize: 14, color: colors.subtitleText, textAlign: 'center' },
  quickAmounts: { flexDirection: 'row', flexWrap: 'wrap', gap: 10, justifyContent: 'center' },
  quickButton: {
    width: '30%', height: 52, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
    backgroundColor: colors.card, borderWidth: 1.5, borderColor: 'transparent',
  },
  quickButtonSelected: { borderColor: colors.primary },
  quickButtonText: { fontSize: 16, fontWeight: '600', color: colors.bodyText },
  quickButtonTextSelected: { color: colors.primary },
  error: { fontSize: 13, color: '#E53E3E', textAlign: 'center', marginTop: -8 },
  addButton: {
    height: 52, borderRadius: 14,
    alignItems: 'center', justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  addButtonDisabled: { backgroundColor: colors.primaryDisabled },
  addButtonText: { fontSize: 16, fontWeight: '800', color: colors.primaryText },
});