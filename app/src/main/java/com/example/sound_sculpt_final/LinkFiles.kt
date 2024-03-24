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

    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var maxDecibelTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var timerHandler: Handler

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

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        maxDecibelTextView = findViewById(R.id.maxDecibelTextView)
        timerTextView = findViewById(R.id.timerTextView)

        timerHandler = Handler(Looper.getMainLooper())

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
                val intent = Intent(this, SaveFile::class.java)
                intent.putExtra("dBValues", maxDecibelArrayList.toFloatArray())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please record 7 times.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecording() {
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

        isRecording = true
        startRecordingButton.text = "Recording..."
        stopRecordingButton.isEnabled = false // Disable stop button while recording

        startTimer() // Start the timer

        val bufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

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

    private fun stopRecording() {
        isRecording = false
        startRecordingButton.text = "Record"
        stopRecordingButton.isEnabled = true // Enable stop button after recording
        if (recordingsCount < 7) {
            maxDecibelArrayList.add(maxDecibel)
            recordingsCount++
            Toast.makeText(this, "Recording $recordingsCount completed.", Toast.LENGTH_SHORT).show()
        }

        maxDecibel = 0f // Reset maxDecibel for the next recording
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
        Log.d("Timer", "Timer started")
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startTime

            val hours = (elapsedTime / 3600000).toInt()
            val minutes = ((elapsedTime - hours * 3600000) / 60000).toInt()
            val seconds = ((elapsedTime - hours * 3600000 - minutes * 60000) / 1000).toInt()
            val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            timerTextView.text = timeString

            timerHandler.postDelayed(this, 1000)
            Log.d("Timer", "Timer updated")
        }
    }

    private fun processAudioBuffer(buffer: ShortArray, readSize: Int) {
        val maxDecibelArray = FloatArray(readSize)
        for (i in 0 until readSize) {
            maxDecibelArray[i] = buffer[i].toFloat() / Short.MAX_VALUE * 100
        }
        maxDecibelArray.sortDescending()

        // Store the maximum decibel value for this buffer
        maxDecibel = maxDecibelArray[0]

        Handler(Looper.getMainLooper()).post {
            updateMaxDecibelTextView(maxDecibel)
        }
    }

    private fun updateMaxDecibelTextView(maxDecibel: Float) {
        maxDecibelTextView.text = "Max Decibel: $maxDecibel"
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}
