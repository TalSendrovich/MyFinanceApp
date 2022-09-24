package com.example.myfinanceapp.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapp.Adapters.ListsAdapter
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.api.StockData
import com.example.myfinanceapp.data.MyList
import com.example.myfinanceapp.databinding.FragmentHomeBinding
import com.example.myfinanceapp.ui.home.homefragments.LoadingDialog
import com.example.myfinanceapp.ui.home.homefragments.SearchViewFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await


open class HomeFragment : Fragment() {

    lateinit var mainIndexes: Array<String>
    var listStocks: ArrayList<MyList> = arrayListOf()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainStocksAdapter: MainStocksAdapter
    private lateinit var loadingDialog : LoadingDialog
    private var _binding: FragmentHomeBinding? = null
    private val apikey: String = "73990e67670a472396934b1757bfb135"

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
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mainStocksRecyclerViewCreator(root)
        listsRecyclerViewCreator(root)
        userListManagerCreator(root)
        searchViewCreator()

        return root
    }

    private fun userListManagerCreator(root: View) {
        val ibUserListManger = binding.ibUserListManager

        ibUserListManger.setOnClickListener {
            val puListManagementMenu = PopupMenu(context, ibUserListManger)
            puListManagementMenu.inflate(R.menu.user_list_managment_popup_menu)
            puListManagementMenu.show()
        }
    }

    private fun listsRecyclerViewCreator(root: View) {
        listStocks.clear()
        email = (activity as MainActivity).accountEmail //Check!!
        val listsCollectionRef = Firebase.firestore.collection("users/$email/stock lists")

        CoroutineScope(IO).async {
            val querySnapshot = listsCollectionRef.get().await()
            Log.d("HomeFragment", "Inside listsRecyclerViewCreator")
            for (doc in querySnapshot.documents) {
                doc.toObject(MyList::class.java)?.let {
                    listStocks.add(it)
                    Log.d("HomeFragment", "$it")
                }
            }

            withContext(Main) {
                val listStocksAdapter = ListsAdapter(requireContext(), listStocks)
                binding.rvLists.adapter = listStocksAdapter
                val layoutManager = LinearLayoutManager(root.context)
                binding.rvLists.layoutManager = layoutManager
            }
        }
    }

    /**
     * Making the functionality of touching the search view
     * When touching it, moving to the search fragment
     */
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


    /**
     * Initialize and creates the recycler view of main indexes
     * S&P500, Nasdaq, etc
     */

    @SuppressLint("NotifyDataSetChanged")
    private fun mainStocksRecyclerViewCreator(root: View) {
        var indexesPrice = arrayOf("0", "0", "0", "0")
        var indexesPercentage = arrayOf("0", "0", "0", "0")

        mainIndexes = resources.getStringArray(R.array.main_indexes)
        mainStocksAdapter = MainStocksAdapter(requireContext(), mainIndexes, indexesPrice, indexesPercentage)
        binding.rvMainIndexes.adapter = mainStocksAdapter
        val layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainIndexes.layoutManager = layoutManager

        updateIndexesInfo(indexesPrice, indexesPercentage)
    }

    /**
     * In charge of calling 2 functions
     * Each function takes current data from the Internet
     * Eventually the RecyclerView is updated
     */
    private fun updateIndexesInfo(indexesPrice: Array<String>, indexesPercentage: Array<String>) {
        val progressBar = AlertDialog.Builder(this.context)
            .setView(R.layout.custom_dialog)
            .setCancelable(true)
            .create()
        progressBar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressBar.show()

        CoroutineScope(IO).launch {
            getIndexesCurrentPrice(indexesPrice)
            getIndexesCurrentPercentage(indexesPrice, indexesPercentage, progressBar)
            progressBar.dismiss()
        }

    }

    private fun initializeLoadingDialog() {

    }

    /**
     * Get the open prices of all the main indexes
     * Calculates the percentage of daily change
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getIndexesCurrentPercentage(indexesPrice: Array<String>,
                                                    indexesPercentage: Array<String>,
                                                    progressBar : AlertDialog) {
        val arrSymbols = arrayOf("SPX", "dji", "IXIC", "rut")
        var index = 0
        for (i in arrSymbols) {
            val priceJob = StockData.api.getQuote(i, apikey)

            if (priceJob.isSuccessful && priceJob.body() != null) {
                val openPrice = priceJob.body()!!.open.toFloat()
                val percentage = "%.2f".format(((indexesPrice[index].toFloat() * 100) / openPrice) - 100)
                indexesPercentage[index] = percentage
            }
            index += 1
        }
        progressBar.dismiss()
        updateMainIndexesRecyclerview()
    }

    /**
     * Get the current prices of all the main indexes
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getIndexesCurrentPrice(indexesPrice: Array<String>) {
        val arrSymbols = arrayOf("SPX", "dji", "IXIC", "rut")
        var index = 0
        for (i in arrSymbols) {
            val priceJob = StockData.api.getPrice(i, apikey)

            if (priceJob.isSuccessful && priceJob.body() != null) {
                val price = priceJob.body()!!.price
                indexesPrice[index] = price
            }
            index += 1
        }
        updateMainIndexesRecyclerview()
    }

    /**
     * Updates the recycler view
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun updateMainIndexesRecyclerview() {
        withContext(Main) {
            mainStocksAdapter.notifyDataSetChanged()
        }
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
