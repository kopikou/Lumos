package com.example.lumos.presentation.adapters

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class HistoryOrderAdapter(
//    private val onItemClick: (Order) -> Unit
//) : RecyclerView.Adapter<HistoryOrderAdapter.OrderViewHolder>() {
//
//    private val orders = mutableListOf<Order>()
//    private val earningsMap = mutableMapOf<Int, Earning>() // orderId to Earning
//
//    //        fun updateOrders(newOrders: List<Order>, earnings: List<Earning>) {
////            orders.clear()
////            earningsMap.clear()
////            orders.addAll(newOrders)
////            earnings.forEach { earning ->
////                earningsMap[earning.order.id] = earning
////            }
////            notifyDataSetChanged()
////        }
//    fun updateOrders(newOrders: List<Order>, earnings: Map<Int, Earning>) {
//        orders.clear()
//        earningsMap.clear()
//        orders.addAll(newOrders)
//        earningsMap.putAll(earnings)
//        notifyDataSetChanged()
//    }
//
//    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//        val order = orders[position]
//        holder.date.text = DateFormatter.formatShort(order.date)
//        holder.performance.text = order.performance.title
//
//        // Получаем информацию о выплате
//        val earning = earningsMap[order.id]
//
//        // Устанавливаем иконку в зависимости от статусов
//        val iconRes = R.drawable.ic_check_circle
//
//        val iconColor = when {
//            !order.completed -> R.color.orange
//            earning?.paid == true -> R.color.green
//            else -> R.color.yellow
//        }
//
//        val drawable = AppCompatResources.getDrawable(requireContext(), iconRes)?.mutate()
//        drawable?.setTint(ContextCompat.getColor(requireContext(), iconColor))
//        holder.statusIcon.setImageDrawable(drawable)
//        holder.statusIcon.contentDescription = when {
//            !order.completed -> "Заказ не выполнен"
//            earning?.paid == true -> "Зарплата выплачена"
//            else -> "Ожидает выплаты"
//        }
//
//        holder.itemView.setOnClickListener {
//            onItemClick(order)
//        }
//    }
//
//    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val date: TextView = itemView.findViewById(R.id.orderDate)
//        val performance: TextView = itemView.findViewById(R.id.orderPerformance)
//        val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_order_history, parent, false)
//        return OrderViewHolder(view)
//    }
//
//    override fun getItemCount() = orders.size
//
//}
// HistoryOrderAdapter.kt

class HistoryOrderAdapter(
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<HistoryOrderAdapter.OrderViewHolder>() {

    private val orders = mutableListOf<Order>()
    private val earningsMap = mutableMapOf<Int, Earning>()

//    fun updateOrders(newOrders: List<Order>, earnings: Map<Int, Earning>) {
//        orders.clear()
//        earningsMap.clear()
//        orders.addAll(newOrders)
//        earningsMap.putAll(earnings)
//        notifyDataSetChanged()
//    }
    fun updateOrders(newOrders: List<Order>, earnings: List<Earning>) {
        orders.clear()
        earningsMap.clear()
        orders.addAll(newOrders)
        earningsMap.putAll(earnings.associateBy { it.order.id })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order, earningsMap[order.id], onItemClick)
    }

    override fun getItemCount() = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val date: TextView = itemView.findViewById(R.id.orderDate)
        private val performance: TextView = itemView.findViewById(R.id.orderPerformance)
        private val statusIcon: ImageView = itemView.findViewById(R.id.ivStatus)

        fun bind(order: Order, earning: Earning?, onItemClick: (Order) -> Unit) {
            date.text = DateFormatter.formatShort(order.date)
            performance.text = order.performance.title

            val iconRes = R.drawable.ic_check_circle
            val iconColor = when {
                !order.completed -> R.color.orange
                earning?.paid == true -> R.color.green
                else -> R.color.yellow
            }

            AppCompatResources.getDrawable(itemView.context, iconRes)?.let { drawable ->
                drawable.setTint(ContextCompat.getColor(itemView.context, iconColor))
                statusIcon.setImageDrawable(drawable)
            }

            statusIcon.contentDescription = when {
                !order.completed -> "Заказ не выполнен"
                earning?.paid == true -> "Зарплата выплачена"
                else -> "Ожидает выплаты"
            }

            itemView.setOnClickListener { onItemClick(order) }
        }
    }
}
//object DateFormatter {
//    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    private val outputFormatLong = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
//    private val outputFormatShort = SimpleDateFormat("dd MMM", Locale("ru"))
//
//    fun formatLong(dateString: String): String {
//        return try {
//            val date = inputFormat.parse(dateString)
//            outputFormatLong.format(date ?: Date())
//        } catch (e: Exception) {
//            dateString
//        }
//    }
//
//    fun formatShort(dateString: String): String {
//        return try {
//            val date = inputFormat.parse(dateString)
//            outputFormatShort.format(date ?: Date())
//        } catch (e: Exception) {
//            dateString
//        }
//    }
//}