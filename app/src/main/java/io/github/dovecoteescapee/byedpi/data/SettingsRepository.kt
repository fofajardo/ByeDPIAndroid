package io.github.dovecoteescapee.byedpi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File

val Context.appSettingsDataStore: DataStore<AppSettings> by dataStore(
    fileName = "app_settings.json",
    serializer = AppSettingsSerializer,
)

class SettingsRepository(
    private val context: Context,
) {
    val settingsFlow: Flow<AppSettings> = context.appSettingsDataStore.data

    suspend fun getSettings(): AppSettings = context.appSettingsDataStore.data.first()

    fun getSettingsBlocking(): AppSettings = runBlocking { getSettings() }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        context.appSettingsDataStore.updateData(transform)
    }

    suspend fun exportJson(): String {
        val current = getSettings()
        return AppSettingsSerializer.json.encodeToString(AppSettings.serializer(), current)
    }

    suspend fun importJson(jsonString: String) {
        val imported = AppSettingsSerializer.json.decodeFromString<AppSettings>(jsonString)
        context.appSettingsDataStore.updateData { imported }
    }

    suspend fun resetToDefault() {
        context.appSettingsDataStore.updateData { AppSettings() }
    }

    suspend fun migrateFromSharedPreferencesIfNeeded() {
        val file = File(context.filesDir, "datastore/app_settings.json")
        if (file.exists()) {
            return
        }
        val sp = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
        if (sp.all.isEmpty()) {
            return
        }

        context.appSettingsDataStore.updateData {
            AppSettings(
                theme = sp.getString("app_theme", "system") ?: "system",
                amoledTheme = sp.getBoolean("amoled_theme", false),
                mode = sp.getString("byedpi_mode", "vpn") ?: "vpn",
                autostart = sp.getBoolean("autostart", false),
                dnsIp = sp.getString("dns_ip", "1.1.1.1") ?: "1.1.1.1",
                ipv6Enable = sp.getBoolean("ipv6_enable", false),
                vpnFilterMode = sp.getString("vpn_filter_mode", "blacklist") ?: "blacklist",
                vpnFilteredApps = sp.getStringSet("vpn_filtered_apps", emptySet()) ?: emptySet(),
                wasRunning = sp.getBoolean("was_running", false),
                engine =
                    EngineSettings(
                        enableCmdSettings = sp.getBoolean("byedpi_enable_cmd_settings", false),
                        cmdArgs = sp.getString("byedpi_cmd_args", "") ?: "",
                        proxyIp = sp.getString("byedpi_proxy_ip", "127.0.0.1") ?: "127.0.0.1",
                        proxyPort = sp.getString("byedpi_proxy_port", "1080") ?: "1080",
                        maxConnections = sp.getString("byedpi_max_connections", "512") ?: "512",
                        bufferSize = sp.getString("byedpi_buffer_size", "16384") ?: "16384",
                        connIp = sp.getString("byedpi_conn_ip", "") ?: "",
                        noIpv6 = sp.getBoolean("byedpi_no_ipv6", false),
                        tcpFastOpen = sp.getBoolean("byedpi_tcp_fast_open", false),
                        noDomain = sp.getBoolean("byedpi_no_domain", false),
                        waitSend = sp.getBoolean("byedpi_wait_send", false),
                        awaitInt = sp.getString("byedpi_await_int", "") ?: "",
                        defaultTtl = sp.getString("byedpi_default_ttl", "0") ?: "0",
                        desyncMethod = sp.getString("byedpi_desync_method", "none") ?: "none",
                        splitPosition = sp.getString("byedpi_split_position", "0") ?: "0",
                        splitAtHost = sp.getBoolean("byedpi_split_at_host", false),
                        dropSack = sp.getBoolean("byedpi_drop_sack", false),
                        fakeTtl = sp.getString("byedpi_fake_ttl", "0") ?: "0",
                        fakeOffset = sp.getString("byedpi_fake_offset", "0") ?: "0",
                        fakeSni = sp.getString("byedpi_fake_sni", "") ?: "",
                        md5sig = sp.getBoolean("byedpi_md5sig", false),
                        fakeData = sp.getString("byedpi_fake_data", "") ?: "",
                        fakeTlsMod = sp.getString("byedpi_fake_tls_mod", "") ?: "",
                        tlsminor = sp.getString("byedpi_tlsminor", "") ?: "",
                        round = sp.getString("byedpi_round", "") ?: "",
                        oobData = sp.getString("byedpi_oob_data", "") ?: "",
                        desyncHttp = sp.getBoolean("byedpi_desync_http", true),
                        desyncHttps = sp.getBoolean("byedpi_desync_https", true),
                        desyncUdp = sp.getBoolean("byedpi_desync_udp", false),
                        hostMixedCase = sp.getBoolean("byedpi_host_mixed_case", false),
                        domainMixedCase = sp.getBoolean("byedpi_domain_mixed_case", false),
                        hostRemoveSpaces = sp.getBoolean("byedpi_host_remove_spaces", false),
                        tlsrecEnabled = sp.getBoolean("byedpi_tlsrec_enabled", false),
                        tlsrecPosition = sp.getString("byedpi_tlsrec_position", "0") ?: "0",
                        tlsrecAtSni = sp.getBoolean("byedpi_tlsrec_at_sni", false),
                        udpFakeCount = sp.getString("byedpi_udp_fake_count", "0") ?: "0",
                        hostsMode = sp.getString("byedpi_hosts_mode", "none") ?: "none",
                        hostsBlacklist = sp.getString("byedpi_hosts_blacklist", "") ?: "",
                        hostsWhitelist = sp.getString("byedpi_hosts_whitelist", "") ?: "",
                        pf = sp.getString("byedpi_pf", "") ?: "",
                        ipset = sp.getString("byedpi_ipset", "") ?: "",
                        auto = sp.getString("byedpi_auto", "") ?: "",
                        autoMode = sp.getString("byedpi_auto_mode", "none") ?: "none",
                        timeout = sp.getString("byedpi_timeout", "") ?: "",
                    ),
            )
        }
    }
}
