import { colors } from '@/src/themes/colors';
import { useLocalSearchParams, router } from 'expo-router';
import { View, StyleSheet, Pressable, Text, TextInput, FlatList } from 'react-native';
import { useState, useEffect } from 'react';
import { useSession } from '@/src/context/AuthContext';
import { searchProfilesRequest, SearchProfileDTO } from '@/src/services/profile.service';
import { createMoneyRequestService } from '@/src/services/transaction.service';

export default function Request() {
  const { amount } = useLocalSearchParams<{ amount: string }>();
  const { session } = useSession();

  const [ to, setTo ] = useState( '' );
  const [ note, setNote ] = useState( '' );
  const [ selected, setSelected ] = useState<SearchProfileDTO | null>( null );
  const [ results, setResults ] = useState<SearchProfileDTO[]>( [] );
  const [ error, setError ] = useState( '' );
  const [ submitting, setSubmitting ] = useState( false );

  async function handleRequest() {
    if ( !selected ) return;
    setError( '' );
    try {
      setSubmitting( true );
      await createMoneyRequestService( session!, {
        recipientUsername: selected.userName,
        amount: Number( amount ),
        note: note ?? '',
      } );
      router.dismiss( 2 );
    } catch ( e: any ) {
      setError( e.message ?? 'Failed to send request. Please try again.' );
      setTimeout( () => setError( '' ), 3000 );
    } finally {
      setSubmitting( false );
    }
  }

  useEffect( () => {
    if ( selected ) return;
    if ( to.length < 2 ) {
      setResults( [] );
      return;
    }

    const timer = setTimeout( async () => {
      try {
        const data = await searchProfilesRequest( session!, to );
        setResults( data );
      } catch {
        setResults( [] );
      }
    }, 300 );

    return () => clearTimeout( timer );
  }, [ to ] );

  function handleSelect( profile: SearchProfileDTO ) {
    setSelected( profile );
    setTo( profile.userName );
    setResults( [] );
  }

  function handleToChange( text: string ) {
    const cleaned = text.startsWith( '@' ) ? text.slice( 1 ) : text;
    setTo( cleaned );
    if ( selected ) setSelected( null );
  }

  return (
    <View style={styles.container}>

      {/* Top bar */}
      <View style={styles.topBar}>
        <Pressable style={styles.closeButton} onPress={() => router.back()}>
          <Text style={styles.closeText}>✕</Text>
        </Pressable>
        <Text style={styles.amountText}>{`$${ amount }`}</Text>
        <View style={styles.closeButton} />
      </View>
      <View style={styles.divider} />

      {/* To field */}
      <View style={styles.fieldRow}>
        <Text style={styles.fieldLabel}>From</Text>
        <View style={styles.atInputContainer}>
          <Text style={styles.atPrefix}>@</Text>
          <TextInput
            style={styles.fieldInput}
            placeholder="username"
            placeholderTextColor={colors.subtitleText}
            value={to}
            onChangeText={handleToChange}
            autoCapitalize="none"
            autoCorrect={false}
          />
        </View>
      </View>
      <View style={styles.divider} />

      {/* Selected user badge */}
      {selected && (
        <>
          <View style={styles.selectedBadge}>
            <View style={styles.contactAvatar}>
              <Text style={styles.contactInitials}>
                {selected.firstName.charAt( 0 ).toUpperCase()}{selected.lastName.charAt( 0 ).toUpperCase()}
              </Text>
            </View>
            <View style={styles.contactInfo}>
              <Text style={styles.contactName}>{selected.firstName} {selected.lastName}</Text>
              <Text style={styles.contactHandle}>@{selected.userName}</Text>
            </View>
            <Pressable onPress={() => { setSelected( null ); setTo( '' ); }}>
              <Text style={styles.closeText}>✕</Text>
            </Pressable>
          </View>
          <View style={styles.divider} />

          {/* For field */}
          <View style={styles.fieldRow}>
            <Text style={styles.fieldLabel}>For</Text>
            <TextInput
              style={styles.fieldInput}
              placeholder="Note (optional)"
              placeholderTextColor={colors.subtitleText}
              value={note}
              onChangeText={setNote}
            />
          </View>
          <View style={styles.divider} />
        </>
      )}

      {/* Search results */}
      {results.length > 0 && (
        <FlatList
          data={results}
          keyExtractor={( item ) => item.userName}
          renderItem={( { item } ) => (
            <Pressable style={styles.contactRow} onPress={() => handleSelect( item )}>
              <View style={styles.contactAvatar}>
                <Text style={styles.contactInitials}>
                  {item.firstName.charAt( 0 ).toUpperCase()}{item.lastName.charAt( 0 ).toUpperCase()}
                </Text>
              </View>
              <View style={styles.contactInfo}>
                <Text style={styles.contactName}>{item.firstName} {item.lastName}</Text>
                <Text style={styles.contactHandle}>@{item.userName}</Text>
              </View>
            </Pressable>
          )}
          style={styles.resultsList}
          contentContainerStyle={styles.resultsContent}
          keyboardShouldPersistTaps="handled"
        />
      )}

      {/* Error */}
      {!!error && (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}

      {/* Request button */}
      <View style={styles.requestButtonContainer}>
        <Pressable
          style={[ styles.requestButton, ( !selected || submitting ) && styles.requestButtonDisabled ]}
          disabled={!selected || submitting}
          onPress={handleRequest}
        >
          <Text style={styles.requestButtonText}>
            {submitting ? 'Requesting...' : `Request $${ amount }`}
          </Text>
        </Pressable>
      </View>

    </View>
  );
}

const styles = StyleSheet.create( {
  container: { flex: 1, backgroundColor: colors.background, paddingTop: 60 },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingBottom: 16,
  },
  closeButton: { width: 36, height: 36, alignItems: 'center', justifyContent: 'center' },
  closeText: { fontSize: 18, color: colors.bodyText },
  amountText: { fontSize: 20, fontWeight: '700', color: colors.bodyText },
  divider: { height: 1, backgroundColor: colors.card },
  fieldRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 14,
    gap: 16,
  },
  fieldLabel: { fontSize: 16, fontWeight: '700', color: colors.bodyText, width: 36 },
  fieldInput: { flex: 1, fontSize: 16, color: colors.bodyText },
  resultsList: { flex: 1 },
  resultsContent: { paddingBottom: 100 },
  contactRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 14,
    paddingHorizontal: 20,
    gap: 14,
    borderBottomWidth: 1,
    borderBottomColor: colors.card,
  },
  selectedBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 10,
    gap: 14,
    backgroundColor: colors.card,
  },
  contactAvatar: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  contactInitials: {
    fontSize: 18,
    fontWeight: '700',
    color: colors.primaryText,
    textAlign: 'center',
  },
  contactInfo: { flex: 1, gap: 2 },
  contactName: { fontSize: 16, fontWeight: '600', color: colors.bodyText },
  contactHandle: { fontSize: 14, color: colors.subtitleText },
  requestButtonContainer: {
    position: 'absolute',
    bottom: 40,
    left: 20,
    right: 20,
  },
  requestButton: {
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.card,
  },
  requestButtonDisabled: { opacity: 0.5 },
  requestButtonText: { fontSize: 16, fontWeight: '700', color: colors.bodyText },
  errorBox: {
    marginHorizontal: 20,
    marginTop: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: { color: colors.error, fontSize: 14, fontWeight: '600' },
  atInputContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 2,
  },
  atPrefix: {
    fontSize: 16,
    fontWeight: '600',
    color: colors.bodyText,
  },
} );