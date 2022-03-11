package com.example.myfinanceapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.Stock
import com.example.myfinanceapp.ui.home.homefragments.StockInfo
import java.util.ArrayList


class SearchViewAdapter(context: Context, stockList : ArrayList<Stock>) : RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>() {
    var context = context
    var stockList = stockList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val stock = stockList[position]
        holder.tvSymbol.text = stock.symbol
        holder.tvStockNameItem.text = stock.name

        holder.clStockItem.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("symbol", stockList[position].symbol)
            bundle.putString("name", stockList[position].name)

            // Creating the mechanism to replace the home layout with the stock layout
            val activity: AppCompatActivity = it.context as AppCompatActivity

            //  Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment = StockInfo()
            myFragment.arguments = bundle

            (activity as MainActivity).setCurrentFragment(myFragment)
        }

    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvSymbol: TextView = itemView.findViewById(R.id.tvSymbol)
        var tvStockNameItem : TextView = itemView.findViewById(R.id.tvStockNameItem)
        var cbToList : CheckBox = itemView.findViewById(R.id.cbToList)
        val clStockItem : ConstraintLayout = itemView.findViewById(R.id.clStockItem)

    }
}