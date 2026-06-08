package br.com.fiap.aquaalert.data.local

import androidx.room.TypeConverter
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.data.model.AlertStatus
import br.com.fiap.aquaalert.data.model.ReportType

class Converters {

    @TypeConverter
    fun fromAlertSeverity(value: AlertSeverity): String = value.name

    @TypeConverter
    fun toAlertSeverity(value: String): AlertSeverity = AlertSeverity.valueOf(value)

    @TypeConverter
    fun fromAlertStatus(value: AlertStatus): String = value.name

    @TypeConverter
    fun toAlertStatus(value: String): AlertStatus = AlertStatus.valueOf(value)

    @TypeConverter
    fun fromReportType(value: ReportType): String = value.name

    @TypeConverter
    fun toReportType(value: String): ReportType = ReportType.valueOf(value)
}
