package com.example.lumos.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.lumos.R
import com.example.lumos.databinding.DialogOrderDetailsBinding
import com.example.lumos.domain.entities.Order
import com.example.lumos.presentation.utils.DateFormatter
import java.text.NumberFormat
import java.util.Locale

class OrderDetailsDialogFragment : DialogFragment() {

    interface OnStatusChangedListener {
        fun onStatusChanged(orderId: Int, isCompleted: Boolean)
    }

    private var _binding: DialogOrderDetailsBinding? = null
    private val binding get() = _binding!!
    var listener: OnStatusChangedListener? = null

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        private const val ARG_DATE = "date"
        private const val ARG_PERFORMANCE_TITLE = "performance_title"
        private const val ARG_LOCATION = "location"
        private const val ARG_COMMENT = "comment"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_COMPLETED = "completed"

        fun newInstance(order: Order): OrderDetailsDialogFragment {
            return OrderDetailsDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ORDER_ID, order.id)
                    putString(ARG_DATE, order.date)
                    putString(ARG_PERFORMANCE_TITLE, order.performance.title)
                    putString(ARG_LOCATION, order.location)
                    putString(ARG_COMMENT, order.comment)
                    putDouble(ARG_AMOUNT, order.amount)
                    putBoolean(ARG_COMPLETED, order.completed)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogOrderDetailsBinding.inflate(layoutInflater)

        val orderId = requireArguments().getInt(ARG_ORDER_ID)
        val date = requireArguments().getString(ARG_DATE) ?: ""
        val performanceTitle = requireArguments().getString(ARG_PERFORMANCE_TITLE) ?: ""
        val location = requireArguments().getString(ARG_LOCATION) ?: ""
        val comment = requireArguments().getString(ARG_COMMENT) ?: ""
        val amount = requireArguments().getDouble(ARG_AMOUNT)
        val isCompleted = requireArguments().getBoolean(ARG_COMPLETED)

        with(binding) {
            tvDate.text = DateFormatter.formatLong(date)
            tvPerformance.text = performanceTitle
            tvLocation.text = location
            tvComment.text = comment
            tvAmount.text = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
                .format(amount)
            switchCompleted.isChecked = isCompleted
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.order_details)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                listener?.onStatusChanged(
                    orderId,
                    binding.switchCompleted.isChecked
                )
                dismiss()
            }
            .setNegativeButton(R.string.close, null)
            .create()
    }

    fun setOnStatusChangedListener(listener: OnStatusChangedListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}