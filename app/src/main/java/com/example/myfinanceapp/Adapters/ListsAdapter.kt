package com.example.myfinanceapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.MyList

class ListsAdapter(val context : Context, private val myLists: ArrayList<MyList>) : RecyclerView.Adapter<ListsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tvTitle.text = myLists[position].name
        //TODO add num symbols


    }

    override fun getItemCount(): Int {
        return myLists.size
    }


    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {
        var tvTitle = itemView.findViewById<TextView>(R.id.tvCardTitle)
        var tvNumSymbols = itemView.findViewById<TextView>(R.id.tvNumSymbols)
    }

}