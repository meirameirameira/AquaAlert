package br.com.fiap.aquaalert.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.fiap.aquaalert.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.databinding.FragmentHomeBinding
import br.com.fiap.aquaalert.ui.alerts.AlertsAdapter
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var alertsAdapter: AlertsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadUserLocation()
    }

    private fun setupRecyclerView() {
        alertsAdapter = AlertsAdapter { }
        binding.rvRecentAlerts.apply {
            adapter = alertsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            weather?.let {
                binding.tvTemperature.text = "${it.temperature.toInt()}°C"
                binding.tvHumidity.text = "Umidade: ${it.humidity}%"
                binding.tvWindSpeed.text = "Vento: ${it.windSpeed.toInt()} km/h"
                binding.tvPrecipitation.text = "Chuva atual: ${it.precipitation}mm"
                binding.tvWeatherDescription.text = getWeatherDescription(it.weatherCode)
            }
        }

        viewModel.riskLevel.observe(viewLifecycleOwner) { severity ->
            updateRiskCard(severity)
        }

        viewModel.activeAlerts.observe(viewLifecycleOwner) { alerts ->
            alertsAdapter.submitList(alerts.take(3))
            binding.tvNoAlerts.visibility = if (alerts.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRecentAlerts.visibility = if (alerts.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.activeAlertCount.observe(viewLifecycleOwner) { count ->
            binding.tvAlertCount.text = if (count > 0) "$count alerta(s) ativo(s)" else "Sem alertas ativos"
        }

        viewModel.locationName.observe(viewLifecycleOwner) { name ->
            binding.tvLocation.text = name
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.cardWeather.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
        }
    }

    private fun setupClickListeners() {
        binding.btnViewMap.setOnClickListener { selectBottomNavTab(R.id.mapFragment) }
        binding.btnViewAllAlerts.setOnClickListener { selectBottomNavTab(R.id.alertsFragment) }
        binding.btnReport.setOnClickListener { selectBottomNavTab(R.id.reportFragment) }
        binding.btnRefresh.setOnClickListener { loadUserLocation() }
    }

    private fun selectBottomNavTab(itemId: Int) {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = itemId
    }

    private fun loadUserLocation() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                viewModel.loadWeatherData(location.latitude, location.longitude)
            } else {
                viewModel.loadWeatherData(-23.5505, -46.6333)
            }
        } else {
            viewModel.loadWeatherData(-23.5505, -46.6333)
        }
    }

    private fun updateRiskCard(severity: AlertSeverity) {
        val (bgColor, label, icon) = when (severity) {
            AlertSeverity.CRITICAL -> Triple(R.color.risk_critical, "RISCO CRÍTICO", R.drawable.ic_warning)
            AlertSeverity.HIGH     -> Triple(R.color.risk_high,     "RISCO ALTO",    R.drawable.ic_warning)
            AlertSeverity.MEDIUM   -> Triple(R.color.risk_medium,   "RISCO MÉDIO",   R.drawable.ic_warning)
            AlertSeverity.LOW      -> Triple(R.color.risk_low,      "RISCO BAIXO",   R.drawable.ic_info)
        }
        binding.cardRisk.setCardBackgroundColor(ContextCompat.getColor(requireContext(), bgColor))
        binding.tvRiskLevel.text = label
        binding.tvRiskLevel.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.ivRiskIcon.setImageResource(icon)
    }

    private fun getWeatherDescription(code: Int) = when (code) {
        0 -> "Céu limpo"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Neblina"
        51, 53, 55 -> "Garoa leve"
        61, 63, 65 -> "Chuva"
        80, 81, 82 -> "Pancadas de chuva"
        95 -> "Tempestade"
        96, 99 -> "Tempestade com granizo"
        else -> "Condições variáveis"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
