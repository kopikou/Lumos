package com.example.lumos.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lumos.R
import com.example.lumos.domain.entities.Earning
import com.example.lumos.domain.entities.Order

class HistoryOrderAdapter(
    private val context: Context,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<HistoryOrderAdapter.OrderViewHolder>() {
    private val orders = mutableListOf<Order>()
    private val earningsMap = mutableMapOf<Int, Earning>()

    fun updateOrders(newOrders: List<Order>, earnings: List<Earning>) {
        orders.clear()
        earningsMap.clear()
        orders.addAll(newOrders)
        earnings.forEach { earning ->
            earningsMap[earning.order.id] = earning
        }
        println(earningsMap)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.date.text = DateFormatter.formatShort(order.date)
        holder.performance.text = order.performance.title
        println(order)

        // Получаем информацию о выплате
        val earning = earningsMap[order.id]
        println(earning)

        // Устанавливаем иконку в зависимости от статусов
        val iconRes = R.drawable.ic_check_circle

        val iconColor = when {
            !order.completed -> R.color.orange
            earning?.paid == true -> R.color.green
            else -> R.color.yellow
        }

        val drawable = AppCompatResources.getDrawable(context, iconRes)?.mutate()
        drawable?.setTint(ContextCompat.getColor(context, iconColor))
        holder.statusIcon.setImageDrawable(drawable)
        holder.statusIcon.contentDescription = when {
            !order.completed -> "Заказ не выполнен"
            earning?.paid == true -> "Зарплата выплачена"
            else -> "Ожидает выплаты"
        }

        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.orderDate)
        val performance: TextView = itemView.findViewById(R.id.orderPerformance)
        val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = orders.size

}