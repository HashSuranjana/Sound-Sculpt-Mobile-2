package com.example.sound_sculpt_final
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class LinkFiles : AppCompatActivity() {

    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var playRecordingButton: Button
    private lateinit var maxDecibelTextView: TextView

    private var isRecording = false
    private var audioRecord: AudioRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkfiles)

        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        playRecordingButton = findViewById(R.id.playRecordingButton)
        maxDecibelTextView = findViewById(R.id.maxDecibelTextView)

        startRecordingButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        stopRecordingButton.setOnClickListener {
            stopRecording()
        }

        playRecordingButton.setOnClickListener {
            // Implement play functionality here
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
    }

    private fun processAudioBuffer(buffer: ShortArray, readSize: Int) {
        // Process audio buffer and calculate decibel values
        val maxDecibelArray = FloatArray(readSize)
        for (i in 0 until readSize) {
            maxDecibelArray[i] = buffer[i].toFloat() / Short.MAX_VALUE * 100 // Convert to decibel scale (0 to 100)
        }

        // Sort the array to find the 7 maximum decibel values
        maxDecibelArray.sortDescending()

        // Get the 7 maximum decibel values
        val max7DecibelArray = maxDecibelArray.take(7).toFloatArray()

        // Update UI with the 7 maximum decibel values
        Handler(Looper.getMainLooper()).post {
            updateMaxDecibelTextView(max7DecibelArray)
        }
    }

    private fun updateMaxDecibelTextView(max7DecibelArray: FloatArray) {
        maxDecibelTextView.text = "Max Decibel Values:\n${max7DecibelArray.joinToString(", ")}"
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}
