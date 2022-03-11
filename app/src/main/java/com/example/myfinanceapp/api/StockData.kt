package com.example.myfinanceapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StockData {

    val api: StockDataApi by lazy{
        Retrofit.Builder().baseUrl("https://api.twelvedata.com ")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(StockDataApi::class.java)
    }
}