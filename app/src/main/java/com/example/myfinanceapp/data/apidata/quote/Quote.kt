package com.example.myfinanceapp.data.apidata.quote

data class Quote(
    val average_volume: String,
    val change: String,
    val close: String,
    val currency: String,
    val datetime: String,
    val exchange: String,
    val fifty_two_week: FiftyTwoWeek,
    val high: String,
    val low: String,
    val name: String,
    val `open`: String,
    val percent_change: String,
    val previous_close: String,
    val symbol: String,
    val volume: String
)