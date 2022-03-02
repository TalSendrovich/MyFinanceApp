package com.example.myfinanceapp.Adapters

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
import com.example.myfinanceapp.ui.home.StockInfo
import org.jetbrains.annotations.NotNull
import androidx.fragment.app.Fragment


class MainStocksAdapter(cl: Context, s1: Array<String>) :
    RecyclerView.Adapter<MainStocksAdapter.StocksViewHolder>() {
    var stocks: Array<String> = s1
    var context: Context
    lateinit var p: ViewGroup

    init {
        context = cl;
    }
    @NotNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        p = parent
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = inflater.inflate(R.layout.my_column, parent,false )
        return StocksViewHolder(view)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        // Setting the views in the col_layout with the appropriate text
        holder.itemKode.text = stocks[position]
        // TODO change the color here to dynamic color
        // TODO means if the stock is up - green, if stock is doewn so red
        holder.text_back_ground.setBackgroundResource(R.drawable.green_rounded_coreners)

        // TODO make navigation work after pressing this
        // TODO set all the views in the layout according to the data base
        holder.stock_layout.setOnClickListener(View.OnClickListener {
            val i = Bundle() // The object the holds the info passed the the stock_layout
            i.putString("stock", stocks[position])
            // Creating the mechanism to replace the home layout with the stock layout
            val activity: AppCompatActivity = p.context as AppCompatActivity
            // Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment = StockInfo()
            myFragment.arguments = i
            // Actually activates the stock fragment
            activity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, myFragment,"OptionsFragment")
                addToBackStack(null)
                commit()
            }
        })

    }

    override fun getItemCount(): Int {
        return stocks.size;
    }

    // The purpose for these class in to initialize the views
    inner class StocksViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var itemKode: TextView
        var text_back_ground: TextView
        var stock_layout: ConstraintLayout

        init {
            itemKode = itemView.findViewById(R.id.tv_stock)
            text_back_ground = itemView.findViewById(R.id.tv_back_ground)
            stock_layout = itemView.findViewById(R.id.layout_col)
        }
        }
    }
