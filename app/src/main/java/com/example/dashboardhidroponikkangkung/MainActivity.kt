package com.example.dashboardhidroponikkangkung

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dashboardhidroponikkangkung.ui.screen.HidroponikScreen
import com.example.dashboardhidroponikkangkung.ui.theme.HidroponikTheme
import com.example.dashboardhidroponikkangkung.ui.viewmodel.HidroponikViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HidroponikTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: HidroponikViewModel = viewModel()
                    HidroponikScreen(viewModel = viewModel)
                }
            }
        }
    }
}