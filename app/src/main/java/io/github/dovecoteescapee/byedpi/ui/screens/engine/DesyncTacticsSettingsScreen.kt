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

@Composable
fun DesyncTacticsSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var defaultTtl by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_default_ttl", "0") ?: "0")
    }
    var desyncMethod by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_desync_method", "none") ?: "none")
    }
    var splitPosition by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_split_position", "0") ?: "0")
    }
    var splitAtHost by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_split_at_host", false))
    }
    var dropSack by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_drop_sack", false)) }
    var fakeTtl by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_fake_ttl", "0") ?: "0")
    }
    var fakeOffset by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_fake_offset", "0") ?: "0")
    }
    var fakeSni by remember(prefs) { mutableStateOf(prefs.getString("byedpi_fake_sni", "") ?: "") }
    var md5sig by remember(prefs) { mutableStateOf(prefs.getBoolean("byedpi_md5sig", false)) }
    var fakeData by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_fake_data", "") ?: "")
    }
    var fakeTlsMod by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_fake_tls_mod", "") ?: "")
    }
    var tlsminor by remember(prefs) { mutableStateOf(prefs.getString("byedpi_tlsminor", "") ?: "") }
    var round by remember(prefs) { mutableStateOf(prefs.getString("byedpi_round", "") ?: "") }
    var oobData by remember(prefs) { mutableStateOf(prefs.getString("byedpi_oob_data", "") ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
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
    }
}
