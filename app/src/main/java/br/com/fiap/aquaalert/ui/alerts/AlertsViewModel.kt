package br.com.fiap.aquaalert.ui.alerts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.aquaalert.data.local.AppDatabase
import br.com.fiap.aquaalert.data.model.AlertStatus
import br.com.fiap.aquaalert.data.model.FloodAlert
import br.com.fiap.aquaalert.data.repository.AlertRepository
import kotlinx.coroutines.launch

class AlertsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlertRepository(
        AppDatabase.getInstance(application).alertDao()
    )

    val allAlerts = repository.allAlerts
    val activeAlerts = repository.activeAlerts

    fun resolveAlert(alert: FloodAlert) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(status = AlertStatus.RESOLVED))
        }
    }

    fun deleteAlert(alert: FloodAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
}
