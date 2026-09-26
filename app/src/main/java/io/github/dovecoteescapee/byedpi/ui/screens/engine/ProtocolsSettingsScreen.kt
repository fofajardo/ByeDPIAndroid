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

@Composable
fun ProtocolsSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var desyncHttp by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_desync_http", true))
    }
    var desyncHttps by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_desync_https", true))
    }
    var desyncUdp by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_desync_udp", false))
    }

    var hostMixedCase by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_host_mixed_case", false))
    }
    var domainMixedCase by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_domain_mixed_case", false))
    }
    var hostRemoveSpaces by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_host_remove_spaces", false))
    }
    var tlsrecEnabled by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_tlsrec_enabled", false))
    }
    var tlsrecPosition by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_tlsrec_position", "0") ?: "0")
    }
    var tlsrecAtSni by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_tlsrec_at_sni", false))
    }
    var udpFakeCount by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_udp_fake_count", "0") ?: "0")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
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
    }
}
