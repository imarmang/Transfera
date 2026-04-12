import { useCallback, useState } from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { router, useFocusEffect } from 'expo-router';
import { colors } from '@/src/themes/colors';
import { useSession } from '@/src/context/AuthContext';
import { getLinkedBankAccountRequest, LinkedBankAccountDTO } from '@/src/services/linked-account.service';
import { addMoneyTransferaWalletRequest } from '@/src/services/wallet.service';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faBuildingColumns, faCheck, faChevronLeft } from '@fortawesome/free-solid-svg-icons';

const QUICK_AMOUNTS = [10, 25, 50, 100, 200];

export default function AddMoneyScreen() {
  const { session } = useSession();

  const [selectedAmount, setSelectedAmount] = useState<number | null>(null);
  const [customInput, setCustomInput] = useState('');
  const [isCustom, setIsCustom] = useState(false);

  const [accounts, setAccounts] = useState<LinkedBankAccountDTO[]>([]);
  const [selectedAccountId, setSelectedAccountId] = useState<string | null>(null);
  const [loadingAccounts, setLoadingAccounts] = useState(true);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  // ─── Derived ──────────────────────────────────────────────────────────────────
  const finalAmount = isCustom
    ? parseFloat(customInput) || 0
    : selectedAmount ?? 0;

  const selectedAccount = accounts.find((a) => a.id === selectedAccountId) ?? null;
  const canSubmit = finalAmount > 0 && !!selectedAccountId && !submitting;

  useFocusEffect(
    useCallback(() => {
      setLoadingAccounts(true);
      getLinkedBankAccountRequest(session!)
        .then((data) => {
          setAccounts(data);
          if (data.length === 1) setSelectedAccountId(data[0].id);
        })
        .catch(() => setError('Failed to load bank accounts.'))
        .finally(() => setLoadingAccounts(false));
    }, [])
  );

  // ─── Handlers ────────────────────────────────────────────────────────────────
  function handleQuickSelect(value: number) {
    setSelectedAmount(value);
    setIsCustom(false);
    setCustomInput('');
    setError('');
  }

  function handleCustomChange(text: string) {
    const cleaned = text.replace(/[^0-9.]/g, '');
    const parts = cleaned.split('.');
    if (parts.length > 2) return;
    if (parts[1] && parts[1].length > 2) return;
    if (parts[0].length > 6) return;
    setCustomInput(cleaned);
    setIsCustom(true);
    setSelectedAmount(null);
    setError('');
  }

  async function handleAddMoney() {
    if (!canSubmit) return;
    setError('');
    setSubmitting(true);
    try {
      await addMoneyTransferaWalletRequest(session!, {
        linkedBankAccountId: selectedAccountId!,
        amount: finalAmount,
      });
      router.back();
    } catch (e: any) {
      setError(e.message ?? 'Failed to add money. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  // ─── Render ───────────────────────────────────────────────────────────────────
  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      {/* Header */}
      <View style={styles.header}>
        <Pressable style={styles.backButton} onPress={() => router.back()}>
          <FontAwesomeIcon icon={faChevronLeft} size={18} color={colors.bodyText} />
        </Pressable>
        <Text style={styles.headerTitle}>Add Money</Text>
        <View style={styles.backButton} />
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        showsVerticalScrollIndicator={false}
      >

        {/* ── Amount Section ── */}
        {!loadingAccounts && accounts.length > 0 && (
          <>
            <Text style={styles.sectionLabel}>Select Amount</Text>
            <View style={styles.quickGrid}>
              {QUICK_AMOUNTS.map((value) => (
                <Pressable
                  key={value}
                  style={[styles.quickButton, selectedAmount === value && !isCustom && styles.quickButtonSelected]}
                  onPress={() => handleQuickSelect(value)}
                >
                  <Text style={[styles.quickButtonText, selectedAmount === value && !isCustom && styles.quickButtonTextSelected]}>
                    ${value}
                  </Text>
                </Pressable>
              ))}
            </View>

            {/* Custom amount input */}
            <View style={[styles.customInputContainer, isCustom && customInput && styles.customInputContainerSelected]}>
              <Text style={styles.customInputPrefix}>$</Text>
              <TextInput
                style={styles.customInput}
                placeholder="Custom amount"
                placeholderTextColor={colors.subtitleText}
                value={customInput}
                onChangeText={handleCustomChange}
                keyboardType="decimal-pad"
                onFocus={() => {
                  if (customInput) {
                    setIsCustom(true);
                    setSelectedAmount(null);
                  }
                }}
              />
            </View>
          </>
        )}



      {/* ── Bank Account Section ── */}
        <Text style={[styles.sectionLabel, { marginTop: 28 }]}>From Account</Text>

        {loadingAccounts ? (
          <Text style={styles.loadingText}>Loading accounts...</Text>
        ) : accounts.length === 0 ? (
          <Pressable
            style={styles.noAccountCard}
            onPress={() => router.push('/profile/linked-bank-accounts')}
          >
            <FontAwesomeIcon icon={faBuildingColumns} size={20} color={colors.primary} />
            <Text style={styles.noAccountText}>No bank accounts linked. Tap to add one.</Text>
          </Pressable>
        ) : (
          <View style={styles.accountList}>
            {accounts.map((account) => {
              const isSelected = account.id === selectedAccountId;
              return (
                <Pressable
                  key={account.id}
                  style={[styles.accountRow, isSelected && styles.accountRowSelected]}
                  onPress={() => setSelectedAccountId(account.id)}
                >
                  <View style={styles.accountIcon}>
                    <FontAwesomeIcon
                      icon={faBuildingColumns}
                      size={18}
                      color={isSelected ? colors.primary : colors.subtitleText}
                    />
                  </View>
                  <View style={styles.accountInfo}>
                    <Text style={styles.accountBank}>{account.bankName}</Text>
                    <Text style={styles.accountDetails}>
                      {account.accountType.charAt(0) + account.accountType.slice(1).toLowerCase()} •••• {account.lastFourDigitsAccountNumber}
                    </Text>
                  </View>
                  {isSelected && (
                    <FontAwesomeIcon icon={faCheck} size={16} color={colors.primary} />
                  )}
                </Pressable>
              );
            })}
          </View>
        )}

        {/* Error */}
        {!!error && (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        )}

      </ScrollView>

      <View style={styles.footer}>
        {!loadingAccounts && accounts.length === 0 ? (
          <Pressable
            style={styles.addButton}
            onPress={() => router.push('/profile/linked-bank-accounts')}
          >
            <Text style={styles.addButtonText}>Link a Bank Account</Text>
          </Pressable>
        ) : (
          <Pressable
            style={[styles.addButton, !canSubmit && styles.addButtonDisabled]}
            onPress={handleAddMoney}
            disabled={!canSubmit}
          >
            <Text style={styles.addButtonText}>
              {submitting
                ? 'Adding...'
                : finalAmount > 0
                  ? `Add $${finalAmount.toFixed(2)} to Transfera Wallet`
                  : 'Add Money to Transfera Wallet'}
            </Text>
          </Pressable>
        )}
      </View>

    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 16,
  },
  backButton: {
    width: 40,
    height: 40,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerTitle: {
    flex: 1,
    textAlign: 'center',
    fontSize: 17,
    fontWeight: '700',
    color: colors.bodyText,
  },
  scroll: { flex: 1 },
  scrollContent: {
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  sectionLabel: {
    fontSize: 13,
    fontWeight: '700',
    color: colors.subtitleText,
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    marginBottom: 12,
  },
  quickGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    marginBottom: 12,
  },
  quickButton: {
    width: '30%',
    height: 52,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.card,
    borderWidth: 1.5,
    borderColor: 'transparent',
  },
  quickButtonSelected: {
    borderColor: colors.primary,
  },
  quickButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.bodyText,
  },
  quickButtonTextSelected: {
    color: colors.primary,
  },
  customInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.card,
    borderRadius: 12,
    paddingHorizontal: 16,
    height: 52,
    borderWidth: 1.5,
    borderColor: 'transparent',
  },
  customInputContainerSelected: {
    borderColor: colors.primary,
  },
  customInputPrefix: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.bodyText,
    marginRight: 4,
  },
  customInput: {
    flex: 1,
    fontSize: 16,
    fontWeight: '600',
    color: colors.bodyText,
  },
  loadingText: {
    color: colors.subtitleText,
    fontSize: 14,
    textAlign: 'center',
    paddingVertical: 20,
  },
  noAccountCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    backgroundColor: colors.card,
    borderRadius: 14,
    padding: 16,
    borderWidth: 1.5,
    borderColor: colors.primary,
  },
  noAccountText: {
    flex: 1,
    fontSize: 14,
    fontWeight: '600',
    color: colors.bodyText,
  },
  accountList: {
    gap: 10,
  },
  accountRow: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderRadius: 14,
    backgroundColor: colors.card,
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
  footer: {
    paddingHorizontal: 20,
    paddingBottom: 40,
    paddingTop: 12,
  },
  addButton: {
    height: 54,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  addButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
  addButtonText: {
    fontSize: 16,
    fontWeight: '800',
    color: colors.primaryText,
  },
});