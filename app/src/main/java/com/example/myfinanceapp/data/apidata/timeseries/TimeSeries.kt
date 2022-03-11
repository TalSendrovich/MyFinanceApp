package com.example.myfinanceapp.data.apidata.timeseries

data class TimeSeries(
    val meta: Meta,
    val status: String,
    val values: List<Value>
)