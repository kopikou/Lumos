package com.example.lumos.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.databinding.ItemOrderBinding
import com.example.lumos.domain.entities.Order
import com.example.lumos.presentation.utils.DateFormatter

class OrderArtistsAdapter(
    private val onItemClick: (Order) -> Unit,
    private val dateFormatter: DateFormatter
) : ListAdapter<Order, OrderArtistsAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            with(binding) {
                orderDate.text = dateFormatter.formatShort(order.date)
                orderPerformance.text = order.performance.title
                root.setOnClickListener { onItemClick(order) }
            }
        }
    }
}

private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}