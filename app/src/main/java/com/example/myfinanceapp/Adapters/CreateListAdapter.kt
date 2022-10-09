package com.example.myfinanceapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.SimpleStock
import com.example.myfinanceapp.data.Stock
import java.util.ArrayList

class CreateListAdapter(context: Context, var viewModel: MainViewModel, var stockList: ArrayList<SimpleStock>) :
    RecyclerView.Adapter<CreateListAdapter.CreateListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateListAdapter.CreateListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stock_item, parent, false)
        return CreateListViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CreateListViewHolder, position: Int) {
        val symbol = stockList[position].symbol
        holder.tvSymbol.text = symbol
        holder.tvStockNameItem.text = stockList[position].name
        holder.cbToList.isChecked = true

        holder.cbToList.setOnClickListener {
            viewModel.deleteStockInUserListInCreation(symbol)
            stockList = viewModel.getUserListInCreation()
            this.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return stockList.size
    }
    inner class CreateListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvSymbol: TextView = itemView.findViewById(R.id.tvSingleStockSymbol)
        var tvStockNameItem : TextView = itemView.findViewById(R.id.tvSingleStockName)
        var cbToList : CheckBox = itemView.findViewById(R.id.cbToList)

    }
}