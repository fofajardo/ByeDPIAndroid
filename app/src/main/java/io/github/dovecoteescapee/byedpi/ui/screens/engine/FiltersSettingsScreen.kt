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
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop

@Composable
fun FiltersSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var hostsMode by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_hosts_mode", "none") ?: "none")
    }
    var hostsBlacklist by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_hosts_blacklist", "") ?: "")
    }
    var hostsWhitelist by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_hosts_whitelist", "") ?: "")
    }
    var pf by remember(prefs) { mutableStateOf(prefs.getString("byedpi_pf", "") ?: "") }
    var ipset by remember(prefs) { mutableStateOf(prefs.getString("byedpi_ipset", "") ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
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
    }
}
