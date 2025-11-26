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
import com.example.dashboardhidroponikkangkung.ui.theme.StatusCritical
import com.example.dashboardhidroponikkangkung.ui.theme.StatusOptimal

@Composable
fun ManualControlCard(
    isAutomatic: Boolean,
    onToggleMode: () -> Unit,
    onActivatePumps: () -> Unit,
    onDeactivatePumps: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showActivateDialog by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header + Switch Mode
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
                        checked = !isAutomatic, // true = Manual
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

            // Konten Manual
            AnimatedVisibility(visible = !isAutomatic) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Info Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Kontrol Pompa Pupuk",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pompa A dan B akan menyala/mati bersamaan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Tombol Aktifkan
                    Button(
                        onClick = { showActivateDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusOptimal,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Aktifkan Pompa",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tombol Matikan
                    OutlinedButton(
                        onClick = { showDeactivateDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StatusCritical
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Matikan Pompa",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Info kalau lagi mode Auto
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
                        text = "Mode otomatis aktif. Matikan switch untuk kontrol manual.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

    // === Dialog Konfirmasi Aktifkan ===
    if (showActivateDialog) {
        AlertDialog(
            onDismissRequest = { showActivateDialog = false },
            title = {
                Text("Konfirmasi Aktivasi", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Aktifkan Pompa Pupuk A dan B sekarang?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onActivatePumps()          // Langsung panggil ViewModel
                    showActivateDialog = false
                }) {
                    Text("Aktifkan", color = StatusOptimal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showActivateDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // === Dialog Konfirmasi Matikan ===
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = {
                Text("Konfirmasi Mematikan", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Matikan Pompa Pupuk A dan B sekarang?")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeactivatePumps()        // Langsung panggil ViewModel
                    showDeactivateDialog = false
                }) {
                    Text("Matikan", color = StatusCritical, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}