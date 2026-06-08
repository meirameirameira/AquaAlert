package br.com.fiap.aquaalert.data.repository

import br.com.fiap.aquaalert.data.api.ApiClient
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.data.model.AlertStatus
import br.com.fiap.aquaalert.data.model.FloodAlert
import br.com.fiap.aquaalert.data.model.WeatherResponse

class WeatherRepository {

    private val weatherService = ApiClient.weatherService

    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return try {
            val response = weatherService.getWeatherForecast(latitude, longitude)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun generateAlertsFromWeather(weather: WeatherResponse, locationName: String): List<FloodAlert> {
        val alerts = mutableListOf<FloodAlert>()
        val daily = weather.daily ?: return alerts

        daily.precipitationSum.forEachIndexed { index, precipitation ->
            val probability = daily.precipitationProbabilityMax.getOrNull(index) ?: 0
            val date = daily.time.getOrNull(index) ?: return@forEachIndexed

            val severity = when {
                precipitation >= 80 && probability >= 80 -> AlertSeverity.CRITICAL
                precipitation >= 50 || (precipitation >= 30 && probability >= 70) -> AlertSeverity.HIGH
                precipitation >= 25 || probability >= 60 -> AlertSeverity.MEDIUM
                precipitation >= 10 || probability >= 40 -> AlertSeverity.LOW
                else -> return@forEachIndexed
            }

            alerts.add(
                FloodAlert(
                    title = buildAlertTitle(severity, date),
                    description = buildAlertDescription(precipitation, probability, severity),
                    location = locationName,
                    latitude = weather.latitude,
                    longitude = weather.longitude,
                    severity = severity,
                    status = AlertStatus.ACTIVE,
                    precipitation = precipitation,
                    affectedArea = locationName,
                    source = "Open-Meteo / Satélite GOES-16"
                )
            )
        }

        return alerts
    }

    private fun buildAlertTitle(severity: AlertSeverity, date: String): String {
        return when (severity) {
            AlertSeverity.CRITICAL -> "⚠️ ALERTA CRÍTICO - Risco Extremo de Enchente ($date)"
            AlertSeverity.HIGH -> "🔴 ALERTA ALTO - Risco Elevado de Alagamento ($date)"
            AlertSeverity.MEDIUM -> "🟠 ALERTA MÉDIO - Atenção para Chuvas Intensas ($date)"
            AlertSeverity.LOW -> "🟡 AVISO - Possibilidade de Chuva Forte ($date)"
        }
    }

    private fun buildAlertDescription(
        precipitation: Double,
        probability: Int,
        severity: AlertSeverity
    ): String {
        val action = when (severity) {
            AlertSeverity.CRITICAL -> "Evite áreas de risco. Acione a Defesa Civil se necessário."
            AlertSeverity.HIGH -> "Evite áreas baixas e margens de rios. Fique em local seguro."
            AlertSeverity.MEDIUM -> "Tome precauções. Evite áreas propensas a alagamento."
            AlertSeverity.LOW -> "Fique atento às condições climáticas ao longo do dia."
        }
        return "Precipitação prevista: ${precipitation}mm. Probabilidade: ${probability}%. $action"
    }
}
