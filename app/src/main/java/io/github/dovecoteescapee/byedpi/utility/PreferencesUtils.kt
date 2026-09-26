package io.github.dovecoteescapee.byedpi.utility

import android.content.Context
import io.github.dovecoteescapee.byedpi.data.Mode
import io.github.dovecoteescapee.byedpi.data.SettingsRepository

fun Context.getSettingsRepository(): SettingsRepository = SettingsRepository(this.applicationContext)

fun Context.currentMode(): Mode {
    val settings = getSettingsRepository().getSettingsBlocking()
    return Mode.fromString(settings.mode)
}
