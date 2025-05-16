package com.example.lumos.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Performance

class PerformancesSimpleAdapter(
    private val onItemClick: (Performance) -> Unit
) : ListAdapter<Performance, PerformancesSimpleAdapter.PerformanceViewHolder>(PerformanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performance_simple, parent, false)
        return PerformanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.performance_title)

        fun bind(performance: Performance) {
            titleText.text = performance.title
            itemView.setOnClickListener { onItemClick(performance) }
        }
    }

    class PerformanceDiffCallback : DiffUtil.ItemCallback<Performance>() {
        override fun areItemsTheSame(oldItem: Performance, newItem: Performance): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Performance, newItem: Performance): Boolean {
            return oldItem == newItem
        }
    }
}