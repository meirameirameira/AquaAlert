package br.com.fiap.aquaalert.data.api

import br.com.fiap.aquaalert.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "precipitation,precipitation_probability",
        @Query("daily") daily: String = "precipitation_sum,precipitation_probability_max,temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "America/Sao_Paulo",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("forecast_hours") forecastHours: Int = 24
    ): WeatherResponse
}
