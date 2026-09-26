@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.screens.engine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.data.AppSettings
import io.github.dovecoteescapee.byedpi.ui.components.EditTextPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.ListPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.Section
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop

@Composable
fun AutoSettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val engine = settings.engine

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
                value = engine.auto,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(auto = newValue)) }
                },
            )
            ListPreferenceItem(
                title = stringResource(R.string.byedpi_auto_mode_setting),
                selectedValue = engine.autoMode,
                entries = stringArrayResource(R.array.byedpi_auto_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_auto_modes_entries).toList(),
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(autoMode = newValue)) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_timeout_setting),
                summary = stringResource(R.string.byedpi_timeout_summary),
                value = engine.timeout,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(timeout = newValue)) }
                },
            )
        }
    }
}
