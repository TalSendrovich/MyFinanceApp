package com.example.myfinanceapp.ui.home.homefragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.api.StockData
import com.example.myfinanceapp.data.apidata.quote.Quote
import com.example.myfinanceapp.data.apidata.timeseries.TimeSeries
import com.example.myfinanceapp.data.apidata.timeseries.Value
import com.example.myfinanceapp.databinding.FragmentStockInfoBinding
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.example.myfinanceapp.R
import com.example.myfinanceapp.data.RecentSearch
import com.example.myfinanceapp.ui.home.HomeFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.admin.v1.Index
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.time.LocalDateTime


const val TAG = "StockInfo"

/**
 * A simple [Fragment] subclass.
 * Use the [StockInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class StockInfo : Fragment() {

    private var db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    lateinit var name: String
    private var _binding: FragmentStockInfoBinding? = null
    private val binding get() = _binding!!
    var open: String? = ""
    var low: String? = ""
    var high: String? = ""
    var previous_closed: Float? = 0f
    var price: Double? = 0.0
    private lateinit var bundle: Bundle
    private val apiKey = "73990e67670a472396934b1757bfb135"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStockInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Saves important information
        bundle = this.arguments!!
        val loadingDialog = LoadingDialog(activity as MainActivity)
        loadingDialog.startLoadingDialog()
        //setDialog()
        // Makes the return button work
        returnButton()
        launchRealTimeData(loadingDialog)
        saveToRecent()

        return root
    }


    private fun setDialog() {

    }


    /**
     * Saves the data of the recent searches the user made
     * Limited to the 5 most recent
     */
    private fun saveToRecent() {
        val emailAccount = (activity as MainActivity).accountEmail
        val recentsCollectionRef = db.collection("users/$emailAccount/recent search")
        CoroutineScope(Dispatchers.IO).launch {
            maintainRecentsLimit(recentsCollectionRef, emailAccount)
            try {
                val symbol = bundle.getString("symbol")
                val name = bundle.getString("name")
                val time = Timestamp.now()
                recentsCollectionRef.document(symbol!!).set(RecentSearch(symbol, name!!, time))
                    .await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.message?.let { Log.d("StockInfo", it) }
                }
            }
        }
    }

    /**
     * Checks if the user has more than 5 saves searches
     * If he does than deletes the earliest one
     */
    private suspend fun maintainRecentsLimit(db: CollectionReference, emailAccount: String) {
        CoroutineScope(Dispatchers.IO).async {
            val recentsCollectionRef = db.orderBy("date", Query.Direction.ASCENDING).get().await()
            if (recentsCollectionRef.documents.size >= 5)
                recentsCollectionRef.documents[0].reference.delete()
        }.await()
    }

    private fun launchRealTimeData(loadingDialog : LoadingDialog) {
        launchRealTimePrice()
        launchRealTimeQuote()
        launchRealTimeSeries(loadingDialog)
    }

    /** ---------- Start to launch data ----------*/

    /**
     * Immediately when the fragment is created
     * Takes data from the api
     * Creating the Graph of the stock for TODAY
     */
    private fun launchRealTimeSeries(loadingDialog : LoadingDialog) {
        lifecycleScope.launchWhenCreated {
            val response = try {
                bundle.getString("symbol")?.let {
                    StockData.api.getTimeSeries(it, "1min", "5000", "yesterday", apiKey)
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException, unexpected response")
                return@launchWhenCreated
            }
            if (response != null) {
                delay(200)
                if (response.isSuccessful && response.body() != null)
                    createGraph(response.body(), loadingDialog)
            }
        }
    }

    /**
     * Collecting the necessary data from the api call using helper functions
     */
    private fun createGraph(timeSeries: TimeSeries?, loadingDialog : LoadingDialog) {
        val size = timeSeries?.values?.size!!
        val positions = setStockData(size, timeSeries.values)
        val line = setStockPreviousData(size, previous_closed)
        val graphSize = setGraphSize(previous_closed)

        val dataSets: MutableList<ILineDataSet> = ArrayList()
        dataSets.add(positions)
        dataSets.add(line)
        dataSets.add(graphSize)
        val data = LineData(dataSets)

        paintGraph(data)
        loadingDialog.endDialog()
    }

    private fun setGraphSize(prev: Float?): LineDataSet {
        val positions: MutableList<Entry> = mutableListOf()

        for (i in 0..195)
            positions.add(Entry(i.toFloat(), prev!!))

        val setComp1 = LineDataSet(positions, null)
        setComp1.apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            isVisible = false
        }
        return setComp1
    }

    /**
     * Actually paints the graph
     */
    private fun paintGraph(info: LineData) {
        val chart = binding.gvStock
        chart.apply {
            data = info
            setDrawBorders(false)
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.isEnabled = false
            axisLeft.setDrawGridLines(false)
            axisLeft.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true);
            val mv = CustomMarkerView(context, R.layout.gv_info)
            marker = mv
            invalidate() // refresh
        }
    }

    /**
     * Collecting and setting the data To The Previous Line Graph from the api call
     */
    private fun setStockPreviousData(size: Int, previousClosed: Float?): LineDataSet {
        val line: MutableList<Entry> = mutableListOf()
        line.add(Entry(0f, previous_closed!!))
        line.add(Entry(size.toFloat() / 2, previous_closed!!))

        val setComp2 = LineDataSet(line, "Previous Close")
        setComp2.apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawCircles(false)
            color = R.color.black
        }
        return setComp2
    }

    /**
     * Collecting and setting the data To The Stock Graph from the api call
     */
    private fun setStockData(size: Int, values: List<Value>): LineDataSet {
        val positions: MutableList<Entry> = mutableListOf()
        var counter = 0.0
        var counter2 = 0.0
        for (data in values) {
            if (counter.toInt() % 2 == 0) {
                positions.add(
                    Entry(
                        counter2.toFloat(),
                        values[size - counter.toInt() - 1].high.toFloat()
                    )
                )
                counter2++
            }
            counter++
        }
        val setComp1 = LineDataSet(positions, null)
        setComp1.apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            setDrawValues(false)
            setDrawCircles(false)
            color = R.color.red
        }
        return setComp1
    }

    /**
     * Immediately when the fragment is created
     * Takes data from the api
     * Setting general information for the layout
     */
    private fun launchRealTimeQuote() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                bundle.getString("symbol")?.let {
                    StockData.api.getQuote(it, apiKey)
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException, unexpected response")
                return@launchWhenCreated
            }
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    setLayoutTextViews(response.body())
                }
            }
        }
    }

    /**
     * Setting the stock_info_layout
     */
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun setLayoutTextViews(quote: Quote?) {
        open = quote?.open
        low = quote?.low
        high = quote?.high
        previous_closed = quote?.previous_close?.toFloat()
        val pointsChange = price?.minus(previous_closed?.toDouble()!!)
        Log.d("StockInfo", "$pointsChange")
        var percentageChange = 0.0

        binding.tvStockSymbol.text = quote?.symbol
        binding.tvOpenText.text = open
        var sign: String
        if (pointsChange!! >= 0) {
            percentageChange = (price!! / previous_closed!!) *100 - 100
            sign = "+"
            if (pointsChange > 0)
                binding.tvPriceChange.setTextColor(Color.GREEN)
        } else {
            percentageChange = 100 - (price!! / previous_closed!!) * 100
            binding.tvPriceChange.setTextColor(Color.RED)
            sign = "-"
        }

        binding.tvPrevCloseText.text = previous_closed.toString()
        binding.tvPriceChange.text = String.format("%.2f", pointsChange) +
                " ($sign${String.format("%.2f", percentageChange)}%)"
        binding.tvVolumeText.text = quote?.volume

        binding.tvDayRangeText.text = "$low - $high"
        binding.tv52WeekRangeText.text = quote?.fifty_two_week?.low + " - " +
                quote?.fifty_two_week?.high

    }

    /**
     * Immediately when the fragment is created
     * Takes data from the api
     * Getting the current price of the stock
     */
    @SuppressLint("SetTextI18n")
    private fun launchRealTimePrice() {
        lifecycleScope.launchWhenCreated {
            Log.d(TAG, "Symbol is: ${bundle.getString("symbol")}")
            val response = try {
                bundle.getString("symbol")?.let {
                    StockData.api.getPrice(it, apiKey)
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated

            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException, unexpected response")
                return@launchWhenCreated
            }
            if (response != null) {
                if (response.isSuccessful && response.body() != null) {
                    price = response.body()!!.price.toDouble()
                    binding.tvCurrentPrice.text = price.toString()
                }
            }
        }
    }

    /** ---------- End to launch data ----------*/


    private fun returnButton() {
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nextFrag = HomeFragment()
                activity!!.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.nav_host_fragment_activity_main, nextFrag, "OptionsFragment")
                    commit()
                }
            }
        })
    }
}


