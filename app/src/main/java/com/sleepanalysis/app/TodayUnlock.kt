package com.sleepanalysis.app

/** In-memory easter egg: 7 taps on today unlock playback. Resets with process death. */
class TodayUnlock(private val tapsNeeded: Int = 7) {
    var unlocked: Boolean = false
        private set
    var taps: Int = 0
        private set

    fun registerTodayTap(): Boolean {
        if (unlocked) return true
        taps += 1
        if (taps >= tapsNeeded) {
            unlocked = true
            return true
        }
        return false
    }
}
