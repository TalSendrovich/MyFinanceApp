package com.example.myfinanceapp

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myfinanceapp.data.User
import com.example.myfinanceapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

const val REQUEST_CODE_SIGN_IN = 0

class SignInActivity : AppCompatActivity() {

    private val usersCollectionRef = Firebase.firestore.collection("users")
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        /** Edit all EditText to their normal design by clicking them */
        setEditTextListeners()
        /** Set OnClickListeners to the Log In buttons */
        setSignInButtons()
        /** Set the necessary objects for Google Sign In */
        setGoogleSignIn()
    }

    /** Actually saves the user in the DataBase */
    private fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            usersCollectionRef.document(user.email).set(user).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignInActivity, "Successfully Saved Data!", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignInActivity, "Failed Saved account", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        clearEditTexts()
    }

    private fun clearEditTexts() {
        binding.etEmailAddress.setText("")
        binding.etPassword.setText("")
    }

    private fun setSignInButtons() {
        /** Set editTexts */
        val etEmailAddress = binding.etEmailAddress
        val etPassword = binding.etPassword
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

        /** Checks the all the details about the user are correct
         *  Checks if the user is not already signed up
         ** If they are correct than saves the user in the data base */
        binding.btnSignIn.setOnClickListener {
            var counter = 0
            val password = etPassword.text.toString()
            val email = etEmailAddress.text.toString()
            val user = User(email, password)

            // Email Validator
            if (!email.matches(emailPattern)) {
                counter++
                etEmailAddress.setTextColor(Color.parseColor("#FF0000"))
            }
            if (password == "") {
                counter++
                Toast.makeText(this, "Invalid information", Toast.LENGTH_SHORT).show()
            }
            if (counter == 0) {
                usersCollectionRef.document(email).get().addOnCompleteListener{
                    var isRegistered = true
                    if(it.isSuccessful) {
                        if (!it.result?.exists()!!) {
                            isRegistered = false
                            saveUser(user)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "User is already registered", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun checkIfUserExist(email: String): Boolean {
        return false
    }


    /** The function in charge of set the OnClickListeners of the Edit Text Views */
    private fun setEditTextListeners() {
        binding.etEmailAddress.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etEmailAddress.callOnClick()
            }
        }
        binding.etEmailAddress.setOnClickListener {
            binding.etEmailAddress.setTextColor(Color.parseColor("#FF000000"))
        }
        binding.etPassword.setOnClickListener {
            binding.etPassword.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    /** ---------------- Google Sign In mechanics -------------------*/

    private fun setGoogleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .requestProfile()
            .build()
        signInClient = GoogleSignIn.getClient(this, options)
    }

    private fun signIn() {
        val signInIntent = signInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val acct = GoogleSignIn.getLastSignedInAccount(this)
                    acct?.email?.let { Log.d("SignInAcovity", it) }
                    saveUser(User(acct?.email!!))
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email", acct.email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
