package br.com.fiap.aquaalert.ui.report

import android.app.Application
import androidx.lifecycle.*
import br.com.fiap.aquaalert.data.local.AppDatabase
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.data.model.FloodReport
import br.com.fiap.aquaalert.data.model.ReportType
import kotlinx.coroutines.launch

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _submitSuccess = MutableLiveData<Boolean>()
    val submitSuccess: LiveData<Boolean> = _submitSuccess

    private val _isSubmitting = MutableLiveData<Boolean>(false)
    val isSubmitting: LiveData<Boolean> = _isSubmitting

    fun submitReport(
        type: ReportType,
        description: String,
        address: String,
        latitude: Double,
        longitude: Double,
        severity: AlertSeverity,
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val report = FloodReport(
                    type = type,
                    description = description,
                    address = address,
                    latitude = latitude,
                    longitude = longitude,
                    severity = severity,
                    photoUri = photoUri
                )
                db.reportDao().insertReport(report)
                _submitSuccess.value = true
            } catch (e: Exception) {
                _submitSuccess.value = false
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
