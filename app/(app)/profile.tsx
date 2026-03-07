import { ScrollView, StyleSheet, View, Pressable, Text } from 'react-native';
import { colors } from '@/src/themes/colors';
import { router } from 'expo-router';

type RowProps = {
  icon: string;
  label: string;
  onPress: () => void;
  danger?: boolean;
};

function Row({ icon, label, onPress, danger }: RowProps) {
  return (
    <Pressable style={styles.row} onPress={onPress}>
      <View style={styles.rowLeft}>
        <Text style={styles.rowIcon}>{icon}</Text>
        <Text style={[styles.rowLabel, danger && styles.rowLabelDanger]}>{label}</Text>
      </View>
      <Text style={styles.rowChevron}>›</Text>
    </Pressable>
  );
}

export default function Profile() {
  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>
      <View style={styles.header}>
        <Pressable style={styles.backButton} onPress={() => router.back()}>
          <Text style={styles.backChevron}>‹</Text>
        </Pressable>
        <Text style={styles.headerTitle}>Profile</Text>
        <View style={{ width: 40 }} />
      </View>
      {/* Avatar Section */}
      <View style={styles.avatarSection}>
        <View style={styles.avatarCircle}>
          <Text style={styles.avatarInitials}>JD</Text>
        </View>
        <Text style={styles.userName}>John Doe</Text>
        <Text style={styles.userHandle}>$johndoe</Text>
      </View>
      {/*  End of Avatar Section  */}

      {/* Account Section*/}
      <Text style={styles.accountSection}>Account</Text>
      <View style={styles.card}>
        <Row icon="👤" label="Personal Info" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="🏦" label="Bank Accounts & Cards" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="↕️" label="Transfer Limits" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="✅" label="Verification" onPress={() => {}} />
      </View>
      {/* Settings Section */}
      <Text style={styles.sectionTitle}>Settings</Text>
      <View style={styles.card}>
        <Row icon="🔔" label="Notifications" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="🔒" label="Security & PIN" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="🛡️" label="Privacy" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon="🚪" label="Log Out" onPress={() => router.replace('/logout')} danger />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: { flex: 1, backgroundColor: colors.background },
  scrollContent: { padding: 20, paddingTop: 60, gap: 16, paddingBottom: 40 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  headerTitle: { fontSize: 20, fontWeight: '700' },
  backButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.card,
    alignItems: 'center',
    justifyContent: 'center',
  },
  backChevron: { fontSize: 28, color: colors.bodyText, lineHeight: 36 },
  avatarSection: { alignItems: 'center', paddingVertical: 24, gap: 6 },
  avatarCircle: {
    width: 88,
    height: 88,
    backgroundColor: colors.primary,
    borderRadius: 44,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 4,
  },
  avatarInitials: { fontSize: 32, fontWeight: '800', color: 'white' },
  userName: { fontSize: 22, fontWeight: '800' },
  userHandle: { fontSize: 15, fontWeight: '500', color: colors.subtitleText },
  accountSection: {},
  card: { backgroundColor: colors.card, borderRadius: 16, overflow: 'hidden' },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  rowLeft: { flexDirection: 'row', alignItems: 'center', gap: 14 },
  rowIcon: { fontSize: 20, width: 28, textAlign: 'center' },
  rowLabel: { fontSize: 16, fontWeight: '500' },
  rowLabelDanger: { color: colors.error },
  rowChevron: { fontSize: 22, color: colors.subtitleText, lineHeight: 24 },
  separator: {
    height: 1,
    backgroundColor: colors.background,
    marginLeft: 52,
  },
});
