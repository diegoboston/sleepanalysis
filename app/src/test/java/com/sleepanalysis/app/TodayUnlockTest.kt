package com.sleepanalysis.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TodayUnlockTest {
    @Test
    fun lockedUntilSeventhTap() {
        val unlock = TodayUnlock()
        repeat(6) {
            assertFalse(unlock.registerTodayTap())
            assertFalse(unlock.unlocked)
        }
        assertEquals(6, unlock.taps)
        assertTrue(unlock.registerTodayTap())
        assertTrue(unlock.unlocked)
        assertEquals(7, unlock.taps)
    }

    @Test
    fun staysUnlockedForLaterTaps() {
        val unlock = TodayUnlock()
        repeat(7) { unlock.registerTodayTap() }
        assertTrue(unlock.registerTodayTap())
        assertTrue(unlock.unlocked)
        assertEquals(7, unlock.taps)
    }

    @Test
    fun newInstanceStartsLocked() {
        val first = TodayUnlock()
        repeat(7) { first.registerTodayTap() }
        assertTrue(first.unlocked)

        val restarted = TodayUnlock()
        assertFalse(restarted.unlocked)
        assertEquals(0, restarted.taps)
        assertFalse(restarted.registerTodayTap())
    }
}
