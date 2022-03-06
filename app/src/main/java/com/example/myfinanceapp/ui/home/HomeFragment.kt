package com.example.myfinanceapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.Stock
import com.example.myfinanceapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.Exception


open class HomeFragment : Fragment() {

    lateinit var stocks: Array<String>

    private val db = Firebase.firestore.collection("stocks")
    private lateinit var mAuth: FirebaseAuth
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
        mAuth = FirebaseAuth.getInstance()

        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

       recyclerViewCreator(root)


//        binding.button.setOnClickListener {
//            val inputStream = requireActivity().assets.open("stocks.txt")
//            val size = inputStream.available()
//            val buffer = ByteArray(size)
//            inputStream.read(buffer)
//
//            val text = String(buffer)
//            val list = text.lines().toMutableList()
//            Log.d("Here", "HERE")
//
//            for (line in list) {
//                val a = line.split(",").toMutableList()
//                val stock = Stock(a[0], a[1], a[2], a[3],a[4],a[5])
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        db.document(a[0]).set(stock).await()
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(
//                                this@HomeFragment.context,
//                                "Successfully Saved Data!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } catch (e: Exception) {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(this@HomeFragment.context, e.message, Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//                }
//            }
//           // val listoflist = list.forEach{it.split(",")
//            Log.d("Here", "HERE")
//
//            inputStream.close()
//        }
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