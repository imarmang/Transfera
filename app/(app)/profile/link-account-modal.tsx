import {
  View,
  StyleSheet,
  Pressable,
  Text,
  Modal,
  KeyboardAvoidingView,
  Platform,
  TextInput,
  ScrollView,
} from 'react-native';
import { colors } from '@/src/themes/colors';
import { useState } from 'react';
import { useSession } from '@/src/context/AuthContext';
import { createLinkedAccountRequest } from '@/src/services/linked-account.service';

type Props = {
  visible: boolean;
  onClose: () => void;
  onSuccess: (account: any) => void;
};

export default function LinkBankAccountModal({ visible, onClose, onSuccess }: Props) {
  const bankOptions = [
    'Chase',
    'Bank of America',
    'American Express',
    'Wells Fargo',
    'Citi Bank',
    'Capital One',
    'PNC Bank',
    'Goldman Sachs',
    'Truist Bank',
    'Bank of New York Mellon',
    'Other',
  ];

  const [showBankOptions, setShowBankOptions] = useState(false);
  const [isCustomBank, setIsCustomBank] = useState(false);
  const [bankName, setBankName] = useState('');
  const [accountHolderName, setAccountHolderName] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [routingNumber, setRoutingNumber] = useState('');
  const [accountType, setAccountType] = useState('CHECKING');

  const [error, setError] = useState('');

  const { session } = useSession();
  const [loading, setLoading] = useState(false);

  async function onLinkAccount() {
    setError('');

    if (!bankName) {
      setError('Please select a bank.');
      return;
    }

    if (!accountHolderName.trim()) {
      setError('Please enter the account holder name.');
      return;
    }

    if (!accountNumber.trim() || accountNumber.length < 8) {
      setError('Please enter a valid account number.');
      return;
    }

    if (!routingNumber.trim() || routingNumber.length !== 9) {
      setError('Routing number must be 9 digits.');
      return;
    }

    try {
      setLoading(true);
      const newAccount = await createLinkedAccountRequest(session!, {
        bankName,
        accountHolderName,
        accountNumber,
        routingNumber,
        accountType,
      });
      onSuccess(newAccount);
      resetForm();
    } catch (e: any) {
      setError(e.message ?? 'Failed to link account.');
    } finally {
      setLoading(false);
    }
  }

  function resetForm() {
    setShowBankOptions(false);
    setIsCustomBank(false);
    setBankName('');
    setAccountHolderName('');
    setAccountNumber('');
    setRoutingNumber('');
    setAccountType('CHECKING');
  }

  function handleClose() {
    resetForm();
    onClose();
  }

  return (
    <Modal visible={visible} animationType="slide" onRequestClose={handleClose}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        style={styles.container}
      >
        <ScrollView contentContainerStyle={styles.content}>
          {/* X Button */}
          <Pressable style={styles.closeButton} onPress={handleClose}>
            <Text style={styles.closeText}>✕</Text>
          </Pressable>

          <Text style={styles.title}>Link a Bank Account</Text>
          <Text style={styles.subtitle}>
            Connect your bank to add money to your Transfera wallet
          </Text>

          {/* Bank Name Dropdown */}
          <Pressable
            style={styles.dropDownButton}
            onPress={() => setShowBankOptions(!showBankOptions)}
          >
            <Text
              style={[
                styles.dropdownText,
                !bankName && !isCustomBank && { color: colors.subtitleText },
              ]}
            >
              {isCustomBank ? 'Other' : bankName || 'Bank Name'}
            </Text>
            <Text style={styles.dropdownChevron}>{showBankOptions ? '▲' : '▼'}</Text>
          </Pressable>

          {showBankOptions && (
            <View style={styles.dropdownList}>
              {bankOptions.map((option) => (
                <Pressable
                  key={option}
                  style={styles.dropdownItem}
                  onPress={() => {
                    if (option === 'Other') {
                      setIsCustomBank(true);
                      setBankName('');
                    } else {
                      setIsCustomBank(false);
                      setBankName(option);
                    }
                    setShowBankOptions(false);
                  }}
                >
                  <Text style={styles.dropdownItemText}>{option}</Text>
                </Pressable>
              ))}
            </View>
          )}

          {isCustomBank && (
            <TextInput
              style={styles.input}
              placeholder="Enter your bank name"
              placeholderTextColor={colors.subtitleText}
              value={bankName}
              onChangeText={setBankName}
            />
          )}

          <TextInput
            style={styles.input}
            placeholder="Account Holder Name"
            placeholderTextColor={colors.subtitleText}
            value={accountHolderName}
            onChangeText={setAccountHolderName}
          />
          <TextInput
            style={styles.input}
            placeholder="Account Number"
            placeholderTextColor={colors.subtitleText}
            value={accountNumber}
            onChangeText={setAccountNumber}
            keyboardType="numeric"
          />
          <TextInput
            style={styles.input}
            placeholder="Routing Number"
            placeholderTextColor={colors.subtitleText}
            value={routingNumber}
            onChangeText={setRoutingNumber}
            keyboardType="numeric"
          />
          {error ? (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : null}
          {/* Account Type Toggle */}
          <View style={styles.accountTypeRow}>
            <Pressable
              style={[
                styles.accountTypeButton,
                accountType === 'CHECKING' && styles.accountTypeSelected,
              ]}
              onPress={() => setAccountType('CHECKING')}
            >
              <Text
                style={[
                  styles.accountTypeText,
                  accountType === 'CHECKING' && styles.accountTypeTextSelected,
                ]}
              >
                Checking
              </Text>
            </Pressable>
            <Pressable
              style={[
                styles.accountTypeButton,
                accountType === 'SAVINGS' && styles.accountTypeSelected,
              ]}
              onPress={() => setAccountType('SAVINGS')}
            >
              <Text
                style={[
                  styles.accountTypeText,
                  accountType === 'SAVINGS' && styles.accountTypeTextSelected,
                ]}
              >
                Savings
              </Text>
            </Pressable>
          </View>
        </ScrollView>

        <View style={styles.bottomAction}>
          <Pressable
            style={[styles.primaryButton, loading && styles.primaryButtonDisabled]}
            onPress={onLinkAccount}
            disabled={loading}
          >
            <Text style={styles.primaryButtonText}>{loading ? 'Linking...' : 'Link Account'}</Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  content: {
    flexGrow: 1,
    padding: 24,
    gap: 12,
    paddingTop: 60,
    paddingBottom: 40,
  },
  closeButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 8,
  },
  closeText: { fontSize: 18, color: colors.bodyText },
  title: { fontSize: 24, fontWeight: '800' },
  subtitle: { fontSize: 14, color: colors.subtitleText, marginBottom: 4 },
  input: {
    height: 48,
    borderRadius: 12,
    paddingHorizontal: 12,
    backgroundColor: colors.card,
    fontSize: 16,
  },
  dropDownButton: {
    height: 48,
    borderRadius: 12,
    paddingHorizontal: 12,
    backgroundColor: colors.card,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  dropdownText: { fontSize: 16, color: colors.bodyText },
  dropdownChevron: { fontSize: 12, color: colors.bodyText },
  dropdownList: { backgroundColor: colors.card, borderRadius: 12, overflow: 'hidden' },
  dropdownItem: {
    paddingVertical: 12,
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderBottomColor: colors.background,
  },
  dropdownItemText: { fontSize: 16, color: colors.bodyText },
  accountTypeRow: { flexDirection: 'row', gap: 10 },
  accountTypeButton: {
    flex: 1,
    height: 44,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.card,
    borderWidth: 1.5,
    borderColor: 'transparent',
  },
  accountTypeSelected: { borderColor: colors.primary },
  accountTypeText: { fontSize: 15, fontWeight: '600', color: colors.subtitleText },
  accountTypeTextSelected: { color: colors.primary },
  primaryButton: {
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
    marginTop: 4,
  },
  primaryButtonText: { color: colors.primaryText, fontSize: 16, fontWeight: '800' },
  bottomAction: {
    padding: 24,
    paddingBottom: 40,
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: {
    color: colors.error,
    fontSize: 14,
    fontWeight: '600',
  },
  primaryButtonDisabled: { backgroundColor: colors.primaryDisabled },
});
