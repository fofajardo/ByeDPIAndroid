@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.dovecoteescapee.byedpi.ui.screens

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.ui.components.ListPreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.PreferenceItem
import io.github.dovecoteescapee.byedpi.ui.components.Section
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeBottom
import io.github.dovecoteescapee.byedpi.ui.components.segmentedShapeTop
import io.github.dovecoteescapee.byedpi.ui.fragments.CmdSettingsScreen
import io.github.dovecoteescapee.byedpi.ui.fragments.VisualEngineSettingsScreen
import io.github.dovecoteescapee.byedpi.utility.ByeDpiArgsConverter

@Composable
fun EngineSettingsScreen(
    prefs: SharedPreferences,
    onNavigateToProxy: () -> Unit,
    onNavigateToDesync: () -> Unit,
    onNavigateToProtocols: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToAuto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var editorMode by remember(prefs) {
        val useCmd = prefs.getBoolean("byedpi_enable_cmd_settings", false)
        mutableStateOf(
            if (useCmd) {
                "cmd"
            } else {
                "ui"
            }
        )
    }

    var cmdArgs by remember(prefs) {
        mutableStateOf(prefs.getString("byedpi_cmd_args", "") ?: "")
    }

    val engineHeaderContent: @Composable ColumnScope.() -> Unit = {
        Section(
            title = stringResource(R.string.general_category),
        ) {
            ListPreferenceItem(
                title = stringResource(R.string.editor_mode),
                shapes = segmentedShapeTop(),
                selectedValue = editorMode,
                entries = stringArrayResource(R.array.byedpi_editor_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_editor_modes_entries).toList(),
                onValueChange = { newMode ->
                    editorMode = newMode
                    val isCmd = newMode == "cmd"
                    prefs.edit { putBoolean("byedpi_enable_cmd_settings", isCmd) }
                },
            )

            if (editorMode == "cmd") {
                PreferenceItem(
                    title = stringResource(R.string.sync_from_visual),
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    shapes = segmentedShapeBottom(),
                    onClick = {
                        val generated = ByeDpiArgsConverter.uiPreferencesToCmdArgs(prefs)
                        cmdArgs = generated
                        prefs.edit { putString("byedpi_cmd_args", generated) }
                        Toast.makeText(context, R.string.sync_applied, Toast.LENGTH_SHORT).show()
                    },
                )
            } else {
                PreferenceItem(
                    title = stringResource(R.string.sync_from_cli),
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    shapes = segmentedShapeBottom(),
                    onClick = {
                        val currentCmd = prefs.getString("byedpi_cmd_args", "") ?: ""
                        ByeDpiArgsConverter.applyCmdArgsToUiPreferences(currentCmd, prefs)
                        Toast.makeText(context, R.string.sync_applied, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }

    if (editorMode == "cmd") {
        CmdSettingsScreen(
            cmdArgs = cmdArgs,
            onCmdArgsChange = { newArgs ->
                cmdArgs = newArgs
                prefs.edit { putString("byedpi_cmd_args", newArgs) }
            },
            headerContent = engineHeaderContent,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        VisualEngineSettingsScreen(
            onNavigateToProxy = onNavigateToProxy,
            onNavigateToDesync = onNavigateToDesync,
            onNavigateToProtocols = onNavigateToProtocols,
            onNavigateToFilters = onNavigateToFilters,
            onNavigateToAuto = onNavigateToAuto,
            headerContent = engineHeaderContent,
            modifier = modifier.fillMaxSize(),
        )
    }
}
