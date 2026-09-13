package com.sleepanalysis.app

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DayAvailabilityTest {
    private val install = LocalDate.of(2026, 9, 13)
    private val today = LocalDate.of(2026, 9, 16)

    @Test
    fun todayIsHighlighted() {
        assertEquals(DayVisual.TODAY, DayAvailability.visual(today, today))
    }

    @Test
    fun previousDaysArePast() {
        assertEquals(DayVisual.PAST, DayAvailability.visual(today.minusDays(1), today))
    }

    @Test
    fun futureDaysAreFuture() {
        assertEquals(DayVisual.FUTURE, DayAvailability.visual(today.plusDays(1), today))
    }

    @Test
    fun futureDaysHaveNoData() {
        assertEquals(DayAction.NO_DATA, DayAvailability.action(today.plusDays(1), today, install))
    }

    @Test
    fun daysAfterInstallUntilTodayAnalyze() {
        assertEquals(DayAction.ANALYZE, DayAvailability.action(install.plusDays(1), today, install))
        assertEquals(DayAction.ANALYZE, DayAvailability.action(today, today, install))
    }

    @Test
    fun installDayHasNoData() {
        assertEquals(DayAction.NO_DATA, DayAvailability.action(install, today, install))
    }

    @Test
    fun daysBeforeInstallHaveNoData() {
        assertEquals(DayAction.NO_DATA, DayAvailability.action(install.minusDays(1), today, install))
    }
}
