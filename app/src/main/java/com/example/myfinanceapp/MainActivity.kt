package com.example.myfinanceapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myfinanceapp.databinding.ActivityMainBinding
import com.example.myfinanceapp.ui.dashboard.DashboardFragment
import com.example.myfinanceapp.ui.home.HomeFragment
import com.example.myfinanceapp.ui.account.AccountFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    lateinit var accountEmail : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting the navigation Bar
        navigationBarController()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Displaying the home fragment on top of the FrameLayout
        val homeFragment = HomeFragment()
        if (user != null) {
            accountEmail = user.email.toString()
            setCurrentFragment(homeFragment)
        } else {
            val email = intent.extras?.getString("email")
            if (email != null) {
                accountEmail = email
                setCurrentFragment(homeFragment)
            } else {
                val signInIntent = Intent(this, SignInActivity::class.java)
                startActivity(signInIntent)
            }
        }
    }

    private fun navigationBarController(){
        val homeFragment = HomeFragment()
        val dashboardFragment = DashboardFragment()
        val notificationsFragment = AccountFragment()

        setCurrentFragment(homeFragment)
        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> setCurrentFragment(homeFragment)
                R.id.navigation_dashboard -> setCurrentFragment(dashboardFragment)
                R.id.navigation_account -> setCurrentFragment(notificationsFragment)

            }
            true
        }
    }

    fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_activity_main, fragment, "OptionsFragment")
            addToBackStack(null)
            commit()
        }
    }
}