//package com.example.lumos.presentation.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.lumos.R
//import com.example.lumos.domain.entities.Order
//
//class OrderArtistsAdapter(
//    private val onItemClick: (Order) -> Unit
//) : RecyclerView.Adapter<OrderArtistsAdapter.OrderViewHolder>() {
//
//    private val orders = mutableListOf<Order>()
//
//    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val date: TextView = itemView.findViewById(R.id.orderDate)
//        val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_order, parent, false)
//        return OrderViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//        val order = orders[position]
//        holder.date.text = DateFormatter.formatShort(order.date)
//        holder.performance.text = order.performance.title
//
//        holder.itemView.setOnClickListener {
//            onItemClick(order)
//        }
//    }
//
//    override fun getItemCount() = orders.size
//
//    fun updateOrders(newOrders: List<Order>) {
//        orders.clear()
//        orders.addAll(newOrders)
//        notifyDataSetChanged()
//    }
//
//    fun updateOrderStatus(orderId: Int, isCompleted: Boolean) {
//        orders.find { it.id == orderId }?.completed = isCompleted
//        notifyDataSetChanged()
//    }
//}

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