import { ScrollView, Text, View, StyleSheet, Pressable } from 'react-native';
import { colors } from '@/src/themes/colors';
import { useCallback, useState } from 'react';
import { router, useFocusEffect } from 'expo-router';
import { faMagnifyingGlass, faUser, faArrowUp, faArrowDown, faBuildingColumns } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { useSession } from '@/src/context/AuthContext';
import { getTransactionHistoryRequest, TransactionDTO, TransactionType } from '@/src/services/transaction.service';

export default function Activity() {
  const { session } = useSession();
  const [transactions, setTransactions] = useState<TransactionDTO[]>( [] );
  const [loading, setLoading] = useState( true );
  const [error, setError] = useState( '' );

  useFocusEffect(
    useCallback( () => {
      setLoading( true );
      setError( '' );
      getTransactionHistoryRequest( session! )
        .then( setTransactions )
        .catch( () => setError( 'Failed to load transactions.' ) )
        .finally( () => setLoading( false ) );
    }, [] )
  );

  function getIcon( type: TransactionType ) {
    switch ( type ) {
      case 'ADD_MONEY': return faBuildingColumns;
      case 'CASH_OUT': return faBuildingColumns;
      case 'SEND': return faArrowUp;
      case 'RECEIVED': return faArrowDown;
      default: return faBuildingColumns;

    }
  }

  function getLabel( transaction: TransactionDTO ): string {
    switch ( transaction.transactionType ) {
      case 'ADD_MONEY': return `Added from ${ transaction.bankName } ••••${ transaction.lastFourDigits }`;
      case 'CASH_OUT': return `Cashed out to ${ transaction.bankName } ••••${ transaction.lastFourDigits }`;
      case 'SEND': return `Sent to ${ transaction.peerName }`;
      case 'RECEIVED': return `Received from ${ transaction.peerName }`;
    }
  }

  function isPositive( type: TransactionType ): boolean {
    return type === 'ADD_MONEY' || type === 'RECEIVED';
  }

  function formatDate( createdAt: string ): string {
    return new Date( createdAt ).toLocaleDateString( 'en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    } );
  }

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>

      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Recent Activity</Text>
        <View style={styles.topBarRight}>
          <Pressable
            style={styles.topBarIcon}
            onPress={() => setError( 'Search is not yet implemented' )}
          >
            <FontAwesomeIcon icon={faMagnifyingGlass} size={20} color={colors.bodyText} />
          </Pressable>
          <Pressable style={styles.topBarIcon} onPress={() => router.push( '/profile' )}>
            <FontAwesomeIcon icon={faUser} size={20} color={colors.bodyText} />
          </Pressable>
        </View>
      </View>

      {/* Error */}
      {!!error && (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}

      {/* Loading */}
      {loading && (
        <Text style={styles.loadingText}>Loading transactions...</Text>
      )}

      {/* Empty state */}
      {!loading && !error && transactions.length === 0 && (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyText}>No transactions yet.</Text>
          <Text style={styles.emptySubtext}>Add money or send to get started.</Text>
        </View>
      )}

      {/* Transaction list */}
      {!loading && transactions.length > 0 && (
        <View style={styles.transactionsSection}>
          {transactions.map( ( transaction ) => {
            const positive = isPositive( transaction.transactionType );
            return (
              <View key={transaction.transactionId} style={styles.transactionItem}>
                <View style={[styles.transactionIcon, positive && styles.transactionIconPositive]}>
                  <FontAwesomeIcon
                    icon={getIcon( transaction.transactionType )}
                    size={18}
                    color="white"
                  />
                </View>
                <View style={styles.transactionDetails}>
                  <Text style={styles.transactionName}>{getLabel( transaction )}</Text>
                  <Text style={styles.transactionDate}>{formatDate( transaction.createdAt )}</Text>
                </View>
                <Text style={[styles.transactionAmount, positive ? styles.transactionAmountPositive : styles.transactionAmountNegative]}>
                  {positive ? '+' : '-'}${transaction.amount.toFixed( 2 )}
                </Text>
              </View>
            );
          } )}
        </View>
      )}

    </ScrollView>
  );
}

const styles = StyleSheet.create( {
  scroll: { flex: 1, backgroundColor: colors.background },
  scrollContent: { padding: 20, paddingTop: 60, gap: 12 },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  headerTitle: { fontSize: 28, fontWeight: '800' },
  topBarRight: { flexDirection: 'row', gap: 8 },
  topBarIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: { color: colors.error, fontSize: 14, fontWeight: '600' },
  loadingText: {
    color: colors.subtitleText,
    fontSize: 14,
    textAlign: 'center',
    paddingVertical: 20,
  },
  emptyBox: {
    alignItems: 'center',
    paddingVertical: 60,
    gap: 8,
  },
  emptyText: { fontSize: 16, fontWeight: '700', color: colors.bodyText },
  emptySubtext: { fontSize: 14, color: colors.subtitleText },
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
  transactionIconPositive: { backgroundColor: '#1A6B3A' },
  transactionDetails: { flex: 1, gap: 2 },
  transactionName: { fontSize: 15, fontWeight: '600', color: colors.bodyText },
  transactionDate: { fontSize: 12, color: colors.subtitleText },
  transactionAmount: { fontSize: 16, fontWeight: '700' },
  transactionAmountPositive: { color: '#1A6B3A' },
  transactionAmountNegative: { color: colors.error },
} );