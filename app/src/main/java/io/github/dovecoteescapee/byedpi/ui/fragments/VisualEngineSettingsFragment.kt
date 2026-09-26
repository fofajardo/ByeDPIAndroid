@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.fragments

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import io.github.dovecoteescapee.byedpi.ui.components.Section
import io.github.dovecoteescapee.byedpi.ui.components.SwitchPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop
import io.github.dovecoteescapee.byedpi.utility.checkIp

@Composable
fun UiSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
    headerContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    var proxyIp by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_proxy_ip", "127.0.0.1") ?: "127.0.0.1"
        )
    }
    var proxyPort by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_proxy_port", "1080") ?: "1080"
        )
    }
    var maxConnections by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_max_connections", "512"
            ) ?: "512"
        )
    }
    var bufferSize by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_buffer_size", "16384"
            ) ?: "16384"
        )
    }
    var noDomain by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_no_domain", false)) }
    var noIpv6 by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_no_ipv6", false)) }
    var connIp by remember(prefs) { mutableStateOf(prefs.getString("byedpi_conn_ip", "") ?: "") }
    var waitSend by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_wait_send", false)) }
    var awaitInt by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_await_int", "") ?: ""
        )
    }
    var tcpFastOpen by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_tcp_fast_open", false
            )
        )
    }

    var hostsMode by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_hosts_mode", "none") ?: "none"
        )
    }
    var hostsBlacklist by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_hosts_blacklist", ""
            ) ?: ""
        )
    }
    var hostsWhitelist by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_hosts_whitelist", ""
            ) ?: ""
        )
    }
    var defaultTtl by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_default_ttl", "0") ?: "0"
        )
    }
    var desyncMethod by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_desync_method", "none"
            ) ?: "none"
        )
    }
    var splitPosition by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_split_position", "0"
            ) ?: "0"
        )
    }
    var splitAtHost by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_split_at_host", false
            )
        )
    }
    var dropSack by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_drop_sack", false)) }
    var fakeTtl by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_fake_ttl", "0") ?: "0"
        )
    }
    var fakeOffset by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_fake_offset", "0") ?: "0"
        )
    }
    var fakeSni by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_sni", "") ?: "") }
    var md5sig by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_md5sig", false)) }
    var fakeData by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_fake_data", "") ?: ""
        )
    }
    var fakeTlsMod by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_fake_tls_mod", "") ?: ""
        )
    }
    var tlsminor by remember(prefs) { mutableStateOf(prefs.getString("byedpi_tlsminor", "") ?: "") }
    var round by remember(prefs) { mutableStateOf(prefs.getString("byedpi_round", "") ?: "") }
    var oobData by remember(prefs) { mutableStateOf(prefs.getString("byedpi_oob_data", "") ?: "") }

    var desyncHttp by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_desync_http", true
            )
        )
    }
    var desyncHttps by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_desync_https", true
            )
        )
    }
    var desyncUdp by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_desync_udp", false
            )
        )
    }

    var hostMixedCase by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_host_mixed_case", false
            )
        )
    }
    var domainMixedCase by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_domain_mixed_case", false
            )
        )
    }
    var hostRemoveSpaces by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_host_remove_spaces", false
            )
        )
    }

    var tlsrecEnabled by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_tlsrec_enabled", false
            )
        )
    }
    var tlsrecPosition by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_tlsrec_position", "0"
            ) ?: "0"
        )
    }
    var tlsrecAtSni by remember(prefs) {
        mutableStateOf(
            prefs.getBoolean(
                "byedpi_tlsrec_at_sni", false
            )
        )
    }

    var udpFakeCount by remember(prefs) {
        mutableStateOf(
            prefs.getString(
                "byedpi_udp_fake_count", "0"
            ) ?: "0"
        )
    }

    var pf by remember(prefs) { mutableStateOf(prefs.getString("byedpi_pf", "") ?: "") }
    var ipset by remember(prefs) { mutableStateOf(prefs.getString("byedpi_ipset", "") ?: "") }

    var autoVal by remember(prefs) { mutableStateOf(prefs.getString("byedpi_auto", "") ?: "") }
    var autoMode by remember(prefs) {
        mutableStateOf(
            prefs.getString("byedpi_auto_mode", "none") ?: "none"
        )
    }
    var timeout by remember(prefs) { mutableStateOf(prefs.getString("byedpi_timeout", "") ?: "") }

    val hasHostList = hostsMode == "blacklist" || hostsMode == "whitelist"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        if (headerContent != null) {
            headerContent()
        }

        Section(title = stringResource(R.string.byedpi_proxy)) {
            EditTextPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.bye_dpi_proxy_ip_setting),
                value = proxyIp,
                onValueChange = {
                    proxyIp = it
                    prefs.edit { putString("byedpi_proxy_ip", it) }
                },
                validate = { checkIp(it) },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_proxy_port_setting),
                value = proxyPort,
                onValueChange = {
                    proxyPort = it
                    prefs.edit { putString("byedpi_proxy_port", it) }
                },
                validate = { it.toIntOrNull()?.let { p -> p in 1..65535 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_max_connections_setting),
                value = maxConnections,
                onValueChange = {
                    maxConnections = it
                    prefs.edit { putString("byedpi_max_connections", it) }
                },
                validate = { it.toIntOrNull()?.let { c -> c in 1..Short.MAX_VALUE } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_buffer_size_setting),
                value = bufferSize,
                onValueChange = {
                    bufferSize = it
                    prefs.edit { putString("byedpi_buffer_size", it) }
                },
                validate = { it.toIntOrNull()?.let { b -> b in 1..(Int.MAX_VALUE / 4) } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_conn_ip_setting),
                value = connIp,
                onValueChange = {
                    connIp = it
                    prefs.edit { putString("byedpi_conn_ip", it) }
                },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_no_ipv6_setting),
                summary = stringResource(R.string.byedpi_no_ipv6_summary),
                checked = noIpv6,
                onCheckedChange = {
                    noIpv6 = it
                    prefs.edit { putBoolean("byedpi_no_ipv6", it) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_connection_category)) {
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_tcp_fast_open_setting),
                summary = stringResource(R.string.byedpi_tcp_fast_open_summary),
                checked = tcpFastOpen,
                onCheckedChange = {
                    tcpFastOpen = it
                    prefs.edit { putBoolean("byedpi_tcp_fast_open", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_no_domain_setting),
                summary = stringResource(R.string.byedpi_no_domain_summary),
                checked = noDomain,
                onCheckedChange = {
                    noDomain = it
                    prefs.edit { putBoolean("byedpi_no_domain", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_wait_send_setting),
                summary = stringResource(R.string.byedpi_wait_send_summary),
                checked = waitSend,
                onCheckedChange = {
                    waitSend = it
                    prefs.edit { putBoolean("byedpi_wait_send", it) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_await_int_setting),
                summary = stringResource(R.string.byedpi_await_int_summary),
                value = awaitInt,
                onValueChange = {
                    awaitInt = it
                    prefs.edit { putString("byedpi_await_int", it) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_desync)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_desync_method_setting),
                selectedValue = desyncMethod,
                entries = stringArrayResource(R.array.byedpi_desync_methods).toList(),
                entryValues = stringArrayResource(R.array.byedpi_desync_methods_entries).toList(),
                onValueChange = {
                    desyncMethod = it
                    prefs.edit { putString("byedpi_desync_method", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_split_position_setting),
                value = splitPosition,
                onValueChange = {
                    splitPosition = it
                    prefs.edit { putString("byedpi_split_position", it) }
                },
                validate = { it.toIntOrNull() != null },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_split_at_host_setting),
                checked = splitAtHost,
                onCheckedChange = {
                    splitAtHost = it
                    prefs.edit { putBoolean("byedpi_split_at_host", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_default_ttl_setting),
                summary = stringResource(R.string.byedpi_default_ttl_summary),
                value = defaultTtl,
                onValueChange = {
                    defaultTtl = it
                    prefs.edit { putString("byedpi_default_ttl", it) }
                },
                validate = { it.toIntOrNull()?.let { t -> t in 0..255 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_ttl_setting),
                summary = stringResource(R.string.byedpi_fake_ttl_summary),
                value = fakeTtl,
                onValueChange = {
                    fakeTtl = it
                    prefs.edit { putString("byedpi_fake_ttl", it) }
                },
                validate = { it.toIntOrNull()?.let { t -> t in 1..255 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_offset_setting),
                value = fakeOffset,
                onValueChange = {
                    fakeOffset = it
                    prefs.edit { putString("byedpi_fake_offset", it) }
                },
                validate = { it.toIntOrNull() != null },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.sni_of_fake_packet),
                summary = stringResource(R.string.sni_of_fake_packet_summary),
                value = fakeSni,
                onValueChange = {
                    fakeSni = it
                    prefs.edit { putString("byedpi_fake_sni", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_md5sig_setting),
                summary = stringResource(R.string.byedpi_md5sig_summary),
                checked = md5sig,
                onCheckedChange = {
                    md5sig = it
                    prefs.edit { putBoolean("byedpi_md5sig", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_data_setting),
                value = fakeData,
                onValueChange = {
                    fakeData = it
                    prefs.edit { putString("byedpi_fake_data", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_tls_mod_setting),
                value = fakeTlsMod,
                onValueChange = {
                    fakeTlsMod = it
                    prefs.edit { putString("byedpi_fake_tls_mod", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_tlsminor_setting),
                value = tlsminor,
                onValueChange = {
                    tlsminor = it
                    prefs.edit { putString("byedpi_tlsminor", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_round_setting),
                summary = stringResource(R.string.byedpi_round_summary),
                value = round,
                onValueChange = {
                    round = it
                    prefs.edit { putString("byedpi_round", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.oob_data),
                value = oobData,
                onValueChange = {
                    oobData = it
                    prefs.edit { putString("byedpi_oob_data", it) }
                },
                validate = { it.length <= 1 },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_drop_sack_setting),
                checked = dropSack,
                onCheckedChange = {
                    dropSack = it
                    prefs.edit { putBoolean("byedpi_drop_sack", it) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_protocols_category)) {
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.desync_http),
                checked = desyncHttp,
                onCheckedChange = {
                    desyncHttp = it
                    prefs.edit { putBoolean("byedpi_desync_http", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.desync_https),
                checked = desyncHttps,
                onCheckedChange = {
                    desyncHttps = it
                    prefs.edit { putBoolean("byedpi_desync_https", it) }
                },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.desync_udp),
                checked = desyncUdp,
                onCheckedChange = {
                    desyncUdp = it
                    prefs.edit { putBoolean("byedpi_desync_udp", it) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_protocol_mod_category)) {
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_host_mixed_case_setting),
                summary = stringResource(R.string.byedpi_host_mixed_case_summary),
                checked = hostMixedCase,
                onCheckedChange = {
                    hostMixedCase = it
                    prefs.edit { putBoolean("byedpi_host_mixed_case", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_domain_mixed_case_setting),
                summary = stringResource(R.string.byedpi_domain_mixed_case_summary),
                checked = domainMixedCase,
                onCheckedChange = {
                    domainMixedCase = it
                    prefs.edit { putBoolean("byedpi_domain_mixed_case", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_host_remove_spaces_setting),
                checked = hostRemoveSpaces,
                onCheckedChange = {
                    hostRemoveSpaces = it
                    prefs.edit { putBoolean("byedpi_host_remove_spaces", it) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_tlsrec_enabled_setting),
                summary = stringResource(R.string.byedpi_tlsrec_enabled_summary),
                checked = tlsrecEnabled,
                onCheckedChange = {
                    tlsrecEnabled = it
                    prefs.edit { putBoolean("byedpi_tlsrec_enabled", it) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_tlsrec_position_setting),
                summary = stringResource(R.string.byedpi_tlsrec_position_summary),
                value = tlsrecPosition,
                enabled = tlsrecEnabled,
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
                onCheckedChange = {
                    tlsrecAtSni = it
                    prefs.edit { putBoolean("byedpi_tlsrec_at_sni", it) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_udp_fake_count),
                value = udpFakeCount,
                onValueChange = {
                    udpFakeCount = it
                    prefs.edit { putString("byedpi_udp_fake_count", it) }
                },
                validate = { it.toIntOrNull()?.let { c -> c >= 0 } ?: false },
            )
        }

        Section(title = stringResource(R.string.byedpi_filter_category)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_hosts_mode_setting),
                selectedValue = hostsMode,
                entries = stringArrayResource(R.array.byedpi_hosts_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_hosts_modes_entries).toList(),
                onValueChange = {
                    hostsMode = it
                    prefs.edit { putString("byedpi_hosts_mode", it) }
                },
            )
            if (hostsMode == "blacklist") {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_hosts_blacklist_setting),
                    value = hostsBlacklist,
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
                    onValueChange = {
                        hostsWhitelist = it
                        prefs.edit { putString("byedpi_hosts_whitelist", it) }
                    },
                )
            }
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_pf_setting),
                value = pf,
                onValueChange = {
                    pf = it
                    prefs.edit { putString("byedpi_pf", it) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_ipset_setting),
                value = ipset,
                onValueChange = {
                    ipset = it
                    prefs.edit { putString("byedpi_ipset", it) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_auto_category)) {
            EditTextPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_auto_setting),
                summary = stringResource(R.string.byedpi_auto_summary),
                value = autoVal,
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
                onValueChange = {
                    autoMode = it
                    prefs.edit { putString("byedpi_auto_mode", it) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_timeout_setting),
                summary = stringResource(R.string.byedpi_timeout_summary),
                value = timeout,
                onValueChange = {
                    timeout = it
                    prefs.edit { putString("byedpi_timeout", it) }
                },
            )
        }
    }
}
