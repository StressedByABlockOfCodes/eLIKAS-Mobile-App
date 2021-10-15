package com.example.elikas.ui.custom

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.example.elikas.R

class LoadingDialog constructor(private val activity: Activity) {
    private lateinit var dialog: Dialog

    fun showDialog() {
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.Widget_LoadingDialog_Base)
        val mView: View = LayoutInflater.from(activity).inflate(R.layout.loading_dialog, null)
        mBuilder.setView(mView)
        dialog = mBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun close() {
        dialog.dismiss()
    }
}