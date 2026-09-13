package com.sleepanalysis.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sleepanalysis.app.ui.theme.SleepAnalysisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepAnalysisTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SleepAnalysisApp()
                }
            }
        }
    }
}
