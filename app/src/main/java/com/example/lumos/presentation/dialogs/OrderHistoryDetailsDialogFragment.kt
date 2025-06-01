package com.example.lumos.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.domain.usecases.OrderDetails
import com.example.lumos.presentation.utils.DateFormatter
import com.example.lumos.presentation.viewModels.artists.ManagementArtistViewModel
import kotlinx.coroutines.launch
import android.view.View
import com.example.lumos.presentation.views.fragments.artists.ManagementFragmentArtist

class OrderHistoryDetailsDialogFragment : DialogFragment() {
    private var orderId: Int = -1
    private val viewModel: ManagementArtistViewModel by lazy {
        val parentFragment = parentFragment as? ManagementFragmentArtist
        parentFragment?.viewModel ?: throw IllegalStateException("Parent fragment must implement ViewModel provider")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getInt(ARG_ORDER_ID, -1) ?: -1
        if (orderId == -1) {
            dismiss()
            return
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_order_history_details, null)

        lifecycleScope.launch {
            try {
                val details = viewModel.getOrderDetails(orderId)
                updateDialogView(view, details)
            } catch (e: Exception) {
                dismiss()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_loading_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_details_title)
            .setView(view)
            .setPositiveButton(R.string.close, null)
            .create()
    }

    private fun updateDialogView(view: View, details: OrderDetails) {
        val order = details.order
        val earning = details.earning
        val resources = requireContext().resources

        with(view) {
            findViewById<TextView>(R.id.tvDate).text = DateFormatter.formatLong(order.date)
            findViewById<TextView>(R.id.tvPerformance).text = order.performance.title
            findViewById<TextView>(R.id.tvLocation).text = order.location
            findViewById<TextView>(R.id.tvComment).text = order.comment
            findViewById<TextView>(R.id.tvAmount).text =
                resources.getString(R.string.price_format, order.amount)

            // Status texts
            val completionStatus = if (order.completed) {
                resources.getString(R.string.completed)
            } else {
                resources.getString(R.string.not_completed)
            }

            val paymentStatus = when {
                !order.completed -> resources.getString(R.string.not_payable)
                earning == null -> resources.getString(R.string.payment_not_found)
                earning.paid -> resources.getString(R.string.payment_paid, earning.amount)
                else -> resources.getString(R.string.payment_pending, earning.amount)
            }

            // Status colors
            val completionColor = if (order.completed) R.color.green else R.color.orange
            val paymentColor = when {
                !order.completed -> R.color.orange
                earning?.paid == true -> R.color.green
                else -> R.color.yellow
            }

            findViewById<TextView>(R.id.tvCompletionStatus).apply {
                text = completionStatus
                setTextColor(ContextCompat.getColor(requireContext(), completionColor))
            }

            findViewById<TextView>(R.id.tvPaymentStatus).apply {
                text = paymentStatus
                setTextColor(ContextCompat.getColor(requireContext(), paymentColor))
            }
        }
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        fun newInstance(orderId: Int): OrderHistoryDetailsDialogFragment {
            return OrderHistoryDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ORDER_ID, orderId)
                }
            }
        }
    }
}
