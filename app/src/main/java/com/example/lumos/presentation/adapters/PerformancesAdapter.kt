package com.example.lumos.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.presentation.viewModels.ManagementViewModel

class PerformancesAdapter : ListAdapter<ManagementViewModel.PerformanceWithRate, PerformancesAdapter.PerformanceViewHolder>(PerformanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performance_with_rate, parent, false)
        return PerformanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.performance_title)
        private val rateText: TextView = itemView.findViewById(R.id.performance_rate)

        fun bind(performanceWithRate: ManagementViewModel.PerformanceWithRate) {
            titleText.text = performanceWithRate.performance.title
            rateText.text = "Ставка: ${performanceWithRate.rate} руб."
        }
    }

    class PerformanceDiffCallback : DiffUtil.ItemCallback<ManagementViewModel.PerformanceWithRate>() {
        override fun areItemsTheSame(
            oldItem: ManagementViewModel.PerformanceWithRate,
            newItem: ManagementViewModel.PerformanceWithRate
        ): Boolean {
            return oldItem.performance.id == newItem.performance.id
        }

        override fun areContentsTheSame(
            oldItem: ManagementViewModel.PerformanceWithRate,
            newItem: ManagementViewModel.PerformanceWithRate
        ): Boolean {
            return oldItem == newItem
        }
    }
}