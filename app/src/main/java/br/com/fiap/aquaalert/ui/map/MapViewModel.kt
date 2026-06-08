package br.com.fiap.aquaalert.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.aquaalert.data.local.AppDatabase
import br.com.fiap.aquaalert.data.repository.AlertRepository
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlertRepository(
        AppDatabase.getInstance(application).alertDao()
    )

    val alerts = repository.activeAlerts

    fun loadAlerts() {
        // Alerts are loaded reactively via LiveData
    }
}
