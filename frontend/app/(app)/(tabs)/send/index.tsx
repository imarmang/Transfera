import { useCallback, useState } from 'react';
import { Pressable, StyleSheet, View, Text } from 'react-native';
import { colors } from '@/src/themes/colors';
import { router, useFocusEffect } from 'expo-router';
import {
  faQrcode,
  faUser,
  faDeleteLeft,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { getTransferaWalletRequest } from '@/src/services/wallet.service';
import { useSession } from '@/src/context/AuthContext';

export default function Index() {
  const [error, setError] = useState('');
  const [amount, setAmount] = useState('0');

  const { session } = useSession();
  const [balance, setBalance] = useState( 0 );
  const amountExceedsBalance = Number( amount ) > balance;


  useFocusEffect(
    useCallback( () => {
      getTransferaWalletRequest( session! )
        .then( ( data ) => setBalance( data.balance ) )
        .catch( () => {} );
    }, [] )
  );

  function handleKey(key: string) {
    if (key === '⌫') setAmount('0');
    else if (key === '.' && (amount.includes('.') || Number(amount) < 1)) return;
    else if (key === '0' && amount === '0') return;
    else if (amount === '0' && key !== '0') setAmount(key);
    else setAmount((prev) => prev + key);
  }

  return (
    <View style={styles.container}>
      {/*  Top Bar */}
      <View style={styles.topBar}>
        <Pressable
          style={styles.topBarIcon}
          onPress={() => {
            setError('QR code is not yet implemented');
            setTimeout(() => setError(''), 3000);
          }}
        >
          <FontAwesomeIcon icon={faQrcode} size={20} color={colors.bodyText} />
        </Pressable>

        <View style={styles.topBarRight}>
          <Pressable style={styles.topBarIcon} onPress={() => router.push('/profile')}>
            <FontAwesomeIcon icon={faUser} size={20} color={colors.bodyText} />
          </Pressable>
        </View>
      </View>
      {/* End of Top Bar */}

      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}

      {/* Amount to display */}
      <View style={styles.amountDisplay}>
        <Text style={styles.amountText}>${amount}</Text>
      </View>
      {/* End Amount to display */}

      {/* Number Pad */}
      <View style={styles.numberPad}>
        {['1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '0', '⌫'].map((key) => (
          <Pressable key={key} style={styles.numberKey} onPress={() => handleKey(key)}>
            {key === '⌫' ? (
              <FontAwesomeIcon icon={faDeleteLeft} size={24} color={colors.bodyText} />
            ) : (
              <Text style={styles.numberKeyText}>{key}</Text>
            )}
          </Pressable>
        ))}
      </View>
      {/* End of Number Pad */}

      {/* Pay and Request Button */}
      <View style={styles.actionButtons}>
        <Pressable
          style={styles.requestButton}
          onPress={() => {
            setError('Request button is not yet implemented.')
            setTimeout(() => setError(''), 3000);
          }}

            >
          <Text style={styles.requestButtonText}>Request</Text>
        </Pressable>



        <Pressable
          style={[
            styles.payButton,
            ( Number( amount ) < 1 || amountExceedsBalance ) && styles.payButtonDisabled
          ]}
          disabled={Number( amount ) < 1 || amountExceedsBalance}
          onPress={() => router.push(
            { pathname: '/send/pay',
              params: {
                amount: parseFloat( amount ).toFixed( 2 )
            } } )}
        >
          <Text style={styles.payButtonText}>
            {amountExceedsBalance ? 'Insufficient Balance' : 'Pay'}
          </Text>
        </Pressable>
      </View>
      {/* End of Pay and Request Button */}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    paddingTop: 60,
    paddingHorizontal: 20,
  },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  topBarIcon: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  topBarEmoji: {
    fontSize: 22,
  },
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
  amountDisplay: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  amountText: { fontSize: 64, fontWeight: '800' },
  numberPad: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 16,
  },
  numberKey: {
    width: '33.33%',
    height: 72,
    alignItems: 'center',
    justifyContent: 'center',
  },
  numberKeyText: {
    fontSize: 24,
    fontWeight: '500',
  },
  actionButtons: { flexDirection: 'row', gap: 12, marginBottom: 32 },
  requestButton: {
    flex: 1,
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.card,
  },
  requestButtonText: {
    fontSize: 16,
    fontWeight: '700',
  },
  payButton: {
    flex: 1,
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  payButtonText: {
    fontSize: 16,
    fontWeight: '700',
    color: 'white',
  },
  payButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
});
