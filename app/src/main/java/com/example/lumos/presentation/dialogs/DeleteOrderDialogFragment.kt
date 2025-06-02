package com.example.lumos.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteOrderDialogFragment : DialogFragment() {
    var onDeleteConfirmedListener: ((orderId: Int) -> Unit)? = null
    private var orderId: Int = -1

    fun setOrderId(id: Int) {
        orderId = id
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Удаление заказа")
            .setMessage("Вы уверены, что хотите удалить этот заказ?")
            .setPositiveButton("Удалить") { _, _ ->
                onDeleteConfirmedListener?.invoke(orderId)
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
}