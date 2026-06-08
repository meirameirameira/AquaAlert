package br.com.fiap.aquaalert.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AlertSeverity(val label: String, val color: String) {
    LOW("Baixo", "#4CAF50"),
    MEDIUM("Médio", "#FF9800"),
    HIGH("Alto", "#F44336"),
    CRITICAL("Crítico", "#9C27B0")
}

enum class AlertStatus { ACTIVE, RESOLVED, MONITORING }

@Entity(tableName = "flood_alerts")
data class FloodAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val severity: AlertSeverity,
    val status: AlertStatus,
    val precipitation: Double,
    val affectedArea: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "AquaAlert / Satélite"
)
