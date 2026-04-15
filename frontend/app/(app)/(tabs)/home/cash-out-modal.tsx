import { useEffect, useState, useCallback } from 'react';
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
import {
  cashOutMoneyTransferaWalletRequest,
  getTransferaWalletRequest,
  TransferaWalletDTO,
} from '@/src/services/wallet.service';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faBuildingColumns, faCheck, faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import LinkBankAccountModal from '@/app/(app)/profile/link-account-modal';

const QUICK_AMOUNTS = [10, 25, 50, 100, 200];

export default function CashOutScreen() {
  const { session } = useSession();

  const [selectedAmount, setSelectedAmount] = useState<number | null>(null);
  const [customInput, setCustomInput] = useState('');
  const [isCustom, setIsCustom] = useState(false);

  const [wallet, setWallet] = useState<TransferaWalletDTO | null>(null);
  const [loadingWallet, setLoadingWallet] = useState(true);

  const [accounts, setAccounts] = useState<LinkedBankAccountDTO[]>([]);
  const [selectedAccountId, setSelectedAccountId] = useState<string | null>(null);
  const [loadingAccounts, setLoadingAccounts] = useState(true);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [showLinkModal, setShowLinkModal] = useState(false);


  // ─── Derived ──────────────────────────────────────────────────────────────────
  const currentBalance = wallet?.balance ?? 0;

  const finalAmount = isCustom
    ? parseFloat(customInput) || 0
    : selectedAmount ?? 0;

  const exceedsBalance = finalAmount > currentBalance;
  const canSubmit = finalAmount > 0 && !exceedsBalance && !!selectedAccountId && !submitting && !!wallet;

  // ─── Load wallet ──────────────────────────────────────────────────────────────
  useEffect(() => {
    getTransferaWalletRequest(session!)
      .then((data) => setWallet(data))
      .catch(() => setError('Failed to load wallet balance.'))
      .finally(() => setLoadingWallet(false));
  }, []);

  // ─── Load accounts — re-fetch on focus in case user just linked one ───────────
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

  async function handleCashOut() {
    if (!canSubmit) return;
    setError('');
    setSubmitting(true);
    try {
      await cashOutMoneyTransferaWalletRequest(session!, {
        linkedBankAccountId: selectedAccountId!,
        amount: finalAmount,
      });
      router.back();
    } catch (e: any) {
      setError(e.message ?? 'Failed to cash out. Please try again.');
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
        <Text style={styles.headerTitle}>Cash Out</Text>
        <View style={styles.backButton} />
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        showsVerticalScrollIndicator={false}
      >

        {/* ── Balance Banner ── */}
        <View style={styles.balanceBanner}>
          <Text style={styles.balanceBannerLabel}>Available Balance</Text>
          <Text style={styles.balanceBannerAmount}>
            {loadingWallet ? '...' : `$${currentBalance.toFixed(2)}`}
          </Text>
        </View>

        {/* ── Amount Section ── */}
        {!loadingWallet && !loadingAccounts && accounts.length > 0 && (
          <>
            <Text style={styles.sectionLabel}>Select Amount</Text>
            <View style={styles.quickGrid}>
              {QUICK_AMOUNTS.map((value) => {
                const disabled = value > currentBalance;
                const isSelected = selectedAmount === value && !isCustom;
                return (
                  <Pressable
                    key={value}
                    style={[
                      styles.quickButton,
                      isSelected && styles.quickButtonSelected,
                      disabled && styles.quickButtonDisabled,
                    ]}
                    onPress={() => !disabled && handleQuickSelect(value)}
                    disabled={disabled}
                  >
                    <Text style={[
                      styles.quickButtonText,
                      isSelected && styles.quickButtonTextSelected,
                      disabled && styles.quickButtonTextDisabled,
                    ]}>
                      ${value}
                    </Text>
                  </Pressable>
                );
              })}
            </View>

            {/* Custom amount input */}
            <View style={[
              styles.customInputContainer,
              isCustom && customInput && !exceedsBalance && styles.customInputContainerSelected,
              exceedsBalance && styles.customInputContainerError,
            ]}>
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

            {/* Exceeds balance warning */}
            {exceedsBalance && (
              <Text style={styles.balanceWarning}>
                Amount exceeds your available balance of ${currentBalance.toFixed(2)}
              </Text>
            )}
          </>
        )}

        {/* ── Bank Account Section ── */}
        <Text style={[styles.sectionLabel, { marginTop: 28 }]}>To Account</Text>

        {loadingAccounts ? (
          <Text style={styles.loadingText}>Loading accounts...</Text>
        ) : accounts.length === 0 ? (
          <Pressable
            style={styles.noAccountCard}
            onPress={() => setShowLinkModal(true)}
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

      {/* ── Cash Out Button ── */}
      <View style={styles.footer}>
        {!loadingAccounts && accounts.length === 0 ? (
          <Pressable
            style={styles.cashOutButton}
            onPress={() => router.push('/profile/linked-bank-accounts')}
          >
            <Text style={styles.cashOutButtonText}>Link a Bank Account</Text>
          </Pressable>
        ) : (
          <Pressable
            style={[styles.cashOutButton, !canSubmit && styles.cashOutButtonDisabled]}
            onPress={handleCashOut}
            disabled={!canSubmit}
          >
            <Text style={styles.cashOutButtonText}>
              {submitting
                ? 'Cashing Out...'
                : finalAmount > 0 && !exceedsBalance
                  ? `Cash Out $${finalAmount.toFixed(2)} from Transfera Wallet`
                  : 'Cash Out from Transfera Wallet'}
            </Text>
          </Pressable>
        )}
      </View>

      <LinkBankAccountModal
        visible={showLinkModal}
        onClose={() => setShowLinkModal(false)}
        onSuccess={(newAccount) => {
          setAccounts((prev) => [...prev, newAccount]);
          setSelectedAccountId(newAccount.id);  // auto-select the new account
          setShowLinkModal(false);
        }}
      />
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
  balanceBanner: {
    backgroundColor: '#1A1A1A',
    borderRadius: 16,
    padding: 20,
    alignItems: 'center',
    marginBottom: 28,
  },
  balanceBannerLabel: {
    fontSize: 13,
    color: 'rgba(255,255,255,0.6)',
    marginBottom: 6,
  },
  balanceBannerAmount: {
    fontSize: 36,
    fontWeight: '800',
    color: 'white',
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
  quickButtonDisabled: {
    opacity: 0.35,
  },
  quickButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.bodyText,
  },
  quickButtonTextSelected: {
    color: colors.primary,
  },
  quickButtonTextDisabled: {
    color: colors.subtitleText,
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
  customInputContainerError: {
    borderColor: colors.error,
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
  balanceWarning: {
    fontSize: 13,
    color: colors.error,
    marginTop: 6,
    fontWeight: '600',
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
  cashOutButton: {
    height: 54,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  cashOutButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
  cashOutButtonText: {
    fontSize: 16,
    fontWeight: '800',
    color: colors.primaryText,
  },
});