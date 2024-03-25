package com.example.sound_sculpt_final

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class LinkFiles : AppCompatActivity() {

    // Declare UI elements
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var maxDecibelTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var timerHandler: Handler

    // Recording variables
    private var isRecording = false
    private var audioRecord: AudioRecord? = null
    private lateinit var userId: String
    private var maxDecibel: Float = 0f
    private var maxDecibelArrayList = ArrayList<Float>()
    private var recordingsCount = 0

    // Timer variables
    private var startTime: Long = 0
    private var elapsedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkfiles)

        // Get current user's ID from Firebase
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Initialize UI elements
        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        maxDecibelTextView = findViewById(R.id.maxDecibelTextView)
        timerTextView = findViewById(R.id.timerTextView)

        // Handler for timer updates
        timerHandler = Handler(Looper.getMainLooper())

        // Set click listeners for start and stop recording buttons
        startRecordingButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        stopRecordingButton.setOnClickListener {
            stopRecording()
            stopTimer()
            if (recordingsCount == 7) {
                // If 7 recordings are completed, navigate to SaveFile activity
                val intent = Intent(this, SaveFile::class.java)
                intent.putExtra("dBValues", maxDecibelArrayList.toFloatArray())
                startActivity(intent)
            } else {
                // Inform user to complete 7 recordings
                Toast.makeText(this, "Please record 7 times.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to start audio recording
    private fun startRecording() {
        // Check for RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
            return
        }

        // Update UI and start timer
        isRecording = true
        startRecordingButton.text = "Recording..."
        stopRecordingButton.isEnabled = false // Disable stop button while recording
        startTimer() // Start the timer

        // Initialize AudioRecord object
        val bufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        // Start recording in a separate thread
        val recordingThread = Thread {
            val buffer = ShortArray(bufferSize)
            audioRecord?.startRecording()

            while (isRecording) {
                val readSize = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (readSize > 0) {
                    processAudioBuffer(buffer, readSize)
                }
            }

            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        }

        recordingThread.start()
    }

    // Function to stop audio recording
    private fun stopRecording() {
        // Update UI and enable stop button
        isRecording = false
        startRecordingButton.text = "Record"
        stopRecordingButton.isEnabled = true // Enable stop button after recording

        // If less than 7 recordings are completed, add max decibel value to list
        if (recordingsCount < 7) {
            maxDecibelArrayList.add(maxDecibel)
            recordingsCount++
            Toast.makeText(this, "Recording $recordingsCount completed.", Toast.LENGTH_SHORT).show()
        }

        maxDecibel = 0f // Reset maxDecibel for the next recording
    }

    // Function to start the timer
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
        Log.d("Timer", "Timer started")
    }

    // Function to stop the timer
    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    // Runnable for updating the timer
    private val timerRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime

            // Convert elapsed time to hours, minutes, and seconds
            val hours = (elapsedTime / 3600000).toInt()
            val minutes = ((elapsedTime - hours * 3600000) / 60000).toInt()
            val seconds = ((elapsedTime - hours * 3600000 - minutes * 60000) / 1000).toInt()
            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            // Update timer text view
            timerTextView.text = timeString

            // Schedule the next timer update
            timerHandler.postDelayed(this, 1000)
            Log.d("Timer", "Timer updated")
        }
    }

    // Function to process audio buffer and update max decibel value
    private fun processAudioBuffer(buffer: ShortArray, readSize: Int) {
        val maxDecibelArray = FloatArray(readSize)
        for (i in 0 until readSize) {
            maxDecibelArray[i] = buffer[i].toFloat() / Short.MAX_VALUE * 100
        }
        maxDecibelArray.sortDescending()

        // Store the maximum decibel value for this buffer
        maxDecibel = maxDecibelArray[0]

        // Update max decibel text view
        Handler(Looper.getMainLooper()).post {
            updateMaxDecibelTextView(maxDecibel)
        }
    }

    // Function to update max decibel text view
    private fun updateMaxDecibelTextView(maxDecibel: Float) {
        maxDecibelTextView.text = "Decibel Value: $maxDecibel"
    }

    companion object {
        // Constants for audio recording parameters
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}
