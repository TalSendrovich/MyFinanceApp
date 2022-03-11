package com.example.myfinanceapp.data

import com.google.firebase.Timestamp

data class RecentSearch (
    var symbol : String,
    var name : String,
    var date : Timestamp
    )