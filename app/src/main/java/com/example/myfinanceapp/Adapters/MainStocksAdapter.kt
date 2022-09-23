package com.example.myfinanceapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.R
import org.jetbrains.annotations.NotNull
import androidx.fragment.app.Fragment
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.ui.home.homefragments.StockInfo


class MainStocksAdapter(var context: Context, var stocks: Array<String>, private val indexesPrices: Array<String>,
                        val indexesPercentage: Array<String>) :
    RecyclerView.Adapter<MainStocksAdapter.StocksViewHolder>() {
    private val indexesSymbols = arrayOf("SPX", "dji", "IXIC", "rut")
    private val apikey : String = "73990e67670a472396934b1757bfb135"


    @NotNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.my_column, parent,false )
        return StocksViewHolder(view)
    }
//
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        // Setting the views in the col_layout with the appropriate text
        holder.itemKode.text = stocks[position]
        // TODO change the color here to dynamic color
        // TODO means if the stock is up - green, if stock is doewn so red
        holder.tvPoints.text = indexesPrices[position]
        val percentageChange = indexesPercentage[position]
        println(percentageChange)
        if (percentageChange.toFloat() >= 0)
            holder.text_back_ground.setBackgroundResource(R.drawable.green_rounded_corners)
        else
            holder.text_back_ground.setBackgroundResource(R.drawable.red_rounded_corners)

        holder.tvPercentageChange.text = "$percentageChange% "

        // TODO make navigation work after pressing this
        // TODO set all the views in the layout according to the data base

        setIndexesClickable(holder, position)

    }

    /**
     * Make a single index clickable
     * Means when clicking it moving to the stock info page
     */
    private fun setIndexesClickable(holder: MainStocksAdapter.StocksViewHolder, position : Int) {
        holder.stock_layout.setOnClickListener(View.OnClickListener {
            val i = Bundle() // The object the holds the info passed the the stock_layout
            i.putString("symbol", indexesSymbols[position])
            i.putString("name", stocks[position])

            // Creating the mechanism to replace the home layout with the stock layout
            val activity: AppCompatActivity = it.context as AppCompatActivity

            //  Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment = StockInfo()
            myFragment.arguments = i

            (activity as MainActivity).setCurrentFragment(myFragment)
        })
    }


    override fun getItemCount(): Int {
        return stocks.size;
    }

    // The purpose for these class in to initialize the views
    inner class StocksViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var itemKode: TextView = itemView.findViewById(R.id.tv_stock)
        var text_back_ground: TextView = itemView.findViewById(R.id.tv_back_ground)
        var tvPoints : TextView = itemView.findViewById(R.id.tv_total_points)
        var tvPercentageChange : TextView = itemView.findViewById(R.id.tv_points_change)
        var stock_layout: ConstraintLayout = itemView.findViewById(R.id.layout_col)

    }
}
