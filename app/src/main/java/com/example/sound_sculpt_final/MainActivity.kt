package com.example.sound_sculpt_final

import Settings
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.sound_sculpt_final.databinding.ActivityMainBinding
import com.example.sound_sculpt_final.databinding.ActivityRegisterBinding
import com.example.sound_sculpt_final.ui.theme.Sound_Sculpt_FinalTheme
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(this, Home())
                    true
                }
                R.id.profile -> {
                    replaceFragment(this, User_profile())
                    true
                }
                R.id.settings -> {
                    replaceFragment(this, Settings())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        replaceFragment(this, Home())
    }

    private fun replaceFragment(activity: AppCompatActivity, fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}



