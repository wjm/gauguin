package org.piepmeyer.gauguin.ui

import android.app.Activity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class ActivityUtils : KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()

    @Suppress("DEPRECATION")
    fun configureFullscreen(activity: Activity) {
        if (!applicationPreferences.showFullscreen()) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun configureKeepScreenOn(activity: Activity) {
        if (applicationPreferences.keepScreenOn()) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun configureNightMode(activity: Activity) {
        when (applicationPreferences.theme) {
            Theme.LIGHT -> {
                activity.setTheme(R.style.AppTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Theme.DARK -> {
                activity.setTheme(R.style.AppTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Theme.MONOCHROME -> {
                activity.setTheme(R.style.AppThemeMonochrome)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Theme.SYSTEM_DEFAULT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else -> {
                // Means usage of dynamic colors, in this case we do not have to set any mode by hand
            }
        }
    }
}
