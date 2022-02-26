package com.example.myfinanceapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.R
import com.example.myfinanceapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var stocks: Array<String>

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView: View = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = rootView.findViewById(R.id.mainStocks)
        stocks = resources.getStringArray(R.array.mainStocks)
        val stockAdapter = MainStocksAdapter(this, stocks)
        recyclerView.adapter = stockAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}