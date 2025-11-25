package com.example.dashboardhidroponikkangkung.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dashboardhidroponikkangkung.ui.theme.StatusOptimal
import kotlin.math.roundToInt

@Composable
fun ManualControlCard(
    isAutomatic: Boolean,
    onToggleMode: () -> Unit,
    onActivatePump: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var pendingPump by remember { mutableStateOf("") }
    var pendingDuration by remember { mutableIntStateOf(0) }

    var pumpADuration by remember { mutableFloatStateOf(5f) }
    var pumpBDuration by remember { mutableFloatStateOf(5f) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kontrol Manual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isAutomatic) "Auto" else "Manual",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isAutomatic) StatusOptimal else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = !isAutomatic,
                        onCheckedChange = { onToggleMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onSurface,
                            checkedTrackColor = StatusOptimal,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = !isAutomatic) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Pump A Control
                    PumpControl(
                        title = "Pompa Pupuk A",
                        duration = pumpADuration,
                        onDurationChange = { pumpADuration = it },
                        onActivate = {
                            pendingPump = "A"
                            pendingDuration = pumpADuration.roundToInt()
                            showDialog = true
                        },
                        enabled = !isAutomatic
                    )

                    // Pump B Control
                    PumpControl(
                        title = "Pompa Pupuk B",
                        duration = pumpBDuration,
                        onDurationChange = { pumpBDuration = it },
                        onActivate = {
                            pendingPump = "B"
                            pendingDuration = pumpBDuration.roundToInt()
                            showDialog = true
                        },
                        enabled = !isAutomatic
                    )
                }
            }

            AnimatedVisibility(visible = isAutomatic) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mode otomatis aktif. Matikan untuk kontrol manual.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Aktivasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Aktifkan ${
                        when (pendingPump) {
                            "A" -> "Pompa Pupuk A"
                            "B" -> "Pompa Pupuk B"
                            else -> "Pompa"
                        }
                    } selama $pendingDuration detik?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onActivatePump(pendingPump, pendingDuration)
                        showDialog = false
                    }
                ) {
                    Text("Aktifkan", color = StatusOptimal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun PumpControl(
    title: String,
    duration: Float,
    onDurationChange: (Float) -> Unit,
    onActivate: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${duration.roundToInt()} detik",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = duration,
                onValueChange = onDurationChange,
                valueRange = 1f..30f,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onSurface,
                    activeTrackColor = MaterialTheme.colorScheme.onSurface,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onActivate,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StatusOptimal,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aktifkan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}