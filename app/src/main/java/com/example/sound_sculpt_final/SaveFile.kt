package com.example.sound_sculpt_final

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sound_sculpt_final.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.json.JSONArray
import java.io.File

class SaveFile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_save_file)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the file path from intent
//        val filePath = intent.getStringExtra("file_path")
//        if (filePath != null) {
////            handleJSONFile(filePath)
//        }

        // Initialize spinner with dummy data (replace with actual audio device list)
        val audioDeviceSpinner = findViewById<Spinner>(R.id.audioDeviceSpinner)
        val audioDeviceList = listOf("Device 1", "Device 2", "Device 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, audioDeviceList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        audioDeviceSpinner.adapter = adapter

        // Upload JSON button click listener
//        val uploadButton = findViewById<Button>(R.id.uploadButton)
//        uploadButton.setOnClickListener {
//            if (filePath != null) {
//                uploadJSONToDatabase(filePath)
//            }
//        }
    }

//    private fun handleJSONFile(filePath: String) {
//        // Read JSON file and handle the data
//        val file = File(filePath)
//        try {
//            val jsonString = file.readText()
//            val jsonArray = JSONArray(jsonString)
//            // Process jsonArray as needed
//            // Example: Display the JSON array in log
//            Log.d("JSON Data", jsonArray.toString())
//        } catch (e: Exception) {
//            Log.e("Error", "Error reading JSON file: ${e.message}")
//        }
//    }

//    private fun uploadJSONToDatabase(filePath: String) {
//        // Upload JSON file to database (replace with your database upload logic)
//        val storage = Firebase.storage
//        val storageRef = storage.reference
//        val file = Uri.fromFile(File(filePath))
//
//        val jsonRef = storageRef.child("json_files/${file.lastPathSegment}")
//        jsonRef.putFile(file)
//            .addOnSuccessListener {
//                // File uploaded successfully
//                Toast.makeText(this, "JSON file uploaded successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { exception ->
//                // Handle unsuccessful uploads
//                Toast.makeText(this, "Upload failed: $exception", Toast.LENGTH_SHORT).show()
//            }
//    }
}
