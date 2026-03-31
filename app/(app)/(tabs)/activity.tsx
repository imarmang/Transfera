import { ScrollView, Text, View, StyleSheet, Pressable } from 'react-native';
import { colors } from '@/src/themes/colors';
import { useState } from 'react';
import { router } from 'expo-router';
import { faMagnifyingGlass, faUser } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';

export default function Activity() {
  const [error, setError] = useState('');
  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Recent Activity</Text>
        </View>

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

      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}

      {/*  Recent Transactions, fake for now */}
      <View style={styles.transactionsSection}>
        <View style={styles.transactionItem}>
          <View style={styles.transactionIcon}>
            <Text style={styles.transactionEmoji}>↑</Text>
          </View>
          <View style={styles.transactionDetails}>
            <Text style={styles.transactionName}>Sent to @johndoe</Text>
            <Text style={styles.transactionDate}>Mar 5, 2026</Text>
          </View>
          <Text style={styles.transactionAmountNegative}>-$25.00</Text>
        </View>

        <View style={styles.transactionItem}>
          <View style={[styles.transactionIcon, styles.transactionIconPositive]}>
            <Text style={styles.transactionEmoji}>↓</Text>
          </View>
          <View style={styles.transactionDetails}>
            <Text style={styles.transactionName}>Received from @janedoe</Text>
            <Text style={styles.transactionDate}>Mar 4, 2026</Text>
          </View>
          <Text style={styles.transactionAmountPositive}>+$100.00</Text>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: colors.background },
  scrollContent: { padding: 20, paddingTop: 60, gap: 12 },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  headerTitle: { fontSize: 28, fontWeight: '800' },

  transactionsSection: { gap: 4 },
  transactionItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: colors.card,
    gap: 12,
  },
  transactionIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  transactionEmoji: { color: 'white', fontSize: 17, fontWeight: '800' },
  transactionDetails: { flex: 1, gap: 2 },
  transactionName: { fontSize: 15, fontWeight: '600' },
  transactionDate: { fontSize: 12, color: colors.subtitleText },
  transactionAmountNegative: {
    fontSize: 17,
    fontWeight: '600',
    color: colors.error,
  },
  transactionIconPositive: { backgroundColor: '#1A6B3A' },
  transactionAmountPositive: {
    fontSize: 15,
    fontWeight: '700',
    color: '#1A6B3A',
  },

  topBarIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  topBarEmoji: { fontSize: 22 },
  topBarRight: { flexDirection: 'row', gap: 8 },
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
});
