package br.com.fiap.aquaalert.ui.report

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.data.model.ReportType
import br.com.fiap.aquaalert.databinding.FragmentReportBinding
import com.google.android.material.snackbar.Snackbar

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()

    private var currentLatitude = -23.5505
    private var currentLongitude = -46.6333

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setupObservers()
        loadLocation()

        binding.btnSubmitReport.setOnClickListener { submitReport() }
    }

    private fun setupDropdowns() {
        val reportTypes = ReportType.values().map { it.label }
        binding.actvReportType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reportTypes)
        )

        val severities = listOf("Baixo", "Médio", "Alto", "Crítico")
        binding.actvSeverity.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, severities)
        )
    }

    private fun setupObservers() {
        viewModel.submitSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root,
                    "✅ Ocorrência registrada! Obrigado pela contribuição.",
                    Snackbar.LENGTH_LONG).show()
                clearForm()
            } else {
                Snackbar.make(binding.root, "❌ Erro ao registrar. Tente novamente.", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.isSubmitting.observe(viewLifecycleOwner) { isSubmitting ->
            binding.btnSubmitReport.isEnabled = !isSubmitting
            binding.progressSubmit.visibility = if (isSubmitting) View.VISIBLE else View.GONE
        }
    }

    private fun loadLocation() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                binding.tvCurrentLocation.text =
                    "📍 Lat: ${String.format("%.4f", location.latitude)}, " +
                    "Lon: ${String.format("%.4f", location.longitude)}"
            }
        }
    }

    private fun submitReport() {
        val typeText = binding.actvReportType.text.toString()
        val description = binding.etDescription.text.toString()
        val address = binding.etAddress.text.toString()
        val severityText = binding.actvSeverity.text.toString()

        if (typeText.isEmpty() || description.isEmpty() || address.isEmpty()) {
            Snackbar.make(binding.root, "Preencha todos os campos obrigatórios.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val type = ReportType.values().firstOrNull { it.label == typeText } ?: ReportType.OTHER
        val severity = when (severityText) {
            "Crítico" -> AlertSeverity.CRITICAL
            "Alto"    -> AlertSeverity.HIGH
            "Médio"   -> AlertSeverity.MEDIUM
            else      -> AlertSeverity.LOW
        }

        viewModel.submitReport(type, description, address, currentLatitude, currentLongitude, severity)
    }

    private fun clearForm() {
        binding.actvReportType.text.clear()
        binding.etDescription.text?.clear()
        binding.etAddress.text?.clear()
        binding.actvSeverity.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
