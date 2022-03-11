package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.myfinanceapp.R

class LoadingDialog(act: Activity) {
    var activity = act
    lateinit var dialog : AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun endDialog() {
        dialog.dismiss()
    }
}