import { ScrollView, StyleSheet, View, Pressable, Text } from 'react-native';
import { colors } from '@/src/themes/colors';
import { router } from 'expo-router';
import {
  faArrowsUpDown,
  faBell,
  faBuildingColumns,
  faCircleCheck,
  faLock,
  faRightFromBracket,
  faShield,
  faUser,
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-native-fontawesome';
import { useSession } from '@/src/context/AuthContext';
import { useState, useEffect } from 'react';
import { getProfileDataRequest, ProfileData } from '@/src/services/profile.service';

type RowProps = {
  icon: IconDefinition;
  label: string;
  onPress: () => void;
  danger?: boolean;
};

function Row({ icon, label, onPress, danger }: RowProps) {
  return (
    <Pressable style={styles.row} onPress={onPress}>
      <View style={styles.rowLeft}>
        <View style={styles.rowIconContainer}>
          <FontAwesomeIcon icon={icon} size={18} color={danger ? colors.error : colors.bodyText} />
        </View>
        <Text style={[styles.rowLabel, danger && styles.rowLabelDanger]}>{label}</Text>
      </View>
      <Text style={styles.rowChevron}>›</Text>
    </Pressable>
  );
}

export default function Profile() {
  const { session } = useSession();
  const [profile, setProfile] = useState<ProfileData | null>(null);

  useEffect(() => {
    if (!session) return;
    getProfileDataRequest(session).then(setProfile).catch(console.error);
  }, [session]);

  return (
    <ScrollView style={styles.scroll} contentContainerStyle={styles.scrollContent}>
      <View style={styles.header}>
        <Pressable style={styles.backButton} onPress={() => router.back()}>
          <Text style={styles.backChevron}>✕</Text>
        </Pressable>
        <Text style={styles.headerTitle}>Profile</Text>
        <View style={{ width: 40 }} />
      </View>

      {/* Avatar Section */}
      <View style={styles.avatarSection}>
        <View style={styles.avatarCircle}>
          <Text style={styles.avatarInitials}>
            {profile ? `${profile.firstName[0]}${profile.lastName[0]}` : '??'}
          </Text>
        </View>
        <Text style={styles.userName}>
          {profile ? `${profile.firstName} ${profile.lastName}` : ''}
        </Text>
        <Text style={styles.userHandle}>{profile ? `$${profile.username}` : ''}</Text>
      </View>
      {/*  End of Avatar Section  */}

      {/* Account Section*/}
      <Text style={styles.sectionTitle}>Account</Text>
      <View style={styles.card}>
        <Row icon={faUser} label="Personal Info" onPress={() => {}} />
        <View style={styles.separator} />
        <Row
          icon={faBuildingColumns}
          label="Bank Accounts & Cards"
          onPress={() => router.push('/profile/linked-bank-accounts')}
        />
        <View style={styles.separator} />
        <Row icon={faArrowsUpDown} label="Transfer Limits" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon={faCircleCheck} label="Verification" onPress={() => {}} />
      </View>
      {/* End Account Section*/}

      {/* Settings Section */}
      <Text style={styles.sectionTitle}>Settings</Text>
      <View style={styles.card}>
        <Row icon={faBell} label="Notifications" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon={faShield} label="Security & PIN" onPress={() => {}} />
        <View style={styles.separator} />
        <Row icon={faLock} label="Privacy" onPress={() => {}} />
        <View style={styles.separator} />
        <Row
          icon={faRightFromBracket}
          label="Log Out"
          onPress={() => router.replace('/logout')}
          danger
        />
      </View>
      {/* End Settings Section */}
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
  avatarInitials: { fontSize: 32, fontWeight: '800', color: colors.primaryText },
  userName: { fontSize: 22, fontWeight: '800' },
  userHandle: { fontSize: 15, fontWeight: '500', color: colors.subtitleText },
  card: { backgroundColor: colors.card, borderRadius: 16, overflow: 'hidden' },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  rowLeft: { flexDirection: 'row', alignItems: 'center', gap: 14 },
  rowIconContainer: { width: 28, alignItems: 'center', justifyContent: 'center' },
  rowLabel: { fontSize: 16, fontWeight: '500' },
  rowLabelDanger: { color: colors.error },
  rowChevron: { fontSize: 22, color: colors.subtitleText, lineHeight: 24 },
  separator: {
    height: 1,
    backgroundColor: colors.background,
    marginLeft: 52,
  },
  sectionTitle: {
    fontSize: 13,
    fontWeight: '700',
    color: colors.subtitleText,
    textTransform: 'uppercase',
    letterSpacing: 1,
    marginBottom: -8,
    marginLeft: 4,
  },
});
