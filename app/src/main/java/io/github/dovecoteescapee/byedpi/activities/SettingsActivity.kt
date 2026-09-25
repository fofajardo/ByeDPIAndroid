package io.github.dovecoteescapee.byedpi.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.ui.screens.CmdSettingsScreen
import io.github.dovecoteescapee.byedpi.ui.screens.MainSettingsScreen
import io.github.dovecoteescapee.byedpi.ui.screens.UiSettingsScreen
import io.github.dovecoteescapee.byedpi.ui.screens.VpnAppsFilterScreen
import io.github.dovecoteescapee.byedpi.ui.theme.ByeDpiTheme
import io.github.dovecoteescapee.byedpi.utility.getPreferences

enum class SettingsDestination {
    MAIN,
    UI_SETTINGS,
    CMD_SETTINGS,
    VPN_APPS_FILTER,
}

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs = getPreferences()
            var appTheme by remember {
                mutableStateOf(prefs.getString("app_theme", "system") ?: "system")
            }
            var amoledTheme by remember {
                mutableStateOf(prefs.getBoolean("amoled_theme", false))
            }

            DisposableEffect(prefs) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == "app_theme") {
                        appTheme = prefs.getString("app_theme", "system") ?: "system"
                    }
                    if (key == "amoled_theme") {
                        amoledTheme = prefs.getBoolean("amoled_theme", false)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }

            ByeDpiTheme(appTheme = appTheme, amoledTheme = amoledTheme) {
                SettingsApp(
                    prefs = prefs,
                    onFinish = { finish() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsApp(
    prefs: SharedPreferences,
    onFinish: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navBackStack = remember { mutableStateListOf(SettingsDestination.MAIN) }
    val currentDestination = navBackStack.last()

    // Preferences state tracker to trigger recomposition on preference changes or reset
    var prefsEpoch by remember { mutableStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }

    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefsEpoch++
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val navigateTo: (SettingsDestination) -> Unit = { dest ->
        navBackStack.add(dest)
    }

    val navigateUp: () -> Unit = {
        if (navBackStack.size > 1) {
            navBackStack.removeAt(navBackStack.lastIndex)
        } else {
            onFinish()
        }
    }

    BackHandler(enabled = true) {
        navigateUp()
    }

    val title = when (currentDestination) {
        SettingsDestination.MAIN -> stringResource(R.string.title_settings)
        SettingsDestination.UI_SETTINGS -> stringResource(R.string.ui_editor)
        SettingsDestination.CMD_SETTINGS -> stringResource(R.string.command_line_editor)
        SettingsDestination.VPN_APPS_FILTER -> stringResource(R.string.vpn_filtered_apps)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (currentDestination == SettingsDestination.MAIN) {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.reset_settings),
                            )
                        }
                    } else if (currentDestination == SettingsDestination.CMD_SETTINGS || currentDestination == SettingsDestination.UI_SETTINGS) {
                        val docsUrl = stringResource(R.string.byedpi_docs)
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(docsUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.documentation),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        if (showResetDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(R.string.reset_settings)) },
                text = { Text("Reset all settings to default?") },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            prefs.edit().clear().apply()
                            prefsEpoch++
                            showResetDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.reset_settings))
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                if (navBackStack.size > 1) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "settings_nav",
            modifier = Modifier.padding(innerPadding),
        ) { destination ->
            val epoch = prefsEpoch

            androidx.compose.runtime.key(epoch) {
                when (destination) {
                    SettingsDestination.MAIN -> {
                        MainSettingsScreen(
                            prefs = prefs,
                            onNavigateToUiSettings = { navigateTo(SettingsDestination.UI_SETTINGS) },
                            onNavigateToCmdSettings = { navigateTo(SettingsDestination.CMD_SETTINGS) },
                            onNavigateToVpnAppsFilter = { navigateTo(SettingsDestination.VPN_APPS_FILTER) },
                        )
                    }

                    SettingsDestination.UI_SETTINGS -> {
                        UiSettingsScreen(
                            prefs = prefs,
                        )
                    }

                    SettingsDestination.CMD_SETTINGS -> {
                        val cmdArgs = prefs.getString("byedpi_cmd_args", "") ?: ""
                        CmdSettingsScreen(
                            cmdArgs = cmdArgs,
                            onCmdArgsChange = { newArgs ->
                                prefs.edit { putString("byedpi_cmd_args", newArgs) }
                            },
                        )
                    }

                SettingsDestination.VPN_APPS_FILTER -> {
                    val checkedApps = prefs.getStringSet("vpn_filtered_apps", emptySet()) ?: emptySet()
                    VpnAppsFilterScreen(
                        checkedPackages = checkedApps,
                        onTogglePackage = { pkg, checked ->
                            val updated = checkedApps.toMutableSet().apply {
                                if (checked) {
                                    add(pkg)
                                } else {
                                    remove(pkg)
                                }
                            }
                            prefs.edit { putStringSet("vpn_filtered_apps", updated) }
                        },
                    )
                }
            }
        }
    }
}
}