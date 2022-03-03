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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


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
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = inflater.inflate(R.layout.my_column, parent,false )
        return StocksViewHolder(view)
    }
//
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
            val activity: AppCompatActivity = it.context as AppCompatActivity
           //  Initializing the container fragment and set the info the I want to pass
            val myFragment: Fragment = StockInfo()
            myFragment.arguments = i

          //Navigation.findNavController(it).navigate(R.id.navigateToStockInfoFragment)

            // Actually activates the stock fragment

//            val navHostFragment =
//                activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//            val navController = navHostFragment.navController
//            navController.navigate(R.id.fragment_stocks)

            activity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_host_fragment_activity_main, myFragment ,"OptionsFragment")
                addToBackStack(null)
                commit()
            }
        })
}
////
//
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
