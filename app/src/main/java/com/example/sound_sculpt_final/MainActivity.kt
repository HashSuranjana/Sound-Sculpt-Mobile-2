package com.example.sound_sculpt_final

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
import com.example.sound_sculpt_final.databinding.ActivityMainBinding
import com.example.sound_sculpt_final.databinding.ActivityRegisterBinding
import com.example.sound_sculpt_final.ui.theme.Sound_Sculpt_FinalTheme

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun replaceFragment(fragment :Fragment){

        
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.
    }
}


