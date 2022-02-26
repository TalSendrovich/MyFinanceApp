package com.example.myfinanceapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.R
import com.example.myfinanceapp.ui.home.HomeFragment
import org.jetbrains.annotations.NotNull

class MainStocksAdapter(cl: HomeFragment, s1: Array<String>) :
    RecyclerView.Adapter<MainStocksAdapter.StocksViewHolder>() {
    lateinit var stocks: Array<String>
    var context: HomeFragment? = null

    init {
        stocks = s1;
        context = cl;
    }
    @NotNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View = inflater.inflate(R.layout.my_column, parent,false )
        return StocksViewHolder(view)
    }

    override fun onBindViewHolder(holder: StocksViewHolder, position: Int) {
        holder.textView.setText(stocks[position])
    }

    override fun getItemCount(): Int {
        return stocks.size;
    }

    inner class StocksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.stock)
        }
    }
