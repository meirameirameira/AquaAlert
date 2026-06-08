package br.com.fiap.aquaalert.ui.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.aquaalert.R
import br.com.fiap.aquaalert.data.model.AlertSeverity
import br.com.fiap.aquaalert.data.model.AlertStatus
import br.com.fiap.aquaalert.data.model.FloodAlert
import br.com.fiap.aquaalert.databinding.ItemAlertBinding
import java.text.SimpleDateFormat
import java.util.*

class AlertsAdapter(
    private val onAlertClick: (FloodAlert) -> Unit
) : ListAdapter<FloodAlert, AlertsAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position), onAlertClick)
    }

    class AlertViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: FloodAlert, onClick: (FloodAlert) -> Unit) {
            binding.tvAlertTitle.text = alert.title
            binding.tvAlertDescription.text = alert.description
            binding.tvAlertLocation.text = "📍 ${alert.location}"
            binding.tvAlertTime.text = formatTimestamp(alert.timestamp)
            binding.tvAlertSource.text = "Fonte: ${alert.source}"
            binding.tvPrecipitation.text = "Precipitação: ${alert.precipitation}mm"

            val context = binding.root.context
            val (chipBg, chipText) = when (alert.severity) {
                AlertSeverity.CRITICAL -> Pair(R.color.risk_critical, "CRÍTICO")
                AlertSeverity.HIGH -> Pair(R.color.risk_high, "ALTO")
                AlertSeverity.MEDIUM -> Pair(R.color.risk_medium, "MÉDIO")
                AlertSeverity.LOW -> Pair(R.color.risk_low, "BAIXO")
            }

            binding.chipSeverity.text = chipText
            binding.chipSeverity.chipBackgroundColor =
                ContextCompat.getColorStateList(context, chipBg)

            binding.chipStatus.text = when (alert.status) {
                AlertStatus.ACTIVE -> "ATIVO"
                AlertStatus.RESOLVED -> "RESOLVIDO"
                AlertStatus.MONITORING -> "MONITORANDO"
            }

            val cardStrokeColor = when (alert.severity) {
                AlertSeverity.CRITICAL -> ContextCompat.getColor(context, R.color.risk_critical)
                AlertSeverity.HIGH -> ContextCompat.getColor(context, R.color.risk_high)
                AlertSeverity.MEDIUM -> ContextCompat.getColor(context, R.color.risk_medium)
                AlertSeverity.LOW -> ContextCompat.getColor(context, R.color.risk_low)
            }
            binding.cardAlert.strokeColor = cardStrokeColor

            binding.root.setOnClickListener { onClick(alert) }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
            return sdf.format(Date(timestamp))
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<FloodAlert>() {
        override fun areItemsTheSame(oldItem: FloodAlert, newItem: FloodAlert) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FloodAlert, newItem: FloodAlert) =
            oldItem == newItem
    }
}
