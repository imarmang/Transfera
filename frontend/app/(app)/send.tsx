import { colors } from '@/src/themes/colors';
import { useLocalSearchParams, router } from 'expo-router';
import { View, StyleSheet, Pressable, Text, TextInput } from 'react-native';
import { useState } from 'react';

export default function Send() {
  const { amount } = useLocalSearchParams<{ amount: string }>();
  const [to, setTo] = useState('');
  const [note, setNote] = useState('');
  const [selected, setSelected] = useState<string | null>(null);
  const [error, setError] = useState("");
  return (
    <View style={styles.container}>
      {/* Top bar */}
      <View style={styles.topBar}>
        <Pressable style={styles.closeButton} onPress={() => router.back()}>
          <Text style={styles.closeText}>✕</Text>
        </Pressable>

        <Text style={styles.amountText}>${amount}</Text>

        <Pressable style={styles.typeButton}>
          <Text style={styles.typeButtonText}>Pay</Text>
        </Pressable>
      </View>
      <View style={styles.divider}></View>
      {/* End of top bar */}

      {/* Send to and Note */}
      <View style={styles.fieldRow}>
        <Text style={styles.fieldLabel}>To</Text>
        <TextInput
          style={styles.fieldInput}
          placeholder="Name, $tag, Phone, Email"
          placeholderTextColor={colors.subtitleText}
          value={to}
          onChangeText={setTo}
          autoCapitalize="none"
          autoCorrect={false}
        />
      </View>

      <View style={styles.divider}></View>

      <View style={styles.fieldRow}>
        <Text style={styles.fieldLabel}>For</Text>
        <TextInput
          style={styles.fieldInput}
          placeholder="Note (required)"
          placeholderTextColor={colors.subtitleText}
          value={note}
          onChangeText={setNote}
        />
      </View>

      <View style={styles.divider}></View>
      {/* End of Send to and Note */}

      {/* Suggested */}
      <View style={styles.suggestedSection}>
        <Text style={styles.suggestedTitle}>Suggested</Text>

        {/* Fake contacts for now */}
        <Pressable
          style={[styles.contactRow, selected === '$anthonycal' && styles.contactRowSelected]}
          onPress={() => {
            const tag = '$anthonycal';
            setSelected(selected === tag ? null : tag);
            setTo(selected === tag ? '' : tag);
          }}
        >
          <View style={styles.contactAvatar}>
            <Text style={styles.contactInitials}>AC</Text>
          </View>
          <View style={styles.contactInfo}>
            <Text style={styles.contactName}>Anthony Calore</Text>
            <Text style={styles.contactHandle}>$anthonycal</Text>
          </View>
          <View
            style={[styles.selectCircle, selected === '$anthonycal' && styles.selectCircleFilled]}
          />
        </Pressable>

        <Pressable
          style={[styles.contactRow, selected === '$johndoe' && styles.contactRowSelected]}
          onPress={() => {
            setSelected('$johndoe');
            setTo('$johndoe');
          }}
        >
          <View style={styles.contactAvatar}>
            <Text style={styles.contactInitials}>JD</Text>
          </View>
          <View style={styles.contactInfo}>
            <Text style={styles.contactName}>John Doe</Text>
            <Text style={styles.contactHandle}>$johndoe</Text>
          </View>
          <View
            style={[styles.selectCircle, selected === '$johndoe' && styles.selectCircleFilled]}
          />
        </Pressable>
      </View>
      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}
      <View style={styles.payButtonContainer}>
        <Pressable
          // The user must write a note
          style={[styles.payButton, (!selected || !note) && styles.payButtonDisabled]}
          disabled={!selected || !note}
          onPress={() => setError("Pay is not fully yet implemented")}
        >
          <Text style={styles.payButtonText}>Pay ${amount}</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
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
  typeButton: {
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 20,
    backgroundColor: colors.card,
  },
  typeButtonText: { fontSize: 15, fontWeight: '700', color: colors.bodyText },
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
  suggestedSection: { paddingHorizontal: 20, paddingTop: 24, gap: 4 },
  suggestedTitle: { fontSize: 22, fontWeight: '800', color: colors.bodyText, marginBottom: 12 },
  contactRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, gap: 14 },
  contactAvatar: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  contactInitials: { fontSize: 18, fontWeight: '700', color: colors.primaryText },
  contactInfo: { gap: 2 },
  contactName: { fontSize: 16, fontWeight: '600', color: colors.bodyText },
  contactHandle: { fontSize: 14, color: colors.subtitleText },
  contactRowSelected: {
    backgroundColor: colors.card,
    borderRadius: 12,
    paddingHorizontal: 10,
    marginHorizontal: -10,
  },
  selectCircle: {
    width: 24,
    height: 24,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: colors.subtitleText,
    marginLeft: 'auto',
  },
  selectCircleFilled: { backgroundColor: colors.primary, borderColor: colors.primary },
  payButtonContainer: {
    position: 'absolute',
    bottom: 40,
    left: 20,
    right: 20,
  },
  payButton: {
    height: 52,
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.primary,
  },
  payButtonDisabled: {
    backgroundColor: colors.primaryDisabled,
  },
  payButtonText: {
    fontSize: 16,
    fontWeight: '700',
    color: colors.primaryText,
  },
  errorBox: {
    marginHorizontal: 20,
    marginTop: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: { color: colors.error, fontSize: 14, fontWeight: '600' },});
