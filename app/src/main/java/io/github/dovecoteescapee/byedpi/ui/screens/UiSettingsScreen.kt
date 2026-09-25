@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.screens

import android.content.SharedPreferences
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.ui.components.EditTextPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.ListPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.SettingsGroup
import io.github.dovecoteescapee.byedpi.ui.components.SwitchPreferenceItem
import io.github.dovecoteescapee.byedpi.utility.checkIp

@Composable
fun UiSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var proxyIp by remember(prefs) { mutableStateOf(prefs.getString("byedpi_proxy_ip", "127.0.0.1") ?: "127.0.0.1") }
    var proxyPort by remember(prefs) { mutableStateOf(prefs.getString("byedpi_proxy_port", "1080") ?: "1080") }
    var maxConnections by remember(prefs) { mutableStateOf(prefs.getString("byedpi_max_connections", "512") ?: "512") }
    var bufferSize by remember(prefs) { mutableStateOf(prefs.getString("byedpi_buffer_size", "16384") ?: "16384") }
    var noDomain by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_no_domain", false)) }
    var noIpv6 by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_no_ipv6", false)) }
    var connIp by remember(prefs) { mutableStateOf(prefs.getString("byedpi_conn_ip", "") ?: "") }
    var waitSend by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_wait_send", false)) }
    var awaitInt by remember(prefs) { mutableStateOf(prefs.getString("byedpi_await_int", "") ?: "") }
    var tcpFastOpen by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_tcp_fast_open", false)) }

    var hostsMode by remember(prefs) { mutableStateOf(prefs.getString("byedpi_hosts_mode", "none") ?: "none") }
    var hostsBlacklist by remember(prefs) { mutableStateOf(prefs.getString("byedpi_hosts_blacklist", "") ?: "") }
    var hostsWhitelist by remember(prefs) { mutableStateOf(prefs.getString("byedpi_hosts_whitelist", "") ?: "") }
    var defaultTtl by remember(prefs) { mutableStateOf(prefs.getString("byedpi_default_ttl", "0") ?: "0") }
    var desyncMethod by remember(prefs) { mutableStateOf(prefs.getString("byedpi_desync_method", "none") ?: "none") }
    var splitPosition by remember(prefs) { mutableStateOf(prefs.getString("byedpi_split_position", "0") ?: "0") }
    var splitAtHost by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_split_at_host", false)) }
    var dropSack by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_drop_sack", false)) }
    var fakeTtl by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_ttl", "0") ?: "0") }
    var fakeOffset by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_offset", "0") ?: "0") }
    var fakeSni by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_sni", "") ?: "") }
    var md5sig by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_md5sig", false)) }
    var fakeData by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_data", "") ?: "") }
    var fakeTlsMod by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_tls_mod", "") ?: "") }
    var tlsminor by remember(prefs) { mutableStateOf(prefs.getString("byedpi_tlsminor", "") ?: "") }
    var round by remember(prefs) { mutableStateOf(prefs.getString("byedpi_round", "") ?: "") }
    var oobData by remember(prefs) { mutableStateOf(prefs.getString("byedpi_oob_data", "") ?: "") }

    var desyncHttp by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_desync_http", true)) }
    var desyncHttps by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_desync_https", true)) }
    var desyncUdp by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_desync_udp", false)) }

    var hostMixedCase by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_host_mixed_case", false)) }
    var domainMixedCase by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_domain_mixed_case", false)) }
    var hostRemoveSpaces by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_host_remove_spaces", false)) }

    var tlsrecEnabled by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_tlsrec_enabled", false)) }
    var tlsrecPosition by remember(prefs) { mutableStateOf(prefs.getString("byedpi_tlsrec_position", "0") ?: "0") }
    var tlsrecAtSni by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_tlsrec_at_sni", false)) }

    var udpFakeCount by remember(prefs) { mutableStateOf(prefs.getString("byedpi_udp_fake_count", "0") ?: "0") }

    var pf by remember(prefs) { mutableStateOf(prefs.getString("byedpi_pf", "") ?: "") }
    var ipset by remember(prefs) { mutableStateOf(prefs.getString("byedpi_ipset", "") ?: "") }

    var autoVal by remember(prefs) { mutableStateOf(prefs.getString("byedpi_auto", "") ?: "") }
    var autoMode by remember(prefs) { mutableStateOf(prefs.getString("byedpi_auto_mode", "none") ?: "none") }
    var timeout by remember(prefs) { mutableStateOf(prefs.getString("byedpi_timeout", "") ?: "") }

    val hasHostList = hostsMode == "blacklist" || hostsMode == "whitelist"
    val desyncCount = if (hasHostList) {
        16
    } else {
        15
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            SettingsGroup(title = stringResource(R.string.byedpi_proxy)) {
                EditTextPreferenceItem(
                    title = stringResource(R.string.bye_dpi_proxy_ip_setting),
                    value = proxyIp,
                    index = 0,
                    count = 10,
                    onValueChange = {
                        proxyIp = it
                        prefs.edit { putString("byedpi_proxy_ip", it) }
                    },
                    validate = { checkIp(it) },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_proxy_port_setting),
                    value = proxyPort,
                    index = 1,
                    count = 10,
                    onValueChange = {
                        proxyPort = it
                        prefs.edit { putString("byedpi_proxy_port", it) }
                    },
                    validate = { it.toIntOrNull()?.let { p -> p in 1..65535 } ?: false },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_max_connections_setting),
                    value = maxConnections,
                    index = 2,
                    count = 10,
                    onValueChange = {
                        maxConnections = it
                        prefs.edit { putString("byedpi_max_connections", it) }
                    },
                    validate = { it.toIntOrNull()?.let { c -> c in 1..Short.MAX_VALUE } ?: false },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_buffer_size_setting),
                    value = bufferSize,
                    index = 3,
                    count = 10,
                    onValueChange = {
                        bufferSize = it
                        prefs.edit { putString("byedpi_buffer_size", it) }
                    },
                    validate = { it.toIntOrNull()?.let { b -> b in 1..(Int.MAX_VALUE / 4) } ?: false },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_no_domain_setting),
                    checked = noDomain,
                    index = 4,
                    count = 10,
                    onCheckedChange = {
                        noDomain = it
                        prefs.edit { putBoolean("byedpi_no_domain", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_no_ipv6_setting),
                    checked = noIpv6,
                    index = 5,
                    count = 10,
                    onCheckedChange = {
                        noIpv6 = it
                        prefs.edit { putBoolean("byedpi_no_ipv6", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_conn_ip_setting),
                    value = connIp,
                    index = 6,
                    count = 10,
                    onValueChange = {
                        connIp = it
                        prefs.edit { putString("byedpi_conn_ip", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_wait_send_setting),
                    checked = waitSend,
                    index = 7,
                    count = 10,
                    onCheckedChange = {
                        waitSend = it
                        prefs.edit { putBoolean("byedpi_wait_send", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_await_int_setting),
                    value = awaitInt,
                    index = 8,
                    count = 10,
                    onValueChange = {
                        awaitInt = it
                        prefs.edit { putString("byedpi_await_int", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_tcp_fast_open_setting),
                    checked = tcpFastOpen,
                    index = 9,
                    count = 10,
                    onCheckedChange = {
                        tcpFastOpen = it
                        prefs.edit { putBoolean("byedpi_tcp_fast_open", it) }
                    },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.byedpi_desync)) {
                var currentIndex = 0
                ListPreferenceItem(
                    title = stringResource(R.string.byedpi_hosts_mode_setting),
                    selectedValue = hostsMode,
                    entries = stringArrayResource(R.array.byedpi_hosts_modes).toList(),
                    entryValues = stringArrayResource(R.array.byedpi_hosts_modes_entries).toList(),
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        hostsMode = it
                        prefs.edit { putString("byedpi_hosts_mode", it) }
                    },
                )
                if (hostsMode == "blacklist") {
                    EditTextPreferenceItem(
                        title = stringResource(R.string.byedpi_hosts_blacklist_setting),
                        value = hostsBlacklist,
                        index = currentIndex++,
                        count = desyncCount,
                        onValueChange = {
                            hostsBlacklist = it
                            prefs.edit { putString("byedpi_hosts_blacklist", it) }
                        },
                    )
                }
                if (hostsMode == "whitelist") {
                    EditTextPreferenceItem(
                        title = stringResource(R.string.byedpi_hosts_whitelist_setting),
                        value = hostsWhitelist,
                        index = currentIndex++,
                        count = desyncCount,
                        onValueChange = {
                            hostsWhitelist = it
                            prefs.edit { putString("byedpi_hosts_whitelist", it) }
                        },
                    )
                }
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_default_ttl_setting),
                    value = defaultTtl,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        defaultTtl = it
                        prefs.edit { putString("byedpi_default_ttl", it) }
                    },
                    validate = { it.toIntOrNull()?.let { t -> t in 0..255 } ?: false },
                )
                ListPreferenceItem(
                    title = stringResource(R.string.byedpi_desync_method_setting),
                    selectedValue = desyncMethod,
                    entries = stringArrayResource(R.array.byedpi_desync_methods).toList(),
                    entryValues = stringArrayResource(R.array.byedpi_desync_methods_entries).toList(),
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        desyncMethod = it
                        prefs.edit { putString("byedpi_desync_method", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_split_position_setting),
                    value = splitPosition,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        splitPosition = it
                        prefs.edit { putString("byedpi_split_position", it) }
                    },
                    validate = { it.toIntOrNull() != null },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_split_at_host_setting),
                    checked = splitAtHost,
                    index = currentIndex++,
                    count = desyncCount,
                    onCheckedChange = {
                        splitAtHost = it
                        prefs.edit { putBoolean("byedpi_split_at_host", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_drop_sack_setting),
                    checked = dropSack,
                    index = currentIndex++,
                    count = desyncCount,
                    onCheckedChange = {
                        dropSack = it
                        prefs.edit { putBoolean("byedpi_drop_sack", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_fake_ttl_setting),
                    value = fakeTtl,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        fakeTtl = it
                        prefs.edit { putString("byedpi_fake_ttl", it) }
                    },
                    validate = { it.toIntOrNull()?.let { t -> t in 1..255 } ?: false },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_fake_offset_setting),
                    value = fakeOffset,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        fakeOffset = it
                        prefs.edit { putString("byedpi_fake_offset", it) }
                    },
                    validate = { it.toIntOrNull() != null },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.sni_of_fake_packet),
                    value = fakeSni,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        fakeSni = it
                        prefs.edit { putString("byedpi_fake_sni", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_md5sig_setting),
                    checked = md5sig,
                    index = currentIndex++,
                    count = desyncCount,
                    onCheckedChange = {
                        md5sig = it
                        prefs.edit { putBoolean("byedpi_md5sig", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_fake_data_setting),
                    value = fakeData,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        fakeData = it
                        prefs.edit { putString("byedpi_fake_data", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_fake_tls_mod_setting),
                    value = fakeTlsMod,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        fakeTlsMod = it
                        prefs.edit { putString("byedpi_fake_tls_mod", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_tlsminor_setting),
                    value = tlsminor,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        tlsminor = it
                        prefs.edit { putString("byedpi_tlsminor", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_round_setting),
                    value = round,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        round = it
                        prefs.edit { putString("byedpi_round", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.oob_data),
                    value = oobData,
                    index = currentIndex++,
                    count = desyncCount,
                    onValueChange = {
                        oobData = it
                        prefs.edit { putString("byedpi_oob_data", it) }
                    },
                    validate = { it.length <= 1 },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.byedpi_protocols_category)) {
                SwitchPreferenceItem(
                    title = stringResource(R.string.desync_http),
                    checked = desyncHttp,
                    index = 0,
                    count = 3,
                    onCheckedChange = {
                        desyncHttp = it
                        prefs.edit { putBoolean("byedpi_desync_http", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.desync_https),
                    checked = desyncHttps,
                    index = 1,
                    count = 3,
                    onCheckedChange = {
                        desyncHttps = it
                        prefs.edit { putBoolean("byedpi_desync_https", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.desync_udp),
                    checked = desyncUdp,
                    index = 2,
                    count = 3,
                    onCheckedChange = {
                        desyncUdp = it
                        prefs.edit { putBoolean("byedpi_desync_udp", it) }
                    },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.desync_http_category)) {
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_host_mixed_case_setting),
                    checked = hostMixedCase,
                    index = 0,
                    count = 3,
                    onCheckedChange = {
                        hostMixedCase = it
                        prefs.edit { putBoolean("byedpi_host_mixed_case", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_domain_mixed_case_setting),
                    checked = domainMixedCase,
                    index = 1,
                    count = 3,
                    onCheckedChange = {
                        domainMixedCase = it
                        prefs.edit { putBoolean("byedpi_domain_mixed_case", it) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_host_remove_spaces_setting),
                    checked = hostRemoveSpaces,
                    index = 2,
                    count = 3,
                    onCheckedChange = {
                        hostRemoveSpaces = it
                        prefs.edit { putBoolean("byedpi_host_remove_spaces", it) }
                    },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.desync_https_category)) {
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_tlsrec_enabled_setting),
                    checked = tlsrecEnabled,
                    index = 0,
                    count = 3,
                    onCheckedChange = {
                        tlsrecEnabled = it
                        prefs.edit { putBoolean("byedpi_tlsrec_enabled", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_tlsrec_position_setting),
                    value = tlsrecPosition,
                    enabled = tlsrecEnabled,
                    index = 1,
                    count = 3,
                    onValueChange = {
                        tlsrecPosition = it
                        prefs.edit { putString("byedpi_tlsrec_position", it) }
                    },
                    validate = { it.toIntOrNull() != null },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.byedpi_tlsrec_at_sni_setting),
                    checked = tlsrecAtSni,
                    enabled = tlsrecEnabled,
                    index = 2,
                    count = 3,
                    onCheckedChange = {
                        tlsrecAtSni = it
                        prefs.edit { putBoolean("byedpi_tlsrec_at_sni", it) }
                    },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.desync_udp_category)) {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_udp_fake_count),
                    value = udpFakeCount,
                    index = 0,
                    count = 1,
                    onValueChange = {
                        udpFakeCount = it
                        prefs.edit { putString("byedpi_udp_fake_count", it) }
                    },
                    validate = { it.toIntOrNull()?.let { c -> c >= 0 } ?: false },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.byedpi_filter_category)) {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_pf_setting),
                    value = pf,
                    index = 0,
                    count = 2,
                    onValueChange = {
                        pf = it
                        prefs.edit { putString("byedpi_pf", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_ipset_setting),
                    value = ipset,
                    index = 1,
                    count = 2,
                    onValueChange = {
                        ipset = it
                        prefs.edit { putString("byedpi_ipset", it) }
                    },
                )
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.byedpi_auto_category)) {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_auto_setting),
                    value = autoVal,
                    index = 0,
                    count = 3,
                    onValueChange = {
                        autoVal = it
                        prefs.edit { putString("byedpi_auto", it) }
                    },
                )
                ListPreferenceItem(
                    title = stringResource(R.string.byedpi_auto_mode_setting),
                    selectedValue = autoMode,
                    entries = stringArrayResource(R.array.byedpi_auto_modes).toList(),
                    entryValues = stringArrayResource(R.array.byedpi_auto_modes_entries).toList(),
                    index = 1,
                    count = 3,
                    onValueChange = {
                        autoMode = it
                        prefs.edit { putString("byedpi_auto_mode", it) }
                    },
                )
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_timeout_setting),
                    value = timeout,
                    index = 2,
                    count = 3,
                    onValueChange = {
                        timeout = it
                        prefs.edit { putString("byedpi_timeout", it) }
                    },
                )
            }
        }
    }
}
