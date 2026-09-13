package com.sleepanalysis.app.ui

import android.app.Activity
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sleepanalysis.app.DayAction
import com.sleepanalysis.app.DayAvailability
import com.sleepanalysis.app.DayVisual
import com.sleepanalysis.app.R
import com.sleepanalysis.app.SleepPrefs
import com.sleepanalysis.app.TodayUnlock
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private sealed interface AppScreen {
    data object Calendar : AppScreen
    data class Analyzing(val day: LocalDate) : AppScreen
    data class Playing(val day: LocalDate) : AppScreen
}

@Composable
fun SleepAnalysisApp() {
    val context = LocalContext.current
    val prefs = remember { SleepPrefs(context) }
    val today = remember { LocalDate.now() }
    val installDay = remember { prefs.ensureInstallDay(today) }

    var showSplash by remember { mutableStateOf(true) }
    var watchConnected by remember { mutableStateOf(prefs.watchConnected) }
    var screen by remember { mutableStateOf<AppScreen>(AppScreen.Calendar) }
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var showNoData by remember { mutableStateOf(false) }
    val todayUnlock = remember { TodayUnlock() }

    LaunchedEffect(Unit) {
        delay(2_000)
        showSplash = false
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (val current = screen) {
            AppScreen.Calendar -> CalendarScreen(
                month = month,
                today = today,
                onPrevMonth = { month = month.minusMonths(1) },
                onNextMonth = { month = month.plusMonths(1) },
                onDayClick = { day ->
                    if (!watchConnected) return@CalendarScreen
                    if (day == today && !todayUnlock.registerTodayTap()) {
                        return@CalendarScreen
                    }
                    when (DayAvailability.action(day, today, installDay, todayUnlock.unlocked)) {
                        DayAction.NO_DATA -> showNoData = true
                        DayAction.ANALYZE -> screen = AppScreen.Analyzing(day)
                    }
                },
            )
            is AppScreen.Analyzing -> AnalyzingPanel(
                onFinished = { screen = AppScreen.Playing(current.day) },
            )
            is AppScreen.Playing -> PlaybackPanel(
                onClose = { screen = AppScreen.Calendar },
            )
        }

        if (showSplash) {
            SplashScreen()
        } else if (!watchConnected) {
            ConnectWatchDialog(
                onConnect = {
                    prefs.watchConnected = true
                    watchConnected = true
                },
                onExit = { (context as Activity).finishAffinity() },
            )
        }

        if (showNoData) {
            AlertDialog(
                onDismissRequest = { showNoData = false },
                text = { Text(stringResource(R.string.no_data)) },
                confirmButton = {
                    TextButton(onClick = { showNoData = false }) {
                        Text(stringResource(R.string.ok))
                    }
                },
            )
        }
    }
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.splash_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun ConnectWatchDialog(
    onConnect: () -> Unit,
    onExit: () -> Unit,
) {
    var found by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(3_000)
        found = true
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.extraLarge)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.connect_prompt),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(20.dp))
            if (!found) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(R.string.found_watch),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onExit) {
                        Text(stringResource(R.string.exit_app))
                    }
                    Button(onClick = onConnect) {
                        Text(stringResource(R.string.connect))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarScreen(
    month: YearMonth,
    today: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    val title = remember(month) {
        month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    }
    val firstDay = month.atDay(1)
    val startOffset = firstDay.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val weekdays = remember {
        val sundayFirst = DayOfWeek.SUNDAY
        (0 until 7).map { sundayFirst.plus(it.toLong()).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevMonth) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = stringResource(R.string.previous_month))
            }
            Text(
                text = title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(R.string.next_month))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            weekdays.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        val cellCount = startOffset + daysInMonth
        val rows = (cellCount + 6) / 7
        repeat(rows) { row ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val index = row * 7 + col
                    val dayNumber = index - startOffset + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val visual = DayAvailability.visual(date, today)
                            DayCell(dayNumber, visual, onClick = { onDayClick(date) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(dayNumber: Int, visual: DayVisual, onClick: () -> Unit) {
    val today = visual == DayVisual.TODAY
    val textColor = when (visual) {
        DayVisual.PAST -> Color(0xFF7A738C)
        DayVisual.TODAY -> MaterialTheme.colorScheme.onPrimary
        DayVisual.FUTURE -> MaterialTheme.colorScheme.onBackground
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (today) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = dayNumber.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (today) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun AnalyzingPanel(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2_000)
        onFinished()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.analysing),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator()
    }
}

@Composable
private fun PlaybackPanel(onClose: () -> Unit) {
    val context = LocalContext.current
    BackHandler(enabled = true, onBack = onClose)
    DisposableEffect(Unit) {
        val player = MediaPlayer.create(context, R.raw.sleep_recording)
        player?.isLooping = true
        player?.start()
        onDispose {
            player?.stop()
            player?.release()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.playing_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
