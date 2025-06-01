package com.example.lumos.presentation.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showConfirmationDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    positiveAction: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.yes) { _, _ -> positiveAction() }
        .setNegativeButton(android.R.string.no, null)
        .show()
}

fun validateInput(input: String): Boolean = input.isNotBlank()

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }