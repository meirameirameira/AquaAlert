package br.com.fiap.aquaalert.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.fiap.aquaalert.databinding.FragmentAlertsBinding
import com.google.android.material.tabs.TabLayout

class AlertsFragment : Fragment() {

    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlertsViewModel by viewModels()
    private lateinit var adapter: AlertsAdapter
    private var showingAll = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AlertsAdapter { alert ->
            // Show detail dialog
        }

        binding.rvAlerts.apply {
            this.adapter = this@AlertsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                showingAll = tab?.position == 1
                updateList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.allAlerts.observe(viewLifecycleOwner) { updateList() }
        viewModel.activeAlerts.observe(viewLifecycleOwner) { updateList() }
    }

    private fun updateList() {
        val list = if (showingAll) {
            viewModel.allAlerts.value ?: emptyList()
        } else {
            viewModel.activeAlerts.value ?: emptyList()
        }
        adapter.submitList(list)
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
