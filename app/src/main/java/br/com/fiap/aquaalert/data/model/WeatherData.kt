package br.com.fiap.aquaalert.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("current") val current: CurrentWeather?,
    @SerializedName("hourly") val hourly: HourlyWeather?,
    @SerializedName("daily") val daily: DailyWeather?
)

data class CurrentWeather(
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("apparent_temperature") val feelsLike: Double,
    @SerializedName("precipitation") val precipitation: Double,
    @SerializedName("rain") val rain: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double
)

data class HourlyWeather(
    val time: List<String>,
    @SerializedName("precipitation") val precipitation: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>
)

data class DailyWeather(
    val time: List<String>,
    @SerializedName("precipitation_sum") val precipitationSum: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>
)
