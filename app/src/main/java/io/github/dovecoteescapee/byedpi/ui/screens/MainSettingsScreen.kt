package io.github.dovecoteescapee.byedpi.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
import io.github.dovecoteescapee.byedpi.ui.components.PreferenceCategoryHeader
import io.github.dovecoteescapee.byedpi.ui.components.PreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.SwitchPreferenceItem
import io.github.dovecoteescapee.byedpi.utility.checkNotLocalIp

@Composable
fun MainSettingsScreen(
    prefs: SharedPreferences,
    onNavigateToUiSettings: () -> Unit,
    onNavigateToCmdSettings: () -> Unit,
    onNavigateToVpnAppsFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var appTheme by remember(prefs) {
        mutableStateOf(prefs.getString("app_theme", "system") ?: "system")
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

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            PreferenceCategoryHeader(title = stringResource(R.string.general_category))
        }
        item {
            ListPreferenceItem(
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
        }
        item {
            ListPreferenceItem(
                title = stringResource(R.string.mode_setting),
                selectedValue = byedpiMode,
                entries = stringArrayResource(R.array.byedpi_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_modes_entries).toList(),
                onValueChange = { newValue ->
                    byedpiMode = newValue
                    prefs.edit { putString("byedpi_mode", newValue) }
                },
            )
        }
        item {
            SwitchPreferenceItem(
                title = stringResource(R.string.autostart_setting),
                checked = autostart,
                onCheckedChange = { checked ->
                    autostart = checked
                    prefs.edit { putBoolean("autostart", checked) }
                },
            )
        }
        if (isVpnMode) {
            item {
                EditTextPreferenceItem(
                    title = stringResource(R.string.dbs_ip_setting),
                    value = dnsIp,
                    onValueChange = { newValue ->
                        dnsIp = newValue
                        prefs.edit { putString("dns_ip", newValue) }
                    },
                    validate = { it.isBlank() || checkNotLocalIp(it) },
                )
            }
            item {
                SwitchPreferenceItem(
                    title = stringResource(R.string.ipv6_setting),
                    checked = ipv6Enable,
                    onCheckedChange = { checked ->
                        ipv6Enable = checked
                        prefs.edit { putBoolean("ipv6_enable", checked) }
                    },
                )
            }
        }

        item {
            PreferenceCategoryHeader(title = stringResource(R.string.byedpi_category))
        }
        item {
            SwitchPreferenceItem(
                title = stringResource(R.string.use_command_line_settings),
                checked = cmdSettingsEnabled,
                onCheckedChange = { checked ->
                    cmdSettingsEnabled = checked
                    prefs.edit { putBoolean("byedpi_enable_cmd_settings", checked) }
                },
            )
        }
        item {
            PreferenceItem(
                title = stringResource(R.string.ui_editor),
                enabled = !cmdSettingsEnabled,
                onClick = onNavigateToUiSettings,
            )
        }
        item {
            PreferenceItem(
                title = stringResource(R.string.command_line_editor),
                enabled = cmdSettingsEnabled,
                onClick = onNavigateToCmdSettings,
            )
        }

        if (isVpnMode) {
            item {
                PreferenceCategoryHeader(title = stringResource(R.string.vpn_category))
            }
            item {
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
            }
            item {
                PreferenceItem(
                    title = stringResource(R.string.vpn_filtered_apps),
                    onClick = onNavigateToVpnAppsFilter,
                )
            }
        }

        item {
            PreferenceCategoryHeader(title = stringResource(R.string.about_category))
        }
        item {
            PreferenceItem(
                title = stringResource(R.string.version),
                summary = BuildConfig.VERSION_NAME,
            )
        }
        item {
            PreferenceItem(
                title = stringResource(R.string.source_code_link),
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
