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

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(Home())
                    true
                }
                R.id.profile -> {
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
                    replaceFragment(Settings())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        replaceFragment(Home())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
