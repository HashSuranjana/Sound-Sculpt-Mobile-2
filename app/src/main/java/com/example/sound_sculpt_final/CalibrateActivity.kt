package com.example.sound_sculpt_final


import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CalibrateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calibrate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Assuming you have the path to the recorded audio file
        val audioFilePath = "path/to/your/recorded/audio/file"
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(audioFilePath)
        mediaPlayer.prepare()
        val duration =
            mediaPlayer.duration // Get the duration of the recorded audio in milliseconds

        val segmentDuration = duration / 7 // Split the duration into 7 segments

        // Calculate decibel levels for each segment
    }
}
