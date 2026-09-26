@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.screens.engine

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.ui.components.EditTextPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.Section
import io.github.dovecoteescapee.byedpi.ui.components.SwitchPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop
import io.github.dovecoteescapee.byedpi.utility.checkIp

@Composable
fun ProxyConnectionSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var proxyIp by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_proxy_ip", "127.0.0.1") ?: "127.0.0.1")
    }
    var proxyPort by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_proxy_port", "1080") ?: "1080")
    }
    var maxConnections by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_max_connections", "512") ?: "512")
    }
    var bufferSize by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_buffer_size", "16384") ?: "16384")
    }
    var connIp by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_conn_ip", "") ?: "")
    }
    var noIpv6 by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_no_ipv6", false))
    }

    var tcpFastOpen by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_tcp_fast_open", false))
    }
    var noDomain by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_no_domain", false))
    }
    var waitSend by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_wait_send", false))
    }
    var awaitInt by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_await_int", "") ?: "")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
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
    }
}
