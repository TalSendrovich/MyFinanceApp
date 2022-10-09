package com.example.myfinanceapp.api.stockApi

import android.util.Log
import com.example.myfinanceapp.api.newsApi.NewsData
import com.example.myfinanceapp.data.apidata.newsdata.News
import com.example.myfinanceapp.data.apidata.quote.Quote
import com.example.myfinanceapp.data.apidata.timeseries.TimeSeries
import com.example.myfinanceapp.ui.home.homefragments.TAG
import kotlinx.coroutines.delay

class ApiManager {
    private var newsApiKey = "NWFRzJUDUt3sNlLr6LRB2qLRjhcsVu6Ikt3fty03"
    private val apiKey = "73990e67670a472396934b1757bfb135"

    suspend fun getPrice(symbol : String, apikey : String) : String{
        val priceResponse = StockData.api.getPrice(symbol, apikey)
        if (priceResponse.isSuccessful && priceResponse.body() != null){
            return priceResponse.body()!!.price
        }
        return "-1"
    }

    suspend fun getQuote(symbol : String, apikey : String) : Quote {
        val quoteResponse = StockData.api.getQuote(symbol, apikey)
        if (quoteResponse.isSuccessful && quoteResponse.body() != null){
            return quoteResponse.body()!!
        }
        return Quote()

//    } catch (e: IOException) {
//        Log.e(TAG, "IOException, you might not have internet connection")
//        return@launchWhenCreated
//    } catch (e: HttpException) {
//        Log.e(TAG, "HTTPException, unexpected response")
//        return@launchWhenCreated
//    }
    }

    suspend fun getTimeSeries(symbol: String) : TimeSeries {
        val response = StockData.api.getTimeSeries(
            symbol, "1min", "5000", "2022-09-30", apiKey)

        if (response.isSuccessful && response.body() != null)
            return response.body()!!

        return TimeSeries()

    }

    private fun TimeSeries(): TimeSeries {
        return TimeSeries()
    }

    suspend fun getNews(symbol: String) : News {
        val newsResponse = NewsData.newsApi.getNews(newsApiKey, symbol)

        if (newsResponse.isSuccessful && newsResponse.body()!= null) {
            return newsResponse.body()!!
        }
        return News()
    }

    private fun News(): News {
        return News()
    }


    fun calculatePercentageChange(currentPrice : String, previouslyClosedPrice: String) : String {
        val difference = currentPrice.toFloat() - previouslyClosedPrice.toFloat()
        return "%.2f".format((difference / previouslyClosedPrice.toFloat()) * 100)
    }



    private fun Quote(): Quote {
        return Quote()
    }


}