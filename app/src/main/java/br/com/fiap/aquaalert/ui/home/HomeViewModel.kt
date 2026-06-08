package br.com.fiap.aquaalert.ui.home

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.*
import br.com.fiap.aquaalert.data.local.AppDatabase
import br.com.fiap.aquaalert.data.model.*
import br.com.fiap.aquaalert.data.repository.AlertRepository
import br.com.fiap.aquaalert.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository()
    private val alertRepository = AlertRepository(
        AppDatabase.getInstance(application).alertDao()
    )

    val activeAlerts = alertRepository.activeAlerts
    val activeAlertCount = alertRepository.activeAlertCount

    private val _currentWeather = MutableLiveData<CurrentWeather?>()
    val currentWeather: LiveData<CurrentWeather?> = _currentWeather

    private val _dailyForecast = MutableLiveData<DailyWeather?>()
    val dailyForecast: LiveData<DailyWeather?> = _dailyForecast

    private val _riskLevel = MutableLiveData<AlertSeverity>()
    val riskLevel: LiveData<AlertSeverity> = _riskLevel

    private val _locationName = MutableLiveData<String>("Carregando localização...")
    val locationName: LiveData<String> = _locationName

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val geocoder = Geocoder(getApplication(), Locale("pt", "BR"))
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val locationName = addresses?.firstOrNull()?.let { addr ->
                    "${addr.subLocality ?: addr.locality ?: "Sua Localização"}, ${addr.adminArea ?: ""}"
                } ?: "Sua Localização"
                _locationName.value = locationName

                val result = weatherRepository.getWeather(latitude, longitude)
                result.onSuccess { weather ->
                    _currentWeather.value = weather.current
                    _dailyForecast.value = weather.daily

                    val maxPrecipitation = weather.daily?.precipitationSum?.maxOrNull() ?: 0.0
                    val maxProbability = weather.daily?.precipitationProbabilityMax?.maxOrNull() ?: 0
                    _riskLevel.value = calculateRiskLevel(maxPrecipitation, maxProbability)

                    val alerts = weatherRepository.generateAlertsFromWeather(weather, locationName)
                    if (alerts.isNotEmpty()) {
                        alertRepository.insertAlerts(alerts)
                    }
                }.onFailure { e ->
                    _error.value = "Erro ao carregar dados climáticos: ${e.message}"
                    loadMockData()
                }
            } catch (e: Exception) {
                _error.value = "Erro de conexão. Verifique sua internet."
                loadMockData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateRiskLevel(precipitation: Double, probability: Int): AlertSeverity {
        return when {
            precipitation >= 80 && probability >= 80 -> AlertSeverity.CRITICAL
            precipitation >= 50 || (precipitation >= 30 && probability >= 70) -> AlertSeverity.HIGH
            precipitation >= 25 || probability >= 60 -> AlertSeverity.MEDIUM
            precipitation >= 10 || probability >= 40 -> AlertSeverity.LOW
            else -> AlertSeverity.LOW
        }
    }

    private suspend fun loadMockData() {
        val mockAlerts = listOf(
            FloodAlert(
                title = "⚠️ ALERTA ALTO - Risco de Alagamento",
                description = "Precipitação acumulada acima de 60mm nas últimas 24h. Evite áreas próximas ao rio.",
                location = "Centro - São Paulo, SP",
                latitude = -23.5505,
                longitude = -46.6333,
                severity = AlertSeverity.HIGH,
                status = AlertStatus.ACTIVE,
                precipitation = 65.0,
                affectedArea = "Centro e Bela Vista",
                source = "CEMADEN / Satélite GOES-16"
            ),
            FloodAlert(
                title = "🟠 ALERTA MÉDIO - Chuvas Intensas",
                description = "Probabilidade de 70% de chuvas fortes nas próximas 6 horas.",
                location = "Zona Leste - São Paulo, SP",
                latitude = -23.5400,
                longitude = -46.5100,
                severity = AlertSeverity.MEDIUM,
                status = AlertStatus.ACTIVE,
                precipitation = 35.0,
                affectedArea = "Itaquera e São Mateus",
                source = "INMET / Satélite GOES-16"
            )
        )
        alertRepository.insertAlerts(mockAlerts)

        _riskLevel.value = AlertSeverity.HIGH
        _locationName.value = "São Paulo, SP (Dados de Demonstração)"
    }
}
