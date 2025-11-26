package com.example.dashboardhidroponikkangkung.data.model

data class SensorData(
    val tds: Int = 0,
    val timestamp: Long = 0L
)

data class PumpStatus(
    val status: Boolean = false,
    val activationCount: Long = 0,
    val lastActivation: Long = 0L
)

data class HidroponikData(
    val sensors: SensorData = SensorData(),
    val pumpA: PumpStatus = PumpStatus(),
    val pumpB: PumpStatus = PumpStatus(),
    val isAutomatic: Boolean = true,
    val history: List<HistoryData> = emptyList()
)

data class HistoryData(
    val tds: Int = 0,
    val timestamp: Long = 0L
)

enum class TdsStatus {
    OPTIMAL,    // 1050-1400
    LOW,        // < 1050
    HIGH        // > 1400
}

fun getTdsStatus(tds: Int, minTds: Int = 1050, maxTds: Int = 1400): TdsStatus {
    return when {
        tds < minTds -> TdsStatus.LOW
        tds > maxTds -> TdsStatus.HIGH
        else -> TdsStatus.OPTIMAL
    }
}