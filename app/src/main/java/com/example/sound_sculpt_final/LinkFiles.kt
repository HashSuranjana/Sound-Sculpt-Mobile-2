package com.example.sound_sculpt_final

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.core.app.ActivityCompat
import java.io.IOException

class LinkFiles : AppCompatActivity() {

    lateinit var mediaRecorder: MediaRecorder
    lateinit var startRecordingButton: Button
    lateinit var stopRecordingButton: Button
    lateinit var playRecordingButton: Button

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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
        } else {
            startRecordingButton.isEnabled = true
        }


        startRecordingButton.setOnClickListener {
            try {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder.setOutputFile(path)
                mediaRecorder.prepare()
                mediaRecorder.start()
                stopRecordingButton.isEnabled = true
                startRecordingButton.isEnabled = false
            } catch (e: Exception) {
                // Handle exception (e.g., log error, show error message)
            }
        }


        stopRecordingButton.setOnClickListener {
            mediaRecorder.stop()
            startRecordingButton.isEnabled = true
            stopRecordingButton.isEnabled = false
        }

        playRecordingButton.setOnClickListener{
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(path)
                mediaPlayer.prepare()
                mediaPlayer.start()
                // Optionally, you may also want to release the media player after playback is complete.
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

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordingButton.isEnabled = true
            } else {
                // Permission denied, inform the user and handle accordingly
                // For example, show a toast or a dialog explaining why the permission is needed.
            }
        }
    }

}
