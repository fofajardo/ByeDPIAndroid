@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import io.github.dovecoteescapee.byedpi.BuildConfig
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.activities.MainActivity
import io.github.dovecoteescapee.byedpi.ui.components.EditTextPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.ListPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.PreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.Section
import io.github.dovecoteescapee.byedpi.ui.components.SwitchPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop
import io.github.dovecoteescapee.byedpi.utility.checkNotLocalIp

@Composable
fun MainSettingsScreen(
    prefs: SharedPreferences,
    onNavigateToEngineSettings: () -> Unit,
    onNavigateToVpnAppsFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var appTheme by remember(prefs) {
        mutableStateOf(prefs.getString("app_theme", "system") ?: "system")
    }
    var amoledTheme by remember(prefs) {
        mutableStateOf(prefs.getBoolean("amoled_theme", false))
    }
    var byedpiMode by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_mode", "vpn") ?: "vpn")
    }
    var autostart by remember(prefs) {
        mutableStateOf(prefs.getBoolean("autostart", false))
    }
    var dnsIp by remember(prefs) {
        mutableStateOf(prefs.getString("dns_ip", "1.1.1.1") ?: "1.1.1.1")
    }
    var ipv6Enable by remember(prefs) {
        mutableStateOf(prefs.getBoolean("ipv6_enable", false))
    }
    var cmdSettingsEnabled by remember(prefs) {
        mutableStateOf(prefs.getBoolean("byedpi_enable_cmd_settings", false))
    }
    var vpnFilterMode by remember(prefs) {
        mutableStateOf(prefs.getString("vpn_filter_mode", "blacklist") ?: "blacklist")
    }

    val isVpnMode = byedpiMode == "vpn"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section(title = stringResource(R.string.appearance_category)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.theme_settings),
                selectedValue = appTheme,
                entries = stringArrayResource(R.array.themes).toList(),
                entryValues = stringArrayResource(R.array.themes_entries).toList(),
                onValueChange = { newValue ->
                    appTheme = newValue
                    prefs.edit { putString("app_theme", newValue) }
                    MainActivity.applyAppTheme(newValue)
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.amoled_theme_setting),
                checked = amoledTheme,
                shapes = segmentedShapeBottom(),
                onCheckedChange = { checked ->
                    amoledTheme = checked
                    prefs.edit { putBoolean("amoled_theme", checked) }
                },
            )
        }

        Section(title = stringResource(R.string.general_category)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.mode_setting),
                selectedValue = byedpiMode,
                entries = stringArrayResource(R.array.byedpi_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_modes_entries).toList(),
                onValueChange = { newValue ->
                    byedpiMode = newValue
                    prefs.edit { putString("byedpi_mode", newValue) }
                },
            )
            PreferenceItem(
                title = stringResource(R.string.engine_settings),
                summary = if (cmdSettingsEnabled) {
                    stringResource(R.string.tab_cli)
                } else {
                    stringResource(R.string.tab_visual)
                },
                onClick = onNavigateToEngineSettings,
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.autostart_setting),
                checked = autostart,
                shapes = segmentedShapeBottom(),
                onCheckedChange = { checked ->
                    autostart = checked
                    prefs.edit { putBoolean("autostart", checked) }
                },
            )
        }

        if (isVpnMode) {
            Section(title = stringResource(R.string.vpn_category)) {
                EditTextPreferenceItem(
                    shapes = segmentedShapeTop(),
                    title = stringResource(R.string.dbs_ip_setting),
                    value = dnsIp,
                    onValueChange = { newValue ->
                        dnsIp = newValue
                        prefs.edit { putString("dns_ip", newValue) }
                    },
                    validate = { it.isBlank() || checkNotLocalIp(it) },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.ipv6_setting),
                    summary = stringResource(R.string.ipv6_setting_summary),
                    checked = ipv6Enable,
                    onCheckedChange = { checked ->
                        ipv6Enable = checked
                        prefs.edit { putBoolean("ipv6_enable", checked) }
                    },
                )
                ListPreferenceItem(
                    title = stringResource(R.string.vpn_filter_mode),
                    selectedValue = vpnFilterMode,
                    entries = stringArrayResource(R.array.vpn_filtering_modes).toList(),
                    entryValues = stringArrayResource(R.array.vpn_filtering_modes_entries).toList(),
                    onValueChange = { newValue ->
                        vpnFilterMode = newValue
                        prefs.edit { putString("vpn_filter_mode", newValue) }
                    },
                )
                PreferenceItem(
                    title = stringResource(R.string.vpn_filtered_apps),
                    shapes = segmentedShapeBottom(),
                    onClick = onNavigateToVpnAppsFilter,
                )
            }
        }

        Section(title = stringResource(R.string.about_category)) {
            PreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.version),
                summary = BuildConfig.VERSION_NAME,
            )
            PreferenceItem(
                title = stringResource(R.string.source_code_link),
                shapes = segmentedShapeBottom(),
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/dovecoteescapee/ByeDPIAndroid"),
                    )
                    context.startActivity(intent)
                },
            )
        }
    }
}
