package br.com.fiap.aquaalert.data.repository

import br.com.fiap.aquaalert.data.local.AlertDao
import br.com.fiap.aquaalert.data.model.FloodAlert

class AlertRepository(private val alertDao: AlertDao) {

    val allAlerts = alertDao.getAllAlerts()
    val activeAlerts = alertDao.getActiveAlerts()
    val activeAlertCount = alertDao.getActiveAlertCount()

    suspend fun insertAlert(alert: FloodAlert) = alertDao.insertAlert(alert)

    suspend fun insertAlerts(alerts: List<FloodAlert>) {
        alertDao.deleteAll()
        alertDao.insertAlerts(alerts)
    }

    suspend fun updateAlert(alert: FloodAlert) = alertDao.updateAlert(alert)

    suspend fun deleteAlert(alert: FloodAlert) = alertDao.deleteAlert(alert)
}
