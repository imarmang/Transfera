import { useEffect, useRef } from 'react';
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
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faBuildingColumns } from '@fortawesome/free-solid-svg-icons';

type Props = {
  visible: boolean;
  onClose: () => void;
  onCloseParent: () => void; // closes the parent modal (AddMoneyModal or CustomAmountScreen)
};

export default function NoLinkedAccountModal({ visible, onClose, onCloseParent }: Props) {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.92)).current;

  useEffect(() => {
    if (visible) {
      fadeAnim.setValue(0);
      scaleAnim.setValue(0.92);
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 220,
          useNativeDriver: true,
        }),
        Animated.spring(scaleAnim, {
          toValue: 1,
          useNativeDriver: true,
        }),
      ]).start();
    } else {
      Animated.timing(fadeAnim, {
        toValue: 0,
        duration: 180,
        useNativeDriver: true,
      }).start();
    }
  }, [visible]);

  function handleContinue() {
    onClose();
    onCloseParent();
    router.push('/profile/linked-bank-accounts');
  }

  return (
    <Modal visible={visible} animationType="none" transparent onRequestClose={onClose}>

      {/* Dim overlay */}
      <Animated.View style={[styles.overlay, { opacity: fadeAnim }]}>

        {/* Card */}
        <Animated.View style={[styles.card, { opacity: fadeAnim, transform: [{ scale: scaleAnim }] }]}>

          {/* Icon */}
          <View style={styles.iconContainer}>
            <FontAwesomeIcon icon={faBuildingColumns} size={28} color={colors.primary} />
          </View>

          {/* Text */}
          <Text style={styles.title}>No Bank Account Linked</Text>
          <Text style={styles.subtitle}>
            To add money instantly, link a bank account.
          </Text>

          {/* Buttons */}
          <View style={styles.buttons}>
            <Pressable style={styles.backButton} onPress={onClose}>
              <Text style={styles.backButtonText}>Back</Text>
            </Pressable>
            <Pressable style={styles.continueButton} onPress={handleContinue}>
              <Text style={styles.continueButtonText}>Continue</Text>
            </Pressable>
          </View>

        </Animated.View>
      </Animated.View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.6)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  card: {
    backgroundColor: colors.background,
    borderRadius: 24,
    padding: 28,
    width: '100%',
    alignItems: 'center',
    gap: 12,
  },
  iconContainer: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 4,
  },
  title: { fontSize: 18, fontWeight: '800', color: colors.bodyText, textAlign: 'center' },
  subtitle: { fontSize: 14, color: colors.subtitleText, textAlign: 'center', lineHeight: 20 },
  buttons: { flexDirection: 'row', gap: 12, marginTop: 8, width: '100%' },
  backButton: {
    flex: 1, height: 48, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
    backgroundColor: colors.card,
  },
  backButtonText: { fontSize: 15, fontWeight: '700', color: colors.bodyText },
  continueButton: {
    flex: 1, height: 48, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  continueButtonText: { fontSize: 15, fontWeight: '700', color: colors.primaryText },
});