package br.com.fiap.aquaalert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AquaAlertApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Alertas de Enchente",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de risco de enchente na sua região"
            }

            val infoChannel = NotificationChannel(
                CHANNEL_INFO,
                "Informações Climáticas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Atualizações gerais sobre condições climáticas"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannels(listOf(alertChannel, infoChannel))
        }
    }

    companion object {
        const val CHANNEL_ALERTS = "channel_flood_alerts"
        const val CHANNEL_INFO = "channel_weather_info"
    }
}
