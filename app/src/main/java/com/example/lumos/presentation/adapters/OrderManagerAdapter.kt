package com.example.lumos.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Order
import com.example.lumos.presentation.utils.DateFormatter

class OrderManagerAdapter (
    private val onItemClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderManagerAdapter.OrderViewHolder>() {
    private var orders = mutableListOf<Order>()

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.orderDate)
        val performance: TextView = itemView.findViewById(R.id.orderPerformance)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_manager, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.date.text = DateFormatter.formatShort(order.date)
        holder.performance.text = order.performance.title

        holder.itemView.setOnClickListener {
            onItemClick(order)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(order)
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun getItemCount() = orders.size
}