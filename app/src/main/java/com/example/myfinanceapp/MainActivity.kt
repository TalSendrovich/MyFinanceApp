package com.example.myfinanceapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myfinanceapp.databinding.ActivityMainBinding
import com.example.myfinanceapp.ui.dashboard.DashboardFragment
import com.example.myfinanceapp.ui.home.HomeFragment
import com.example.myfinanceapp.ui.notifications.NotificationsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Displaying the home fragment on top of the FrameLayout
        val homeFragment = HomeFragment()
        setCurrentFragment(homeFragment)

        // Setting the navigation Bar
        navigationBarController()
    }

    private fun navigationBarController(){
        val homeFragment = HomeFragment()
        val dashboardFragment = DashboardFragment()
        val notificationsFragment = NotificationsFragment()

        setCurrentFragment(homeFragment)
        val navView: BottomNavigationView = binding.navView
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> setCurrentFragment(homeFragment)
                R.id.navigation_dashboard -> setCurrentFragment(dashboardFragment)
                R.id.navigation_notifications -> setCurrentFragment(notificationsFragment)

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