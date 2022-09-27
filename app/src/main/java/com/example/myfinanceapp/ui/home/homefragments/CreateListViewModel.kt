package com.example.myfinanceapp.ui.home.homefragments

import android.view.View
import androidx.lifecycle.ViewModel

class CreateListViewModel : ViewModel() {
    private var listName : String = ""

    fun getName() : String {
        return listName
    }

    fun setName(name : String) {
        listName = name
    }


}