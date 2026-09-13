package com.sleepanalysis.app

import android.content.Context
import java.time.LocalDate

class SleepPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("sleep_analysis", Context.MODE_PRIVATE)

    fun ensureInstallDay(today: LocalDate): LocalDate {
        val stored = prefs.getString(KEY_INSTALL_DAY, null)
        if (stored != null) return LocalDate.parse(stored)
        prefs.edit().putString(KEY_INSTALL_DAY, today.toString()).apply()
        return today
    }

    var watchConnected: Boolean
        get() = prefs.getBoolean(KEY_WATCH_CONNECTED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_WATCH_CONNECTED, value).apply()
        }

    private companion object {
        const val KEY_INSTALL_DAY = "install_day"
        const val KEY_WATCH_CONNECTED = "watch_connected"
    }
}
