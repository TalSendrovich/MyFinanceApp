package com.example.myfinanceapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.MyList
import com.example.myfinanceapp.ui.home.homefragments.CreateListFragment

class ListsAdapter(val context : Context, private  val viewModel: MainViewModel,
                   private val myLists: ArrayList<MyList>) :
                   RecyclerView.Adapter<ListsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tvTitle.text = myLists[position].name
        val singleStockList = myLists[position].stocks
        val sizeOfList = myLists[position].stocks.size
        holder.tvNumSymbols.text =
            if (sizeOfList == 1)
                "1 symbol"
            else
                "$sizeOfList symbols"

        setEditList(holder)

        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        val listStocksAdapter = SingleListAdapter(context, singleStockList)
        holder.rvUserLists.adapter = listStocksAdapter
        holder.rvUserLists.layoutManager = layoutManager
    }

    private fun setEditList(holder: ListViewHolder) {
        holder.tvEditUserList.setOnClickListener {
            val createListFragment = CreateListFragment()
            val bundle = Bundle()
            bundle.putBoolean("Editing", true)
            createListFragment.arguments = bundle

            viewModel.setName(holder.tvTitle.text.toString())

            val activity: AppCompatActivity = it.context as AppCompatActivity
            (activity as MainActivity).setCurrentFragment(createListFragment)
        }
    }

    override fun getItemCount(): Int {
        return myLists.size
    }


    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {
        var tvTitle: TextView = itemView.findViewById<TextView>(R.id.tvCardTitle)
        var tvNumSymbols = itemView.findViewById<TextView>(R.id.tvNumSymbols)
        var rvUserLists: RecyclerView = itemView.findViewById<RecyclerView>(R.id.rvList)
        var tvEditUserList : TextView = itemView.findViewById(R.id.tvEditUserList)
    }

}