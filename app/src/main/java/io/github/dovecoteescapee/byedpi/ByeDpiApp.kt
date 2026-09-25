package io.github.dovecoteescapee.byedpi

import android.app.Application
import com.google.android.material.color.DynamicColors

class ByeDpiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
