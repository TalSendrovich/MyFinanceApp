package com.example.myfinanceapp.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapp.Adapters.ListsAdapter
import com.example.myfinanceapp.Adapters.MainStocksAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.R
import com.example.myfinanceapp.api.StockData
import com.example.myfinanceapp.data.MyList
import com.example.myfinanceapp.databinding.FragmentHomeBinding
import com.example.myfinanceapp.ui.home.homefragments.CreateListFragment
import com.example.myfinanceapp.ui.home.homefragments.SearchViewFragment
import com.example.myfinanceapp.ui.home.homefragments.UserLists
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
    private lateinit var userListsAdaper: ListsAdapter
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val usersCollectionRef = Firebase.firestore.collection("users")

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


        mainIndexesRecyclerViewCreator(root)
        listsRecyclerViewCreator(root)
        userListManagerCreator(root)
        searchViewCreator()

        return root
    }

    /**
     * The function in charge of creating the user list menu
     */
    private fun userListManagerCreator(root: View) {
        val ibUserListManger = binding.ibUserListManager

        ibUserListManger.setOnClickListener {
            val puListManagementMenu = PopupMenu(context, ibUserListManger)
            puListManagementMenu.inflate(R.menu.user_list_managment_popup_menu)
            puListManagementMenu.show()

            puListManagementMenu.setOnMenuItemClickListener {
                val id = it.itemId
                if (id == R.id.puCreateList)
                    moveToCreateListFragment()
                else if (id == R.id.puDeleteList)
                    createDeleteMenu()

                true
            }

        }
    }

    /**
     * Delete menu builder
     */
    private fun createDeleteMenu() {
        CoroutineScope(IO).launch {
            val listsNames = getListsNames()

            withContext(Main) {
                var chosenList = listsNames[0]
                AlertDialog.Builder(this@HomeFragment.context)
                    .setTitle("Choose the list to delete")
                    .setSingleChoiceItems(listsNames, 0) { _ , i ->
                        chosenList = listsNames[i]
                    }
                    .setPositiveButton("Accept") { _, _ ->
                        CoroutineScope(IO).launch {
                            deleteList(listsNames, chosenList)
                        }
                    }
                    .create()
                    .show()
            }
        }
    }


    /**
     * Delete the list from storage in the cloud
     * Updates the internal storage
     * Updates the recycler view
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun deleteList(listsNames: Array<out String>, listToDelete: String) {
        usersCollectionRef.document(email)
            .collection("stock lists")
            .document(listToDelete).delete().await()

        deleteStockFromInternalList(listToDelete)
        withContext(Main) {
            userListsAdaper.notifyDataSetChanged()
            Toast.makeText(
                this@HomeFragment.context,
                "Deleted $listToDelete list",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    /**
     * Delete a specific list from internal storage
     * Means it deletes the list from the arrayList in this fragment
     */
    private fun deleteStockFromInternalList(listToDelete: String) {
        for (i in 0 until listStocks.size) {
            if (listStocks[i].name == listToDelete)
                listStocks.removeAt(i)
            break
        }
    }

    private suspend fun getListsNames(): Array<out String> {
        val listsNames = arrayListOf<String>()
        val stockLists = usersCollectionRef.document(email)
            .collection("stock lists")
            .get().await()

        for (doc in stockLists.documents) {
            listsNames.add(doc.get("name") as String)
        }
        return listsNames.toTypedArray()
    }


    private fun moveToCreateListFragment() {
        val createListFragment = CreateListFragment()
        val bundle = Bundle()
        bundle.putBoolean("fromHomePage", true)
        bundle.putBoolean("Editing", false)
        createListFragment.arguments = bundle
        (activity as MainActivity).setCurrentFragment(createListFragment)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun listsRecyclerViewCreator(root: View) {
        val userLists = UserLists()
        email = (activity as MainActivity).accountEmail //Check!!
        CoroutineScope(IO).launch() {
            userLists.getUserLists(email, listStocks)
            withContext(Main) {
                userListsAdaper = ListsAdapter(requireContext(), sharedViewModel, listStocks)
                binding.rvLists.adapter = userListsAdaper
                val layoutManager = LinearLayoutManager(root.context)
                binding.rvLists.layoutManager = layoutManager
                userListsAdaper.notifyDataSetChanged()
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
            val bundle = Bundle()
            bundle.putBoolean("fromHomePage", true)
            searchFragment.arguments = bundle
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
    private fun mainIndexesRecyclerViewCreator(root: View) {
        val indexesPrice = arrayOf("0", "0", "0", "0")
        val indexesPercentage = arrayOf("0", "0", "0", "0")

        mainIndexes = resources.getStringArray(R.array.main_indexes)
        mainStocksAdapter =
            MainStocksAdapter(requireContext(), mainIndexes, indexesPrice, indexesPercentage)
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

    /**
     * Get the open prices of all the main indexes
     * Calculates the percentage of daily change
     */
    @SuppressLint("NotifyDataSetChanged")
    private suspend fun getIndexesCurrentPercentage(
        indexesPrice: Array<String>,
        indexesPercentage: Array<String>,
        progressBar: AlertDialog
    ) {
        val arrSymbols = arrayOf("SPX", "dji", "IXIC", "rut")
        var index = 0
        for (i in arrSymbols) {
            val priceJob = StockData.api.getQuote(i, apikey)

            if (priceJob.isSuccessful && priceJob.body() != null) {
                val openPrice = priceJob.body()!!.open.toFloat()
                val percentage =
                    "%.2f".format(((indexesPrice[index].toFloat() * 100) / openPrice) - 100)
                indexesPercentage[index] = percentage
            }
            index += 1
        }
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
