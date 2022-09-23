package com.example.myfinanceapp.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapp.databinding.FragmentAccountBinding
import com.example.myfinanceapp.R
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import com.example.myfinanceapp.MainActivity
import com.example.myfinanceapp.api.StockData
import com.example.myfinanceapp.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class AccountFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var notificationsViewModel: AccountViewModel
    private var _binding: FragmentAccountBinding? = null
    private val usersCollectionRef = Firebase.firestore.collection("users")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var signInClient : GoogleSignInClient

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setGoogleLogOut()
        setLogOut()
        return root
    }

    /**
     * Function responsible for logout when logged in without google
     * NOT FINISHED!
     */
    private fun setLogOut() {
        binding.btnTest.setOnClickListener {
            val intent = Intent(activity as MainActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /** Function that is responsible for the LogOut mechanics for google */
    //TODO logout without google
    private fun setGoogleLogOut() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .requestProfile()
            .build()
        signInClient = GoogleSignIn.getClient(requireActivity(), options)
        mAuth = FirebaseAuth.getInstance()
        binding.btnGoogleSignOut.setOnClickListener{
            mAuth.signOut()
            signInClient.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}