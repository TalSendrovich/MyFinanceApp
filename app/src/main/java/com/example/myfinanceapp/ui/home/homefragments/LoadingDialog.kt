package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.myfinanceapp.R
import com.example.myfinanceapp.ui.home.HomeFragment

class LoadingDialog(private val activity: Activity) {
    lateinit var dialog : AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun endDialog() {
        dialog.dismiss()
    }
}