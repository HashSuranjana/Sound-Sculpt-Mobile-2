package com.example.sound_sculpt_final

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import androidx.core.app.ActivityCompat

class LinkFiles : AppCompatActivity() {

    lateinit var mediaRecorder: MediaRecorder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_files)

        var path = Environment.getExternalStorageDirectory().toString() + "/myrec.3gp"
        mediaRecorder = MediaRecorder()
        val startRecordingButton = findViewById<Button>(R.id.startRecordingButton)
        val stopRecordingButton = findViewById<Button>(R.id.stopRecordingButton)

        startRecordingButton.isEnabled =false
        stopRecordingButton.isEnabled =false

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE),111)
            startRecordingButton.isEnabled =true

            startRecordingButton.setOnClickListener{
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder.setOutputFile(path)
                mediaRecorder.prepare()
                mediaRecorder.start()
                stopRecordingButton.isEnabled = true
                startRecordingButton.isEnabled = false
            }

            stopRecordingButton.setOnClickListener{
                mediaRecorder.stop()
                startRecordingButton.isEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val startRecordingButton = findViewById<Button>(R.id.startRecordingButton)
        startRecordingButton.isEnabled =false
        if (requestCode ==111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startRecordingButton.isEnabled =true

        }
    }
}
