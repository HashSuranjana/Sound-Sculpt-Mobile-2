package com.example.sound_sculpt_final

import Home
import Settings
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set listener for bottom navigation items
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Replace fragment with Home fragment
                    replaceFragment(Home())
                    true
                }
                R.id.profile -> {
                    // Replace fragment with Profile fragment and pass USER_ID as argument
                    val userId = intent.getStringExtra("USER_ID")
                    val profileFragment = User_profile().apply {
                        arguments = Bundle().apply {
                            putString("USER_ID", userId)
                        }
                    }
                    replaceFragment(profileFragment)
                    true
                }
                R.id.settings -> {
                    // Replace fragment with Settings fragment
                    replaceFragment(Settings())
                    true
                }
                else -> false
            }
        }

        // Set default fragment as Home
        replaceFragment(Home())
    }

    // Function to replace fragment in frame layout
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
