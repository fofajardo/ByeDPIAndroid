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
fun AutoSettingsScreen(
    prefs: SharedPreferences,
    modifier: Modifier = Modifier,
) {
    var autoVal by remember(prefs) { mutableStateOf(prefs.getString("byedpi_auto", "") ?: "") }
    var autoMode by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_auto_mode", "none") ?: "none")
    }
    var timeout by remember(prefs) { mutableStateOf(prefs.getString("byedpi_timeout", "") ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
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
