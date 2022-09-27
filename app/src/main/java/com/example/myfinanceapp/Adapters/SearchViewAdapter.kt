package com.example.myfinanceapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.Stock
import com.example.myfinanceapp.ui.home.HomeViewModel
import com.example.myfinanceapp.ui.home.homefragments.CreateListFragment
import com.example.myfinanceapp.ui.home.homefragments.StockInfo
import java.util.ArrayList


class SearchViewAdapter(var context: Context, var viewModel : MainViewModel,
                        var stockList: ArrayList<Stock>, val fromHomePage: Boolean)
    : RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.cbToList.visibility = View.INVISIBLE
        val stock = stockList[position]
        holder.tvSymbol.text = stock.symbol
        holder.tvStockNameItem.text = stock.name

        /**
         * Set the ability to click on the search view items
         * Move to the stock info page when clicked
         */
        holder.clStockItem.setOnClickListener {
            val bundle = Bundle()
            val symbol = stockList[position].symbol
            val name = stockList[position].name
            bundle.putString("symbol", symbol)
            bundle.putString("name", name)

            // Creating the mechanism to replace the home layout with the stock layout
            val activity: AppCompatActivity = it.context as AppCompatActivity

            //  Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment
            if (fromHomePage)
                myFragment = StockInfo()
            else {
                myFragment = CreateListFragment()
                viewModel.addStockToCreateUserList(symbol, name)
                bundle.putBoolean("fromHomePage", false)
            }

            myFragment.arguments = bundle

            (activity as MainActivity).setCurrentFragment(myFragment)
        }

    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvSymbol: TextView = itemView.findViewById(R.id.tvSingleStockSymbol)
        var tvStockNameItem : TextView = itemView.findViewById(R.id.tvSingleStockName)
        var cbToList : CheckBox = itemView.findViewById(R.id.cbToList)
        val clStockItem : ConstraintLayout = itemView.findViewById(R.id.clStockItem)

    }
}