import { router, useFocusEffect } from 'expo-router';
import { Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { colors } from '@/src/themes/colors';
import { useEffect, useState, useCallback } from 'react';
import { faMagnifyingGlass, faUser } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { getTransferaWalletRequest, TransferaWalletDTO } from '@/src/services/wallet.service';
import { useSession } from '@/src/context/AuthContext';

export default function Index() {
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [wallet, setWallet] = useState<TransferaWalletDTO | null>(null);

  const { session } = useSession();

  async function fetchWallet() {
    setLoading(true);
    setError('');
    try {
      const data = await getTransferaWalletRequest(session!);
      setWallet(data);
    } catch (e: any) {
      setError(e.message ?? 'Failed to load wallet.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchWallet();
  }, []);

  // Re-fetch wallet when returning from add-money screen
  useFocusEffect(
    useCallback(() => {
      fetchWallet();
    }, [])
  );

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Money</Text>
        <View style={styles.topBarRight}>
          <Pressable
            style={styles.topBarIcon}
            onPress={() => setError('Search is not yet implemented')}
          >
            <FontAwesomeIcon icon={faMagnifyingGlass} size={20} color={colors.bodyText} />
          </Pressable>
          <Pressable style={styles.topBarIcon} onPress={() => router.push('/profile')}>
            <FontAwesomeIcon icon={faUser} size={20} color={colors.bodyText} />
          </Pressable>
        </View>
      </View>

      {/* Balance Card */}
      <View style={styles.balanceCard}>
        <Text style={styles.balanceLabel}>Transfera Balance</Text>
        <Text style={styles.balanceAmount}>
          {loading ? '...' : `$${wallet?.balance.toFixed(2) ?? '0.00'}`}
        </Text>
        <View style={styles.balanceAction}>
          <Pressable
            style={styles.balanceButton}
            onPress={() => router.push('/home/add-money-modal')}
          >
            <Text style={styles.balanceButtonText}>Add Money</Text>
          </Pressable>
          <Pressable
            style={styles.balanceButton}
            onPress={() => setError('Cash out is not yet implemented')}
          >
            <Text style={styles.balanceButtonText}>Cash Out</Text>
          </Pressable>
        </View>
      </View>

      {!!error && (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  headerTitle: { fontSize: 28, fontWeight: '800', color: colors.bodyText },
  scroll: { flex: 1, backgroundColor: colors.background },
  scrollContent: { padding: 20, paddingTop: 60, gap: 12 },
  balanceCard: {
    borderRadius: 18,
    padding: 24,
    backgroundColor: '#1A1A1A',
    gap: 8,
  },
  balanceLabel: { color: 'rgba(255,255,255,0.7)', fontSize: 14 },
  balanceAmount: { color: 'white', fontSize: 36, fontWeight: '800' },
  balanceAction: { flexDirection: 'row', gap: 10, marginTop: 12 },
  balanceButton: {
    flex: 1,
    height: 40,
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'rgba(255,255,255,0.15)',
  },
  balanceButtonText: { color: 'white', fontSize: 14, fontWeight: '600' },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: { color: colors.error, fontSize: 14, fontWeight: '600' },
  topBarIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  topBarRight: { flexDirection: 'row', gap: 8 },
});