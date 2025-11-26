package com.example.dashboardhidroponikkangkung.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dashboardhidroponikkangkung.ui.components.*
import com.example.dashboardhidroponikkangkung.ui.viewmodel.HidroponikViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HidroponikScreen(
    viewModel: HidroponikViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val tdsRange by viewModel.tdsRange.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hidroponik Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kangkung Pupuk AB Mix",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Memuat data dari ESP32...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TDS Monitor
                item {
                    TdsMonitorCard(
                        sensorData = uiState.sensors,
                        minTds = tdsRange.min,
                        maxTds = tdsRange.max,
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }

                // Status Pompa
                item {
                    Text(
                        text = "Status Pompa Pupuk",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    PumpStatusCard(
                        title = "Pompa Pupuk A",
                        pumpStatus = uiState.pumpA
                    )
                }

                item {
                    PumpStatusCard(
                        title = "Pompa Pupuk B",
                        pumpStatus = uiState.pumpB
                    )
                }

                // Grafik TDS
                item {
                    TdsChart(historyData = uiState.history)
                }

                // KONTROL MANUAL (INI YANG BARU & BENAR)
                item {
                    ManualControlCard(
                        isAutomatic = uiState.isAutomatic,
                        onToggleMode = { viewModel.toggleMode() },
                        onActivatePumps = { viewModel.activatePumps() },     // Nyalain A & B
                        onDeactivatePumps = { viewModel.deactivatePumps() }  // Matiin A & B
                    )
                }

                // Spacer bawah biar ga ketutup navigation bar
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Dialog Setting TDS
    if (showSettingsDialog) {
        TdsSettingsDialog(
            currentMinTds = tdsRange.min,
            currentMaxTds = tdsRange.max,
            onDismiss = { showSettingsDialog = false },
            onSave = { min, max ->
                viewModel.updateTdsRange(min, max)
                showSettingsDialog = false
            },
            onReset = {
                viewModel.resetTdsRange()
                showSettingsDialog = false
            }
        )
    }
}