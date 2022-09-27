package com.example.myfinanceapp

import androidx.lifecycle.ViewModel
import com.example.myfinanceapp.data.SimpleStock

class MainViewModel : ViewModel() {

    private var userListInCreation : ArrayList<SimpleStock> = arrayListOf()
    private var listName : String = ""
    private var listNameEdit : String = ""

    fun getName() : String {
        return listName
    }

    fun setName(name : String) {
        listName = name
    }

    fun getNameEdit() : String {
        return listNameEdit
    }

    fun setNameEdit(name : String) {
        listNameEdit = name
    }
    fun addStockToCreateUserList(symbol : String, name : String) {
        var flag = true
        for (i in 0 until userListInCreation.size) {
            if (userListInCreation[i].symbol == symbol) {
                flag = false
                break
            }
        }
        if (flag)
            userListInCreation.add(SimpleStock(symbol, name))
    }

    fun getUserListInCreation() : ArrayList<SimpleStock> {
        return userListInCreation
    }

    fun deleteStockInUserListInCreation(symbol: String) {
        for (i in 0 until userListInCreation.size) {
            if (userListInCreation[i].symbol == symbol) {
                userListInCreation.removeAt(i)
                break
            }
        }
    }

    fun deleteUserListInCreation() {
        userListInCreation.clear()
    }

    fun setUserList(list : ArrayList<SimpleStock>) {
        userListInCreation = list
    }
}