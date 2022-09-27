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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.api.ApiManager
import com.example.myfinanceapp.data.SimpleStock
import com.example.myfinanceapp.ui.home.homefragments.StockInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleListAdapter(val context : Context, private val singleStockList: List<SimpleStock>) : RecyclerView.Adapter<SingleListAdapter.ListViewHolder>() {
    private val apiManager = ApiManager()
    private val apikey: String = "73990e67670a472396934b1757bfb135"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val symbol = singleStockList[position].symbol
        val name = singleStockList[position].name
        holder.tvStockName.text = name
        holder.tvStockSymbol.text = symbol

        getStockData(holder, symbol)
        setStockClickable(holder, symbol, name)
    }

    private fun setStockClickable(holder: ListViewHolder, symbol: String, name: String) {
        holder.listItem.setOnClickListener{
            val i = Bundle() // The object the holds the info passed the the stock_layout
            i.putString("symbol", symbol)
            i.putString("name", name)

            // Creating the mechanism to replace the home layout with the stock layout
            val activity: AppCompatActivity = it.context as AppCompatActivity

            //  Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment = StockInfo()
            myFragment.arguments = i

            (activity as MainActivity).setCurrentFragment(myFragment)
        }
    }

    /**
     * Get live data to a single stock in a list
     */
    @SuppressLint("SetTextI18n")
    private fun getStockData(holder: ListViewHolder, symbol : String) {
        CoroutineScope(IO).launch {
            val price = apiManager.getPrice(symbol, apikey)
            val quote = apiManager.getQuote(symbol, apikey)
            val percentageChange = apiManager.calculatePercentageChange(price, quote.previous_close)

            withContext(Main) {
                holder.tvCurrentPrice.text = price
                holder.tvPercentageChange.text = "$percentageChange%"
                if (percentageChange.toFloat() >= 0)
                    holder.tvListPercentageChangeBackGround.setBackgroundResource(R.drawable.green_rounded_corners)
                else
                    holder.tvListPercentageChangeBackGround.setBackgroundResource(R.drawable.red_rounded_corners)
            }
        }
    }

    override fun getItemCount(): Int {
        return singleStockList.size
    }


    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {
        var tvStockSymbol = itemView.findViewById<TextView>(R.id.tvSingleStockSymbol)
        var tvStockName = itemView.findViewById<TextView>(R.id.tvSingleStockName)
        var tvCurrentPrice = itemView.findViewById<TextView>(R.id.tvListCurrentPrice)
        var listItem = itemView.findViewById<ConstraintLayout>(R.id.clListItem)
        var tvPercentageChange = itemView.findViewById<TextView>(R.id.tvListPercentageChange)
        var tvListPercentageChangeBackGround = itemView.findViewById<TextView>(R.id.tvListPercentageChangeBackGround)
    }

}
