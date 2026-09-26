package io.github.dovecoteescapee.byedpi.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.dovecoteescapee.byedpi.R
import io.github.dovecoteescapee.byedpi.data.AppSettings
import io.github.dovecoteescapee.byedpi.data.AppStatus
import io.github.dovecoteescapee.byedpi.data.FAILED_BROADCAST
import io.github.dovecoteescapee.byedpi.data.Mode
import io.github.dovecoteescapee.byedpi.data.SENDER
import io.github.dovecoteescapee.byedpi.data.STARTED_BROADCAST
import io.github.dovecoteescapee.byedpi.data.STOPPED_BROADCAST
import io.github.dovecoteescapee.byedpi.data.Sender
import io.github.dovecoteescapee.byedpi.services.ServiceManager
import io.github.dovecoteescapee.byedpi.services.appStatus
import io.github.dovecoteescapee.byedpi.ui.screens.MainScreen
import io.github.dovecoteescapee.byedpi.ui.theme.ByeDpiTheme
import io.github.dovecoteescapee.byedpi.utility.getSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName

        fun applyAppTheme(theme: String) {
            when (theme) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        private fun collectLogs(): String? =
            try {
                Runtime
                    .getRuntime()
                    .exec("logcat *:D -d")
                    .inputStream
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: Exception) {
                null
            }
    }

    private var statusText by mutableStateOf("")
    private var buttonText by mutableStateOf("")
    private var proxyAddress by mutableStateOf("")
    private var buttonEnabled by mutableStateOf(false)

    private val vpnRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                ServiceManager.start(this, Mode.VPN)
            } else {
                Toast
                    .makeText(
                        this,
                        R.string.vpn_permission_denied,
                        Toast.LENGTH_SHORT,
                    ).show()
                updateStatus()
            }
        }

    private val logsRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val logs = collectLogs()

                if (logs == null) {
                    Toast
                        .makeText(
                            this@MainActivity,
                            R.string.logs_failed,
                            Toast.LENGTH_SHORT,
                        ).show()
                } else {
                    val uri =
                        it.data?.data ?: run {
                            Log.e(TAG, "No data in result")
                            return@launch
                        }
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        try {
                            outputStream.write(logs.toByteArray())
                        } catch (e: IOException) {
                            Log.e(TAG, "Failed to save logs", e)
                        }
                    } ?: run {
                        Log.e(TAG, "Failed to open output stream")
                    }
                }
            }
        }

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                Log.d(TAG, "Received intent: ${intent?.action}")

                if (intent == null) {
                    Log.w(TAG, "Received null intent")
                    return
                }

                val senderOrd = intent.getIntExtra(SENDER, -1)
                val sender = Sender.entries.getOrNull(senderOrd)
                if (sender == null) {
                    Log.w(TAG, "Received intent with unknown sender: $senderOrd")
                    return
                }

                when (val action = intent.action) {
                    STARTED_BROADCAST,
                    STOPPED_BROADCAST,
                    -> updateStatus()

                    FAILED_BROADCAST -> {
                        Toast
                            .makeText(
                                context,
                                getString(R.string.failed_to_start, sender.name),
                                Toast.LENGTH_SHORT,
                            ).show()
                        updateStatus()
                    }

                    else -> Log.w(TAG, "Unknown action: $action")
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = getSettingsRepository()
        lifecycleScope.launch {
            repository.migrateFromSharedPreferencesIfNeeded()
        }

        setContent {
            val settings by repository.settingsFlow.collectAsState(initial = AppSettings())

            ByeDpiTheme(appTheme = settings.theme, amoledTheme = settings.amoledTheme) {
                var menuExpanded by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            actions = {
                                IconButton(
                                    onClick = {
                                        val (status, _) = appStatus
                                        if (status == AppStatus.Halted) {
                                            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast
                                                .makeText(
                                                    this@MainActivity,
                                                    R.string.settings_unavailable,
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = stringResource(R.string.title_settings),
                                    )
                                }

                                Box {
                                    IconButton(onClick = { menuExpanded = true }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "More options",
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false },
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.save_logs)) },
                                            onClick = {
                                                menuExpanded = false
                                                val intent =
                                                    Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                                        addCategory(Intent.CATEGORY_OPENABLE)
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_TITLE, "byedpi.log")
                                                    }
                                                logsRegister.launch(intent)
                                            },
                                        )
                                    }
                                }
                            },
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                    },
                ) { innerPadding ->
                    MainScreen(
                        buttonText = buttonText,
                        statusText = statusText,
                        proxyAddress = proxyAddress,
                        buttonEnabled = buttonEnabled,
                        onButtonClick = {
                            val (status, _) = appStatus
                            when (status) {
                                AppStatus.Halted -> start()
                                AppStatus.Running -> stop()
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }

        val intentFilter =
            IntentFilter().apply {
                addAction(STARTED_BROADCAST)
                addAction(STOPPED_BROADCAST)
                addAction(FAILED_BROADCAST)
            }

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, intentFilter)
        }

        lifecycleScope.launch {
            val settings = repository.getSettings()
            applyAppTheme(settings.theme)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun start() {
        lifecycleScope.launch {
            val settings = getSettingsRepository().getSettings()
            val mode = Mode.fromString(settings.mode)
            when (mode) {
                Mode.VPN -> {
                    val intentPrepare = VpnService.prepare(this@MainActivity)
                    if (intentPrepare != null) {
                        vpnRegister.launch(intentPrepare)
                    } else {
                        ServiceManager.start(this@MainActivity, Mode.VPN)
                    }
                }

                Mode.Proxy -> ServiceManager.start(this@MainActivity, Mode.Proxy)
            }
        }
    }

    private fun stop() {
        ServiceManager.stop(this)
    }

    private fun updateStatus() {
        val (status, currentMode) = appStatus

        Log.i(TAG, "Updating status: $status, $currentMode")

        lifecycleScope.launch {
            val settings = getSettingsRepository().getSettings()
            val proxyIp = settings.engine.proxyIp
            val proxyPort = settings.engine.proxyPort
            proxyAddress = getString(R.string.proxy_address, proxyIp, proxyPort)

            when (status) {
                AppStatus.Halted -> {
                    when (Mode.fromString(settings.mode)) {
                        Mode.VPN -> {
                            statusText = getString(R.string.vpn_disconnected)
                            buttonText = getString(R.string.vpn_connect)
                        }

                        Mode.Proxy -> {
                            statusText = getString(R.string.proxy_down)
                            buttonText = getString(R.string.proxy_start)
                        }
                    }
                    buttonEnabled = true
                }

                AppStatus.Running -> {
                    when (currentMode) {
                        Mode.VPN -> {
                            statusText = getString(R.string.vpn_connected)
                            buttonText = getString(R.string.vpn_disconnect)
                        }

                        Mode.Proxy -> {
                            statusText = getString(R.string.proxy_up)
                            buttonText = getString(R.string.proxy_stop)
                        }
                    }
                    buttonEnabled = true
                }
            }
        }
    }
}
