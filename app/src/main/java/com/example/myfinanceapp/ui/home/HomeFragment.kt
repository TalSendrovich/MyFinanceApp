package com.example.myfinanceapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.myfinanceapp.Adapters.ListsAdapter
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.MyList
import com.example.myfinanceapp.data.SimpleStock
import com.example.myfinanceapp.databinding.FragmentHomeBinding
import com.example.myfinanceapp.ui.home.homefragments.SearchViewFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


open class HomeFragment : Fragment() {

    lateinit var mainStocks: Array<String>
    var listStocks : ArrayList<MyList> = arrayListOf()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var email : String
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

        email = (activity as MainActivity).accountEmail //Check!!
        var listsCollectionRef = Firebase.firestore.collection("users/$email/stock lists")

        mainStocksRecyclerViewCreator(root)
        listsRecyclerViewCreator(root, listsCollectionRef)
        searchViewCreator()

        return root
    }

    private fun listsRecyclerViewCreator(root: View, listsCollectionRef : CollectionReference) {
         CoroutineScope(Dispatchers.IO).async {
             val querySnapshot = listsCollectionRef.get().await()
             Log.d("HomeFragment", "Inside listsRecyclerViewCreator")
             for (doc in querySnapshot.documents) {
                 doc.toObject(MyList::class.java)?.let {
                     listStocks.add(it)
                     Log.d("HomeFragment", "$it")
                 }
             }

             //Log.d("HomeFragment", "$listStocks")

             withContext(Dispatchers.Main) {
                 val listStocksAdapter = ListsAdapter(requireContext(), listStocks)
                 binding.rvLists.adapter = listStocksAdapter
                 val layoutManager = LinearLayoutManager(root.context)
                 binding.rvLists.layoutManager = layoutManager
             }
         }
    }

    private fun searchViewCreator() {
        binding.searchView.setOnClickListener {
            val searchFragment = SearchViewFragment()
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.nav_host_fragment_activity_main, searchFragment, "OptionsFragment")
                addToBackStack(null)
                commit()
            }
        }
    }


    // Initialize and creates the recycler view
    private fun mainStocksRecyclerViewCreator(root: View) {
        mainStocks = resources.getStringArray(R.array.main_stocks)
        val mainStocksAdapter = MainStocksAdapter(requireContext(), mainStocks)
        binding.rvMainStocks.adapter = mainStocksAdapter
        val layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainStocks.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**----------------------------------------------------------------------------- */

//binding.button.setOnClickListener {
//    val inputStream = requireActivity().assets.open("NyseCompanies.txt")
//    val size = inputStream.available()
//    val buffer = ByteArray(size)
//    inputStream.read(buffer)
//
//    val text = String(buffer)
//    val list = text.lines().toMutableList()
//    Log.d("Here", "HERE")
//
//    for (line in list) {
//        val a = line.split(",").toMutableList()
//        val stock = Stock(a[0], a[1], a[2], a[3],a[4],a[5])
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                db.document(a[0]).set(stock).await()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        this@HomeFragment.context,
//                        "Successfully Saved Data!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@HomeFragment.context, e.message, Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }
//    Log.d("Here", "HERE")
//
//    inputStream.close()
//}
