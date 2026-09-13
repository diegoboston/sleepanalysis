package com.sleepanalysis.app

import java.time.LocalDate

enum class DayVisual { PAST, TODAY, FUTURE }

enum class DayAction { NO_DATA, ANALYZE }

object DayAvailability {
    fun visual(day: LocalDate, today: LocalDate): DayVisual = when {
        day.isBefore(today) -> DayVisual.PAST
        day == today -> DayVisual.TODAY
        else -> DayVisual.FUTURE
    }

    fun action(day: LocalDate, today: LocalDate, installDay: LocalDate): DayAction {
        if (day.isAfter(today)) return DayAction.NO_DATA
        if (day.isAfter(installDay) && !day.isAfter(today)) return DayAction.ANALYZE
        return DayAction.NO_DATA
    }
}
