import { View, StyleSheet, Pressable, Text, ScrollView } from 'react-native';
import { router } from 'expo-router';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { faArrowLeft, faPlus } from '@fortawesome/free-solid-svg-icons';
import { colors } from '@/src/themes/colors';
import { useState } from 'react';
import { LinkedBankAccountDTO } from '@/src/services/linked-account.service';
import LinkBankAccountModal from './link-account-modal';

export default function LinkedBankAccounts() {
  const [showModal, setShowModal] = useState(false);
  const [accounts, setAccounts] = useState<LinkedBankAccountDTO[]>([]);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Pressable style={styles.backButton} onPress={() => router.back()}>
          <FontAwesomeIcon icon={faArrowLeft} size={18} color={colors.bodyText} />
        </Pressable>
        <Text style={styles.headerTitle}>Linked Bank Accounts</Text>
        <View style={{ width: 40 }} />
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Pressable style={styles.linkRow} onPress={() => setShowModal(true)}>
          <View style={styles.linkIconContainer}>
            <FontAwesomeIcon icon={faPlus} size={18} color={colors.bodyText} />
          </View>
          <View style={styles.linkInfo}>
            <Text style={styles.linkTitle}>Link a Bank Account</Text>
            <Text style={styles.linkSubtitle}>Add money to your Transfera account</Text>
          </View>
          <Text style={styles.chevron}>›</Text>
        </Pressable>
      </ScrollView>

      <LinkBankAccountModal
        visible={showModal}
        onClose={() => setShowModal(false)}
        onSuccess={(account) => {
          setAccounts([...accounts, account]);
          setShowModal(false);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 16,
  },
  backButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerTitle: { fontSize: 18, fontWeight: '700' },
  scrollContent: { padding: 20, gap: 12 },
  linkRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.card,
    borderRadius: 16,
    padding: 16,
    gap: 14,
  },
  linkIconContainer: {
    width: 44,
    height: 44,
    borderRadius: 22,
    backgroundColor: colors.background,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1.5,
    borderColor: colors.input,
  },
  linkInfo: { flex: 1 },
  linkTitle: { fontSize: 16, fontWeight: '700' },
  linkSubtitle: { fontSize: 14, color: colors.subtitleText, marginTop: 2 },
  chevron: { fontSize: 28, color: colors.subtitleText, lineHeight: 32, alignSelf: 'center' },
});
