import { Link } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { colors } from "@/src/themes/colors";
import { useState } from "react";

export default function Home() {
  const [error, setError] = useState("");
  return (
    <ScrollView
      style={styles.scroll}
      contentContainerStyle={styles.scrollContent}
    >
      <View style={styles.header}>
        <View>
          <Text style={styles.headerTitle}>Accounts</Text>
          <Text style={styles.headerSubtitle}>Good morning 👋</Text>
        </View>
        <View style={styles.profileIcon}>
          <Text style={styles.profileIconText}>👤</Text>
        </View>
      </View>

      {/* Balance Card */}
      <View style={styles.balanceCard}>
        <Text style={styles.balanceLabel}>Transfera Balance</Text>
        <Text style={styles.balanceAmount}>$1,000.00</Text>
        <View style={styles.balanceAction}>
          <Pressable
            style={styles.balanceButton}
            onPress={() => setError("Add money is not yet implemented")}
          >
            <Text style={styles.balanceButtonText}>Add Money</Text>
          </Pressable>
          <Pressable
            style={styles.balanceButton}
            onPress={() => setError("Cash out is not yet implemented")}
          >
            <Text style={styles.balanceButtonText}>Cash Out</Text>
          </Pressable>
        </View>
      </View>
      {error ? (
        <View style={styles.errorBox}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      ) : null}
      {/* Temporary Log out button*/}
      <Link href="/logout">
        <Text>Go to Logout</Text>
      </Link>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 8,
  },
  headerTitle: { fontSize: 28, fontWeight: "800" },
  headerSubtitle: { fontSize: 14, color: colors.subtitleText, marginTop: 2 },

  scroll: { flex: 1, backgroundColor: colors.background },
  scrollContent: { padding: 20, paddingTop: 60, gap: 12 },
  profileIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: colors.card,
    alignItems: "center",
    justifyContent: "center",
  },
  profileIconText: {
    fontSize: 18,
  },

  balanceCard: {
    borderRadius: 18,
    padding: 24,
    backgroundColor: "#1A1A1A",
    gap: 8,
  },
  balanceLabel: {
    color: "rgba(255,255,255,0.7)",
    fontSize: 14,
  },
  balanceAmount: {
    color: "white",
    fontSize: 36,
    fontWeight: "800",
  },
  balanceAction: { flexDirection: "row", gap: 10, marginTop: 12 },

  balanceButton: {
    flex: 1,
    height: 40,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(255,255,255,0.15)",
  },
  balanceButtonText: {
    color: "white",
    fontSize: 14,
    fontWeight: "600",
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: colors.errorBackground,
  },
  errorText: {
    color: colors.error,
    fontSize: 14,
    fontWeight: "600",
  },
});
