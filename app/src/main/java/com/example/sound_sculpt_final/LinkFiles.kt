package com.example.sound_sculpt_final
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.sound_sculpt_final.R
import java.io.FileInputStream
import java.io.IOException

class LinkFiles : AppCompatActivity() {

    // Variables for UI elements
    lateinit var mediaRecorder: MediaRecorder
    lateinit var startRecordingButton: Button
    lateinit var stopRecordingButton: Button
    lateinit var playRecordingButton: Button
    lateinit var decibelTextView: TextView

    // Request code for audio recording permission
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkfiles)

        // Initialize UI elements
        startRecordingButton = findViewById(R.id.start)
        stopRecordingButton = findViewById(R.id.stop)
        playRecordingButton = findViewById(R.id.play)
        decibelTextView = findViewById(R.id.decibelTextView)

        // Initialize MediaRecorder
        mediaRecorder = MediaRecorder()

        // Initially disable buttons until permission is granted
        startRecordingButton.isEnabled = false
        stopRecordingButton.isEnabled = false

        // Request permission for recording audio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            // Enable start recording button if permission is granted
            startRecordingButton.isEnabled = true
        }

        // Set click listeners for buttons
        startRecordingButton.setOnClickListener {
            startRecording()
        }

        stopRecordingButton.setOnClickListener {
            stopRecording()
        }

        playRecordingButton.setOnClickListener {
            playRecording()
        }
    }

    // Function to start recording audio
    private fun startRecording() {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val path = "${externalCacheDir?.absolutePath}/myrec.3gp"
            mediaRecorder.setOutputFile(path)
            mediaRecorder.prepare()
            mediaRecorder.start()
            stopRecordingButton.isEnabled = true
            startRecordingButton.isEnabled = false
        } catch (e: Exception) {
            // Handle exception (e.g., log error, show error message)
        }
    }

    // Function to stop recording audio
    private fun stopRecording() {
        mediaRecorder.stop()
        startRecordingButton.isEnabled = true
        stopRecordingButton.isEnabled = false
    }

    // Function to play recorded audio and calculate decibel levels
    private fun playRecording() {
        try {
            val mediaPlayer = MediaPlayer()
            val path = "${externalCacheDir?.absolutePath}/myrec.3gp"
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            mediaPlayer.start()

            val duration = mediaPlayer.duration // Get the duration of the recorded audio in milliseconds
            val segmentDuration = duration / 7 // Split the duration into 7 segments

            // Calculate decibel levels for each segment and display in TextView
            for (i in 0 until 7) {
                val start = i * segmentDuration
                val end = if (i == 6) duration else (i + 1) * segmentDuration
                val decibelLevel = calculateDecibelLevel(path, start, end)
                decibelTextView.append("Decibel level for segment $i: $decibelLevel\n")
            }

            // Release MediaPlayer after playback completes
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
        } catch (e: IOException) {
            // Handle IOException (e.g., log error, show error message)
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            // Handle IllegalStateException (e.g., log error, show error message)
            e.printStackTrace()
        } catch (e: SecurityException) {
            // Handle SecurityException (e.g., log error, show error message)
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            // Handle IllegalArgumentException (e.g., log error, show error message)
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle any other exceptions that might occur
            e.printStackTrace()
        }
    }

    // Function to calculate decibel level
    private fun calculateDecibelLevel(audioFilePath: String, start: Int, end: Int): Double {
        try {
            // MediaExtractor to extract audio data
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(audioFilePath)
            val trackIndex = selectTrack(mediaExtractor)
            if (trackIndex < 0) {
                // Failed to select a track
                return 0.0
            }

            mediaExtractor.selectTrack(trackIndex)

            // Minimum buffer size for AudioRecord
            val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            // Request permission if not granted
            val audioRecord = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
                null
            } else {
                startRecording()
            }

            // Read audio data from file and calculate decibel level
            val audioInputStream = FileInputStream(audioFilePath)
            audioInputStream.skip(start.toLong())

            val buffer = ByteArray(bufferSize)
            var bytesRead = 0
            var totalAmplitude = 0.0
            var count = 0

            while (audioInputStream.read(buffer).also { bytesRead = it } != -1) {
                if (bytesRead >= 0 && count < (end - start) / bufferSize) {
                    totalAmplitude += calculateRMSAmplitude(buffer)
                    count++
                } else {
                    break
                }
            }

            audioInputStream.close()
            mediaExtractor.release()

            // Calculate average amplitude and decibel level
            val avgAmplitude = totalAmplitude / count
            val decibelLevel = 20 * Math.log10(avgAmplitude)

            return decibelLevel
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0.0
    }

    // Function to calculate RMS amplitude
    private fun calculateRMSAmplitude(buffer: ByteArray): Double {
        var sum = 0.0
        for (sample in buffer) {
            sum += sample.toDouble() * sample.toDouble()
        }
        val rms = Math.sqrt(sum / buffer.size)
        return rms
    }

    // Function to select audio track
    private fun selectTrack(extractor: MediaExtractor): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                return i
            }
        }
        return -1
    }

    // Function to handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }

}
