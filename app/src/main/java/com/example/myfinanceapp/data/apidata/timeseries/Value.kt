package com.example.myfinanceapp.data.apidata.timeseries

data class Value(
    val close: String,
    val datetime: String,
    val high: String,
    val low: String,
    val `open`: String,
    val volume: String
)