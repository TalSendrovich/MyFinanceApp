package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.apidata.quote.Quote
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private var  tvPrice = findViewById<TextView>(R.id.tvPrice)
    private var  tvTime = findViewById<TextView>(R.id.tvTime)

// callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @SuppressLint("SetTextI18n")
    override fun refreshContent(e:Entry, highlight : Highlight) {
        tvPrice.text = "" + e.y
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }


    private  lateinit var mOffset : MPPointF

    override fun getOffset() : MPPointF {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(getWidth() / 2)).toFloat(), (-getHeight()).toFloat())


        return mOffset;
    }
}