import { useEffect, useRef } from 'react';
import {
  Animated,
  Modal,
  View,
  Text,
  StyleSheet,
  Pressable,
  ScrollView,
} from 'react-native';
import { colors } from '@/src/themes/colors';
import { LinkedBankAccountDTO } from '@/src/services/linked-account.service';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faBuildingColumns, faCheck } from '@fortawesome/free-solid-svg-icons';

type Props = {
  visible: boolean;
  onClose: () => void;
  accounts: LinkedBankAccountDTO[];
  amount: number;
  onConfirm: (account: LinkedBankAccountDTO) => void;
  selectedAccountId: string | null;
  onSelectAccount: (id: string) => void;
};

export default function SelectBankAccountModal({
                                                 visible,
                                                 onClose,
                                                 accounts,
                                                 amount,
                                                 onConfirm,
                                                 selectedAccountId,
                                                 onSelectAccount,
                                               }: Props) {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const slideAnim = useRef(new Animated.Value(500)).current;

  useEffect(() => {
    if (visible) {
      fadeAnim.setValue(0);
      slideAnim.setValue(500);
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 200,
          useNativeDriver: true,
        }),
        Animated.timing(slideAnim, {
          toValue: 0,
          duration: 320,
          useNativeDriver: true,
        }),
      ]).start();
    } else {
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 0,
          duration: 180,
          useNativeDriver: true,
        }),
        Animated.timing(slideAnim, {
          toValue: 500,
          duration: 280,
          useNativeDriver: true,
        }),
      ]).start();
    }
  }, [visible]);

  const selectedAccount = accounts.find((a) => a.id === selectedAccountId);

  return (
    <Modal visible={visible} animationType="none" transparent onRequestClose={onClose}>
      <View style={styles.container}>

        {/* Dim overlay */}
        <Animated.View style={[styles.overlay, { opacity: fadeAnim }]}>
          <Pressable style={styles.overlayPressable} onPress={onClose} />
        </Animated.View>

        {/* Handle bar */}
        <Animated.View style={{ transform: [{ translateY: slideAnim }] }}>
          <Pressable style={styles.handleContainer} onPress={onClose}>
            <View style={styles.handle} />
          </Pressable>
        </Animated.View>

        {/* Sheet */}
        <Animated.View style={{ transform: [{ translateY: slideAnim }] }}>
          <View style={styles.sheet}>
            <Text style={styles.title}>Select Bank Account</Text>
            <Text style={styles.subtitle}>
              Adding ${amount.toFixed(2)} to your Transfera wallet
            </Text>

            {/* Account List */}
            <ScrollView
              style={styles.accountList}
              showsVerticalScrollIndicator={false}
            >
              {accounts.map((account) => {
                const isSelected = account.id === selectedAccountId;
                return (
                  <Pressable
                    key={account.id}
                    style={[styles.accountRow, isSelected && styles.accountRowSelected]}
                    onPress={() => onSelectAccount(account.id)}
                  >
                    {/* Bank Icon */}
                    <View style={styles.accountIcon}>
                      <FontAwesomeIcon
                        icon={faBuildingColumns}
                        size={18}
                        color={isSelected ? colors.primary : colors.subtitleText}
                      />
                    </View>

                    {/* Account Info */}
                    <View style={styles.accountInfo}>
                      <Text style={styles.accountBank}>{account.bankName}</Text>
                      <Text style={styles.accountDetails}>
                        {account.accountType.charAt(0) + account.accountType.slice(1).toLowerCase()} •••• {account.lastFourDigitsAccountNumber}
                      </Text>
                    </View>

                    {/* Check mark if selected */}
                    {isSelected && (
                      <FontAwesomeIcon icon={faCheck} size={16} color={colors.primary} />
                    )}
                  </Pressable>
                );
              })}
            </ScrollView>

            {/* Confirm Button */}
            <Pressable
              style={[styles.confirmButton, !selectedAccountId && styles.confirmButtonDisabled]}
              onPress={() => selectedAccount && onConfirm(selectedAccount)}
              disabled={!selectedAccountId}
            >
              <Text style={styles.confirmButtonText}>
                {selectedAccountId ? `Add $${amount.toFixed(2)}` : 'Select an Account'}
              </Text>
            </Pressable>
          </View>
        </Animated.View>

      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.5)',
  },
  overlayPressable: {
    flex: 1,
  },
  handleContainer: {
    alignItems: 'center',
    paddingVertical: 10,
  },
  handle: {
    width: 40,
    height: 4,
    borderRadius: 2,
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
  title: {
    fontSize: 22,
    fontWeight: '800',
    textAlign: 'center',
    color: colors.bodyText,
  },
  subtitle: {
    fontSize: 14,
    color: colors.subtitleText,
    textAlign: 'center',
  },
  accountList: {
    maxHeight: 300,
  },
  accountRow: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderRadius: 14,
    backgroundColor: colors.card,
    marginBottom: 10,
    borderWidth: 1.5,
    borderColor: 'transparent',
    gap: 12,
  },
  accountRowSelected: {
    borderColor: colors.primary,
  },
  accountIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.background,
    alignItems: 'center',
    justifyContent: 'center',
  },
  accountInfo: {
    flex: 1,
    gap: 4,
  },
  accountBank: {
    fontSize: 15,
    fontWeight: '700',
    color: colors.bodyText,
  },
  accountDetails: {
    fontSize: 13,
    color: colors.subtitleText,
  },
  confirmButton: {
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  confirmButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
  confirmButtonText: {
    fontSize: 16,
    fontWeight: '800',
    color: colors.primaryText,
  },
});