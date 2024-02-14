package com.example.weatherapp.baseActivity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var progressDialog : ProgressDialog? = null

    fun showLoadingDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.show()
    }

    fun hideLoading() {
        progressDialog?.dismiss()
    }

    var alertDialog : android.app.AlertDialog? = null

    fun showDialog(
        context: Context,
        message : String,
        posActionTitle : String? = null,
        posAction : DialogInterface.OnClickListener? = null,
        negActionTitle : String? = null,
        negAction : DialogInterface.OnClickListener? = null,
        cancelable : Boolean = true

    ) {
        var messageDialogBuilder = android.app.AlertDialog.Builder(context)
        messageDialogBuilder.setMessage(message)

        if (posActionTitle != null) {
            messageDialogBuilder.setPositiveButton(
                posActionTitle,
                posAction?: DialogInterface.OnClickListener{ dialog, p1 -> dialog?.dismiss() }
            )
        }

        if (negActionTitle != null) {
            messageDialogBuilder.setNegativeButton(
                negActionTitle,
                negAction?: DialogInterface.OnClickListener{ dialog, i -> dialog?.dismiss()  }
            )
        }

        messageDialogBuilder.setCancelable(cancelable)
        alertDialog = messageDialogBuilder.show()
    }
}