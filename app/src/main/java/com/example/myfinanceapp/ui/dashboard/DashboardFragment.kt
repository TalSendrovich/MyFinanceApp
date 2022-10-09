package com.example.myfinanceapp.ui.dashboard

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapp.Adapters.NewsAdapter
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.R
import com.example.myfinanceapp.api.stockApi.ApiManager
import com.example.myfinanceapp.databinding.FragmentDashboardBinding
import com.example.myfinanceapp.ui.home.homefragments.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private var newsApiKey = "NWFRzJUDUt3sNlLr6LRB2qLRjhcsVu6Ikt3fty03"
    private lateinit var newsAdapter: NewsAdapter
    //private lateinit var loadingDialog: AlertDialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setSearchView(root)
        val loadingDialog = startProgressBar()

        CoroutineScope(IO).launch {
            getNews(root, "msft,tsco,amzn")
            withContext(Main) {
                endProcessBar(loadingDialog)
            }
        }

        return root
    }

    private fun endProcessBar(progressBar: AlertDialog) {
        progressBar.dismiss()
    }

    private fun startProgressBar(): AlertDialog {
        val progressBar = AlertDialog.Builder(this.context)
            .setView(R.layout.custom_dialog)
            .setCancelable(true)
            .create()
        progressBar.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressBar.show()
        return progressBar
    }

    private fun setSearchView(root: View) {
        binding.svNews.onActionViewExpanded()

        setSearchViewQueries(root)
    }

    private fun setSearchViewQueries(root: View) {
        binding.svNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.svNews.clearFocus()
                if (!query.isNullOrEmpty() && query.isOnlyLetters()) {
                    val loadingDialog = startProgressBar()
                    CoroutineScope(IO).launch {
                        getNews(root, query)
                        withContext(Main) {
                            endProcessBar(loadingDialog)
                        }
                    }
                } else
                    Toast.makeText(
                        context,
                        "Invalid stock name",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    fun String.isOnlyLetters() = all { it.isLetter() }

    private suspend fun getNews(root: View, symbols: String) {
        val apiManager = ApiManager()

        val news = apiManager.getNews(symbols)

        withContext(Main) {
            newsAdapter =
                NewsAdapter(requireContext(), news.data)
            binding.rvNewsFeed.adapter = newsAdapter

            val layoutManager = LinearLayoutManager(
                root.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.rvNewsFeed.layoutManager = layoutManager

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


