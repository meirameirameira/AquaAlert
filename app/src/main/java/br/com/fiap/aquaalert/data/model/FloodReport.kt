package br.com.fiap.aquaalert.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReportType(val label: String) {
    FLOODED_ROAD("Rua Alagada"),
    OVERFLOWING_RIVER("Rio Transbordando"),
    FLOODED_NEIGHBORHOOD("Bairro Inundado"),
    BLOCKED_DRAIN("Bueiro Entupido"),
    LANDSLIDE("Deslizamento"),
    OTHER("Outro")
}

@Entity(tableName = "flood_reports")
data class FloodReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: ReportType,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val severity: AlertSeverity,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val verified: Boolean = false
)
