package br.com.fiap.aquaalert.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.fiap.aquaalert.data.model.FloodAlert
import br.com.fiap.aquaalert.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by viewModels()
    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(GeoPoint(-23.5505, -46.6333))

        setupLocationOverlay()
        addFloodRiskZones()

        viewModel.alerts.observe(viewLifecycleOwner) { addAlertMarkers(it) }

        binding.legendContainer.visibility = View.VISIBLE
        binding.fabMyLocation.setOnClickListener { centerOnMyLocation() }
    }

    private fun setupLocationOverlay() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)

        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationOverlay.enableMyLocation()
            // Assim que encontrar a localização pela primeira vez, centraliza automaticamente
            locationOverlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    mapView.controller.animateTo(locationOverlay.myLocation)
                    mapView.controller.setZoom(15.0)
                }
            }
        }

        mapView.overlays.add(locationOverlay)
    }

    private fun centerOnMyLocation() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Toast.makeText(requireContext(), "Permissão de localização necessária", Toast.LENGTH_SHORT).show()
            return
        }

        val myLocation = locationOverlay.myLocation
        if (myLocation != null) {
            mapView.controller.animateTo(myLocation)
            mapView.controller.setZoom(15.0)
        } else {
            // Localização ainda não disponível — habilita e aguarda o primeiro fix
            locationOverlay.enableMyLocation()
            locationOverlay.runOnFirstFix {
                requireActivity().runOnUiThread {
                    mapView.controller.animateTo(locationOverlay.myLocation)
                    mapView.controller.setZoom(15.0)
                }
            }
            Toast.makeText(requireContext(), "Obtendo localização...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFloodRiskZones() {
        val highRiskZones = listOf(
            listOf(
                GeoPoint(-23.560, -46.645), GeoPoint(-23.555, -46.625),
                GeoPoint(-23.570, -46.620), GeoPoint(-23.575, -46.640)
            ),
            listOf(
                GeoPoint(-23.530, -46.610), GeoPoint(-23.525, -46.595),
                GeoPoint(-23.540, -46.590), GeoPoint(-23.545, -46.605)
            )
        )

        val mediumRiskZones = listOf(
            listOf(
                GeoPoint(-23.515, -46.630), GeoPoint(-23.510, -46.615),
                GeoPoint(-23.525, -46.610), GeoPoint(-23.530, -46.625)
            )
        )

        highRiskZones.forEach { points ->
            mapView.overlays.add(Polygon().apply {
                this.points = points
                fillColor = Color.argb(80, 244, 67, 54)
                strokeColor = Color.rgb(244, 67, 54)
                strokeWidth = 3f
                title = "Zona de Alto Risco"
            })
        }

        mediumRiskZones.forEach { points ->
            mapView.overlays.add(Polygon().apply {
                this.points = points
                fillColor = Color.argb(80, 255, 152, 0)
                strokeColor = Color.rgb(255, 152, 0)
                strokeWidth = 3f
                title = "Zona de Risco Médio"
            })
        }
    }

    private fun addAlertMarkers(alerts: List<FloodAlert>) {
        alerts.forEach { alert ->
            mapView.overlays.add(Marker(mapView).apply {
                position = GeoPoint(alert.latitude, alert.longitude)
                title = alert.title
                snippet = "${alert.location} | Chuva: ${alert.precipitation}mm"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
        }
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        locationOverlay.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        locationOverlay.disableMyLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
