import { ScrollView, Text, View, StyleSheet, Pressable, ActivityIndicator } from 'react-native';
import { colors } from '@/src/themes/colors';
import { useCallback, useState } from 'react';
import { router, useFocusEffect } from 'expo-router';
import { faBuildingColumns, faArrowUp, faArrowDown, faUser, faClockRotateLeft } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { useSession } from '@/src/context/AuthContext';
import {
  getTransactionHistoryRequest,
  respondToMoneyRequestService,
  TransactionDTO,
  TransactionType,
  MoneyRequestDTO,
  ActivityFeedDTO,
} from '@/src/services/transaction.service';

export default function Activity() {
  const { session } = useSession();
  const [ feed, setFeed ] = useState<ActivityFeedDTO>( { pendingRequests: [], transactions: [] } );
  const [ loading, setLoading ] = useState( true );
  const [ error, setError ] = useState( '' );
  const [ respondingId, setRespondingId ] = useState<string | null>( null );

  useFocusEffect(
    useCallback( () => {
      fetchFeed();
    }, [] )
  );

  async function fetchFeed() {
    setLoading( true );
    setError( '' );
    try {
      const data = await getTransactionHistoryRequest( session! );
      setFeed( data );
    } catch {
      setError( 'Failed to load activity.' );
    } finally {
      setLoading( false );
    }
  }

  async function handleRespond( moneyRequestId: string, response: 'APPROVED' | 'DECLINED' ) {
    setRespondingId( moneyRequestId );
    try {
      await respondToMoneyRequestService( session!, { moneyRequestId, response } );
      await fetchFeed();
    } catch {
      setError( 'Failed to respond to request.' );
    } finally {
      setRespondingId( null );
    }
  }

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
      case 'SEND': return transaction.moneyRequestId
        ? `Paid request from ${ transaction.peerName }`
        : `Sent to ${ transaction.peerName }`;
      case 'RECEIVED': return transaction.moneyRequestId
        ? `Request paid by ${ transaction.peerName }`
        : `Received from ${ transaction.peerName }`;
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

  const hasContent = feed.pendingRequests.length > 0 || feed.transactions.length > 0;

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>

      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Recent Activity</Text>
        <View style={styles.topBarRight}>
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
        <Text style={styles.loadingText}>Loading activity...</Text>
      )}

      {/* Empty state */}
      {!loading && !error && !hasContent && (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyText}>No activity yet.</Text>
          <Text style={styles.emptySubtext}>Add money or send to get started.</Text>
        </View>
      )}

      {/* Pending Requests */}
      {!loading && feed.pendingRequests.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Pending Requests</Text>
          {feed.pendingRequests.map( ( request: MoneyRequestDTO ) => (
            <View key={request.moneyRequestId} style={styles.requestItem}>
              <View style={styles.requestIcon}>
                <FontAwesomeIcon icon={faClockRotateLeft} size={18} color="white" />
              </View>
              <View style={styles.requestDetails}>
                <Text style={styles.transactionName}>
                  {request.payer
                    ? `${ request.peerName } is requesting $$${ request.amount.toFixed( 2 ) }`
                    : `You requested $${ request.amount.toFixed( 2 ) } from ${ request.peerName }`}
                </Text>
                {request.note ? <Text style={styles.requestNote}>{request.note}</Text> : null}
                <Text style={styles.transactionDate}>{formatDate( request.createdAt )}</Text>
                {request.payer && (
                  <View style={styles.requestActions}>
                    <Pressable
                      style={[styles.actionButton, styles.approveButton]}
                      onPress={() => handleRespond( request.moneyRequestId, 'APPROVED' )}
                      disabled={respondingId === request.moneyRequestId}
                    >
                      {respondingId === request.moneyRequestId
                        ? <ActivityIndicator size="small" color="white" />
                        : <Text style={styles.actionButtonText}>Approve</Text>}
                    </Pressable>
                    <Pressable
                      style={[styles.actionButton, styles.declineButton]}
                      onPress={() => handleRespond( request.moneyRequestId, 'DECLINED' )}
                      disabled={respondingId === request.moneyRequestId}
                    >
                      <Text style={styles.actionButtonText}>Decline</Text>
                    </Pressable>
                  </View>
                )}
              </View>
              <Text style={styles.transactionAmount}>
                ${request.amount.toFixed( 2 )}
              </Text>
            </View>
          ) )}
        </View>
      )}

      {/* Transactions */}
      {!loading && feed.transactions.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Transactions</Text>
          {feed.transactions.map( ( transaction: TransactionDTO ) => {
            const positive = isPositive( transaction.transactionType );
            const isDeclined = transaction.transactionStatus === 'DECLINED';
            return (
              <View key={transaction.transactionId} style={styles.transactionItem}>
                <View style={[
                  styles.transactionIcon,
                  positive && !isDeclined && styles.transactionIconPositive,
                  isDeclined && styles.transactionIconDeclined,
                ]}>
                  <FontAwesomeIcon
                    icon={getIcon( transaction.transactionType )}
                    size={18}
                    color="white"
                  />
                </View>
                <View style={styles.transactionDetails}>
                  <Text style={styles.transactionName}>{getLabel( transaction )}</Text>
                  {isDeclined && (
                    <Text style={styles.declinedBadge}>Declined</Text>
                  )}
                  <Text style={styles.transactionDate}>{formatDate( transaction.createdAt )}</Text>
                </View>
                <Text style={[
                  styles.transactionAmount,
                  positive && !isDeclined ? styles.transactionAmountPositive : styles.transactionAmountNegative,
                ]}>
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
  section: { gap: 4 },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: colors.subtitleText,
    marginBottom: 8,
    marginTop: 4,
  },
  requestItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: colors.card,
    gap: 12,
  },
  requestIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: '#B45309',
    alignItems: 'center',
    justifyContent: 'center',
  },
  requestDetails: { flex: 1, gap: 4 },
  requestNote: { fontSize: 13, color: colors.subtitleText },
  requestActions: { flexDirection: 'row', gap: 8, marginTop: 8 },
  actionButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    minWidth: 80,
  },
  approveButton: { backgroundColor: '#1A6B3A' },
  declineButton: { backgroundColor: colors.error },
  actionButtonText: { color: 'white', fontWeight: '700', fontSize: 13 },
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
  transactionIconDeclined: { backgroundColor: colors.subtitleText },
  transactionDetails: { flex: 1, gap: 2 },
  transactionName: { fontSize: 15, fontWeight: '600', color: colors.bodyText },
  declinedBadge: { fontSize: 12, color: colors.error, fontWeight: '600' },
  transactionDate: { fontSize: 12, color: colors.subtitleText },
  transactionAmount: { fontSize: 16, fontWeight: '700' },
  transactionAmountPositive: { color: '#1A6B3A' },
  transactionAmountNegative: { color: colors.error },
} );