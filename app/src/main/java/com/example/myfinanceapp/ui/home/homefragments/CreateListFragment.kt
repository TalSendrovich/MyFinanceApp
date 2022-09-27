package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapp.Adapters.CreateListAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.MainViewModel
import com.example.myfinanceapp.data.MyList
import com.example.myfinanceapp.data.SimpleStock
import com.example.myfinanceapp.databinding.FragmentCreateListBinding
import com.example.myfinanceapp.ui.home.HomeFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CreateListFragment : Fragment() {

    private var _binding: FragmentCreateListBinding? = null
    lateinit var stockList : ArrayList<SimpleStock>
    lateinit var bundle: Bundle
    private val sharedViewModel : MainViewModel by activityViewModels()
    private lateinit var createListAdapter : CreateListAdapter
    private val usersCollectionRef = Firebase.firestore.collection("users")


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateListBinding.inflate(inflater, container, false)
        val root = binding.root
        bundle = requireArguments()

        getListName()
        setStockList()
        setUserListRecyclerView(root)
        setSearchView()
        setCreateListButton()

        return root
    }

    /**
     * Only relevant when pressing "EDIT" from home page
     * Take from firebase the specified user list that the user want to edit
     * Update stock list inner storage
     */
    private suspend fun getEditedList() {
        if (bundle.getBoolean("Editing")) {
            val userEmail = (activity as MainActivity).accountEmail
            val listName = sharedViewModel.getNameEdit()

            val userListArray = usersCollectionRef.document(userEmail)
                .collection("stock lists")
                .document(listName)
                .get()
                .await()

            userListArray.toObject(MyList::class.java).let {
                stockList = it?.stocks as ArrayList<SimpleStock>
            }
            sharedViewModel.setUserList(stockList)
        }
    }

    /**
     * Set the text view of list name
     * Saves the original list name before editing
     */
    private fun getListName() {
        val listName = sharedViewModel.getName()
        binding.etUserListName.append(listName)

        if (bundle.getBoolean("Editing")) {
            sharedViewModel.setNameEdit(listName)
        }
    }


    /**
     * In charge of the save list button
     * When pressing it save the list in the firebase cloud
     * Moves to the home page
     */
    private fun setCreateListButton() {
        binding.btnCreateUserList.setOnClickListener {
            val userListName = binding.etUserListName.text.toString()

            if (userListName == "")
                Toast.makeText(this@CreateListFragment.context, "Please enter list name!", Toast.LENGTH_SHORT)
                    .show()
            else {
                CoroutineScope(IO).launch {
                    if (!checkListNameValidity(userListName))
                        withContext(Main) {
                            Toast.makeText(
                                this@CreateListFragment.context,
                                "List name already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    else {
                        uploadUserListToCloud(userListName)
                        setViewModel()
                        val homeFragment = HomeFragment()
                        (activity as MainActivity).setCurrentFragment(homeFragment)
                    }
                }
            }
        }
    }

    /**
     * Checks if the list that in now created/edited has the same name
     * of other user list.
     */
    private suspend fun checkListNameValidity(listName : String): Boolean {
        val userEmail = (activity as MainActivity).accountEmail
        val document = usersCollectionRef.document(userEmail).collection("stock lists")
            .document(listName).get().await()

        if (document.exists())
            return false
        return true

    }

    /**
     * Clean view model resources.
     */
    private fun setViewModel() {
        sharedViewModel.setName("")
        sharedViewModel.setNameEdit("")
        sharedViewModel.deleteUserListInCreation()
    }

    /**
     * Actually upload the data to the cloud
     */
    private fun uploadUserListToCloud(userListName : String) {
        val userEmail = (activity as MainActivity).accountEmail
        val userList = MyList(userListName, stockList)
        val userListNameEdit = sharedViewModel.getNameEdit()

        if (userListNameEdit != "")
            usersCollectionRef.document(userEmail).collection("stock lists")
                .document(userListNameEdit).delete()
        usersCollectionRef.document(userEmail).collection("stock lists")
            .document(userListName).set(userList)
    }

    private fun setUserListRecyclerView(root: ConstraintLayout) {
        CoroutineScope(IO).launch {
            getEditedList()

            withContext(Main) {
                createListAdapter = CreateListAdapter(requireContext(), sharedViewModel, stockList)
                binding.rvCreateUserList.adapter = createListAdapter
                val layoutManager = LinearLayoutManager(root.context)
                binding.rvCreateUserList.layoutManager = layoutManager
            }
        }

    }

    /**
     * Initialize the currently created stock list
     * Updates it when a stock is added or removed
     */
    private fun setStockList() {
        val fromHomePage = bundle.getBoolean("fromHomePage")
        stockList = if (fromHomePage) {
            arrayListOf()
        } else
            sharedViewModel.getUserListInCreation()
    }

    /**
     * Makes the search view clickable, moving to the fragment which in charge
     * of searching stocks
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchView() {
        val searchView = binding.svCreateUserList

        searchView.setOnClickListener {
            setListName(binding.etUserListName.text.toString())
            val searchViewFragment = SearchViewFragment()
            val bundle = Bundle()
            bundle.putBoolean("fromHomePage", false)
            searchViewFragment.arguments = bundle
            (activity as MainActivity).setCurrentFragment(searchViewFragment)
        }
    }

    private fun setListName(name : String) {
        sharedViewModel.setName(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        setViewModel()
    }


}
