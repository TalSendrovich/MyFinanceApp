package com.example.myfinanceapp.ui.home.homefragments

import android.util.Log
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.api.ApiManager
import com.example.myfinanceapp.data.MyList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserLists {
    val apiManager = ApiManager()

    suspend fun getUserLists(userEmail: String, userLists: ArrayList<MyList>) {
        userLists.clear()
        val listsCollectionRef = Firebase.firestore.collection("users/$userEmail/stock lists")

        val querySnapshot = listsCollectionRef.get().await()
        for (doc in querySnapshot.documents) {
            doc.toObject(MyList::class.java)?.let {
                userLists.add(it)
            }
        }
    }

    suspend fun getStocksPrices(userLists: ArrayList<MyList>) {

        //val priceResponse = apiManager.getPrice(userLists)
    }
}