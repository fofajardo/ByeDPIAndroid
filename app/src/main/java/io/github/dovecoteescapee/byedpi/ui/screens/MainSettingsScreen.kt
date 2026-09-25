@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

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
import io.github.dovecoteescapee.byedpi.ui.components.PreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.SettingsGroup
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
    val generalCount = if (isVpnMode) {
        6
    } else {
        4
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            SettingsGroup(title = stringResource(R.string.general_category)) {
                ListPreferenceItem(
                    title = stringResource(R.string.theme_settings),
                    selectedValue = appTheme,
                    entries = stringArrayResource(R.array.themes).toList(),
                    entryValues = stringArrayResource(R.array.themes_entries).toList(),
                    index = 0,
                    count = generalCount,
                    onValueChange = { newValue ->
                        appTheme = newValue
                        prefs.edit { putString("app_theme", newValue) }
                        MainActivity.applyAppTheme(newValue)
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.amoled_theme_setting),
                    checked = amoledTheme,
                    index = 1,
                    count = generalCount,
                    onCheckedChange = { checked ->
                        amoledTheme = checked
                        prefs.edit { putBoolean("amoled_theme", checked) }
                    },
                )
                ListPreferenceItem(
                    title = stringResource(R.string.mode_setting),
                    selectedValue = byedpiMode,
                    entries = stringArrayResource(R.array.byedpi_modes).toList(),
                    entryValues = stringArrayResource(R.array.byedpi_modes_entries).toList(),
                    index = 2,
                    count = generalCount,
                    onValueChange = { newValue ->
                        byedpiMode = newValue
                        prefs.edit { putString("byedpi_mode", newValue) }
                    },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.autostart_setting),
                    checked = autostart,
                    index = 3,
                    count = generalCount,
                    onCheckedChange = { checked ->
                        autostart = checked
                        prefs.edit { putBoolean("autostart", checked) }
                    },
                )
                if (isVpnMode) {
                    EditTextPreferenceItem(
                        title = stringResource(R.string.dbs_ip_setting),
                        value = dnsIp,
                        index = 4,
                        count = generalCount,
                        onValueChange = { newValue ->
                            dnsIp = newValue
                            prefs.edit { putString("dns_ip", newValue) }
                        },
                        validate = { it.isBlank() || checkNotLocalIp(it) },
                    )
                    SwitchPreferenceItem(
                        title = stringResource(R.string.ipv6_setting),
                        checked = ipv6Enable,
                        index = 5,
                        count = generalCount,
                        onCheckedChange = { checked ->
                            ipv6Enable = checked
                            prefs.edit { putBoolean("ipv6_enable", checked) }
                        },
                    )
                }
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.byedpi_category)) {
                SwitchPreferenceItem(
                    title = stringResource(R.string.use_command_line_settings),
                    checked = cmdSettingsEnabled,
                    index = 0,
                    count = 3,
                    onCheckedChange = { checked ->
                        cmdSettingsEnabled = checked
                        prefs.edit { putBoolean("byedpi_enable_cmd_settings", checked) }
                    },
                )
                PreferenceItem(
                    title = stringResource(R.string.ui_editor),
                    enabled = !cmdSettingsEnabled,
                    index = 1,
                    count = 3,
                    onClick = onNavigateToUiSettings,
                )
                PreferenceItem(
                    title = stringResource(R.string.command_line_editor),
                    enabled = cmdSettingsEnabled,
                    index = 2,
                    count = 3,
                    onClick = onNavigateToCmdSettings,
                )
            }
        }

        if (isVpnMode) {
            item {
                SettingsGroup(title = stringResource(R.string.vpn_category)) {
                    ListPreferenceItem(
                        title = stringResource(R.string.vpn_filter_mode),
                        selectedValue = vpnFilterMode,
                        entries = stringArrayResource(R.array.vpn_filtering_modes).toList(),
                        entryValues = stringArrayResource(R.array.vpn_filtering_modes_entries).toList(),
                        index = 0,
                        count = 2,
                        onValueChange = { newValue ->
                            vpnFilterMode = newValue
                            prefs.edit { putString("vpn_filter_mode", newValue) }
                        },
                    )
                    PreferenceItem(
                        title = stringResource(R.string.vpn_filtered_apps),
                        index = 1,
                        count = 2,
                        onClick = onNavigateToVpnAppsFilter,
                    )
                }
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.about_category)) {
                PreferenceItem(
                    title = stringResource(R.string.version),
                    summary = BuildConfig.VERSION_NAME,
                    index = 0,
                    count = 2,
                )
                PreferenceItem(
                    title = stringResource(R.string.source_code_link),
                    index = 1,
                    count = 2,
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
}
