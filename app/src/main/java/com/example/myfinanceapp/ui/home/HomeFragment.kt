package com.example.myfinanceapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.databinding.FragmentHomeBinding


open class HomeFragment : Fragment() {

    lateinit var stocks: Array<String>

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

       recyclerViewCreator(root)

        if (container != null) {
            container.removeAllViews();
        }
        container?.clearDisappearingChildren()

        val myFragment = StockInfo()
        binding.button.setOnClickListener {
            val activity: AppCompatActivity = it.context as AppCompatActivity
            (getActivity() as MainActivity?)?.setCurrentFragment(myFragment)
        }
        return root
    }



    // Initialize and creates the recycler view
    private fun recyclerViewCreator(root: View) {
        stocks = resources.getStringArray(R.array.main_stocks)
        val stockAdapter = MainStocksAdapter(requireContext(), stocks)
        binding.rvMainStocks.adapter = stockAdapter
        var layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainStocks.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}