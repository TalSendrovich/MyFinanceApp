package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.Adapters.CreateListAdapter
import com.example.myfinanceapp.Adapters.SearchViewAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.data.SimpleStock
import com.example.myfinanceapp.data.Stock
import com.example.myfinanceapp.databinding.FragmentSearchViewBinding
import com.example.myfinanceapp.ui.home.HomeViewModel
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class SearchViewFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var stockList: ArrayList<Stock>
    lateinit var createStockList: ArrayList<SimpleStock>
    lateinit var searchViewAdapter: SearchViewAdapter
    lateinit var db: FirebaseFirestore
    private lateinit var bundle: Bundle
    private var _binding: FragmentSearchViewBinding? = null
    private var TAG = "SearchViewFragment"
    private val viewModel : MainViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bundle = this.requireArguments()
        setRecyclerView(bundle.getBoolean("fromHomePage"))
        binding.svStocks.onActionViewExpanded() // Doesn't work
        binding.svStocks.requestFocus() // Doesn't work
        loadRecentSearches()
        setSearchViewQueries()

        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecentSearches() {
        CoroutineScope(Dispatchers.IO).launch {
            val email = (activity as MainActivity).accountEmail
            val recentCollectionRef = db.collection("users/$email/recent search")
            Log.d(TAG, "Inside loadRecentSearches")
            val querySnapshot =
                recentCollectionRef.orderBy("date", Query.Direction.DESCENDING).get().await()

            for (doc in querySnapshot?.documents!!) {
                doc.toObject(Stock::class.java)?.let {
                    stockList.add(it)
                }
            }
            withContext(Dispatchers.Main) {
                searchViewAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun setSearchViewQueries() {
        binding.svStocks.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.svStocks.clearFocus()
                queryTextChange(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                queryTextChange(query)
                return true
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun queryTextChange(query: String?) {
        stockList.clear()
        searchViewAdapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.IO).launch {
            val fixedQuery = query?.capitalizeWords()

            val querySnapshot = fixedQuery?.let {
                db.collection("stocks").whereGreaterThanOrEqualTo("name", it)
                    .whereLessThanOrEqualTo("name", it + "\uF7FF")
                    .orderBy("name")
                    .limit(5)
                    .get()
                    .await()
            }

            for (doc in querySnapshot?.documents!!) {
                doc.toObject(Stock::class.java)?.let {
                    stockList.add(it)
                }
            }
            withContext(Dispatchers.Main) {
                searchViewAdapter.notifyDataSetChanged()
            }
        }
    }


    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
        it.replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase(Locale.getDefault())
            else it.toString()
        }
    }


    private fun setRecyclerView(fromHomePage: Boolean) {
        db = FirebaseFirestore.getInstance()
        recyclerView = binding.rvStockList
        recyclerView.setHasFixedSize(true)
        stockList = ArrayList()
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchViewAdapter = SearchViewAdapter(requireContext(), viewModel ,stockList, fromHomePage)
        recyclerView.adapter = searchViewAdapter
    }

    private fun getCreatedStockList(createStockList: ArrayList<SimpleStock>) {
        val symbolsArray = bundle.getStringArrayList("symbols")!!
        val namesArray = bundle.getStringArrayList("names")!!
        for (i in 0 until symbolsArray.size)
            createStockList.add(SimpleStock(symbolsArray[i], namesArray[i]))
    }
}

