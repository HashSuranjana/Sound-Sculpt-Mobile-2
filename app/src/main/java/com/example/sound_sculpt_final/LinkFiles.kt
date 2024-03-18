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
import androidx.core.app.ActivityCompat
import java.io.FileInputStream
import java.io.IOException

class LinkFiles : AppCompatActivity() {

    lateinit var mediaRecorder: MediaRecorder
    lateinit var startRecordingButton: Button
    lateinit var stopRecordingButton: Button
    lateinit var playRecordingButton: Button

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkfiles)

        startRecordingButton = findViewById<Button>(R.id.start)
        stopRecordingButton = findViewById<Button>(R.id.stop)
        playRecordingButton = findViewById<Button>(R.id.play)

        val path = "${externalCacheDir?.absolutePath}/myrec.3gp"
        mediaRecorder = MediaRecorder()

        startRecordingButton.isEnabled = false
        stopRecordingButton.isEnabled = false

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            startRecordingButton.isEnabled = true
        }

        startRecordingButton.setOnClickListener {
            startRecording()
        }

        stopRecordingButton.setOnClickListener {
            stopRecording()
        }

        playRecordingButton.setOnClickListener{
            playRecording()
        }

    }

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

    private fun stopRecording() {
        mediaRecorder.stop()
        startRecordingButton.isEnabled = true
        stopRecordingButton.isEnabled = false
    }

    private fun playRecording() {
        try {
            val mediaPlayer = MediaPlayer()
            val path = "${externalCacheDir?.absolutePath}/myrec.3gp"
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            mediaPlayer.start()

            val duration = mediaPlayer.duration // Get the duration of the recorded audio in milliseconds
            val segmentDuration = duration / 7 // Split the duration into 7 segments

            // Calculate decibel levels for each segment
            for (i in 0 until 7) {
                val start = i * segmentDuration
                val end = if (i == 6) duration else (i + 1) * segmentDuration
                val decibelLevel = calculateDecibelLevel(path, start, end)
                // Do something with the decibel level for this segment
                println("Decibel level for segment $i: $decibelLevel")
            }

            mediaPlayer.setOnCompletionListener {
                mediaPlayer.release()
            }
        } catch (e: IOException) {
            // Handle IOException (e.g., log error, show error message)
            // For example:
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            // Handle IllegalStateException (e.g., log error, show error message)
            // For example:
            e.printStackTrace()
        } catch (e: SecurityException) {
            // Handle SecurityException (e.g., log error, show error message)
            // For example:
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            // Handle IllegalArgumentException (e.g., log error, show error message)
            // For example:
            e.printStackTrace()
        } catch (e: Exception) {
            // Handle any other exceptions that might occur
            // For example:
            e.printStackTrace()
        }
    }

    private fun calculateDecibelLevel(audioFilePath: String, start: Int, end: Int): Double {
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(audioFilePath)
            val trackIndex = selectTrack(mediaExtractor)
            if (trackIndex < 0) {
                // Failed to select a track
                return 0.0
            }

            mediaExtractor.selectTrack(trackIndex)

            val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            val audioRecord = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it from the user
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION // You need to define this constant
                )
                null // Return null for now, handle the permission result in onRequestPermissionsResult
            } else {
                // Permission is granted, proceed with audio recording logic
                startRecording() // Assuming you have a function to create AudioRecord object
            }

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

            val avgAmplitude = totalAmplitude / count
            val decibelLevel = 20 * Math.log10(avgAmplitude)

            return decibelLevel
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0.0
    }

    private fun calculateRMSAmplitude(buffer: ByteArray): Double {
        var sum = 0.0
        for (sample in buffer) {
            sum += sample.toDouble() * sample.toDouble()
        }
        val rms = Math.sqrt(sum / buffer.size)
        return rms
    }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with audio recording logic
                    startRecording()
                } else {
                    // Permission denied, inform the user and handle accordingly
                    // For example, show a toast or a dialog explaining why the permission is needed.
                }
            }
        }
    }

}
