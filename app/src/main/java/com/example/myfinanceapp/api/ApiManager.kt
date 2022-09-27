package com.example.myfinanceapp.api

import com.example.myfinanceapp.data.apidata.Price
import com.example.myfinanceapp.data.apidata.quote.Quote
import retrofit2.Response

class ApiManager {
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
    }

    suspend fun calculatePercentageChange(currentPrice : String, previouslyClosedPrice: String) : String {
        val difference = currentPrice.toFloat() - previouslyClosedPrice.toFloat()
        return "%.2f".format((difference / previouslyClosedPrice.toFloat()) * 100)
    }

    private fun Quote(): Quote {
        return Quote()
    }


}