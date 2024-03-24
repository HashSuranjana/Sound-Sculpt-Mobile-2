package com.example.sound_sculpt_final

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SaveFile : AppCompatActivity() {

    private lateinit var selectedDevice: String
    private lateinit var userId: String
    private lateinit var max7DecibelArray: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_file)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Set UserID to TextView
        val userIdTextView = findViewById<TextView>(R.id.userIdTextView)
        userIdTextView.text = "User ID: $userId"

        val audioDeviceSpinner = findViewById<Spinner>(R.id.audioDeviceSpinner)
        val audioDeviceList = listOf("Laptop", "Bookshelf", "Desktop")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, audioDeviceList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        audioDeviceSpinner.adapter = adapter

        // Retrieve max7DecibelArray from intent
        max7DecibelArray = intent.getFloatArrayExtra("dBValues") ?: floatArrayOf()

        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            selectedDevice = audioDeviceSpinner.selectedItem.toString()
            saveToDevice()
        }
    }

    private fun saveToDevice() {
        if (userId.isEmpty()) {
            showToast("User ID not found")
            return
        }

        if (max7DecibelArray.isEmpty()) {
            showToast("Max decibel values not found")
            return
        }

        // Get a reference to the Firebase Realtime Database
        val database = Firebase.database

        // Reference to the users node in the database
        val usersRef = database.reference.child("users")

        // Create a new child node with the userId as the key and set its value to selectedDevice and max7DecibelArray
        val userData = hashMapOf(
            "speakerType" to selectedDevice,
            "dBValues" to max7DecibelArray.joinToString(", ")
        )

        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                // Data saved successfully
                showToast("Device and Max Decibeaal Values saved successfully")
                finish()
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful upload
                showToast("Failed to save device and max decibel values: ${exception.message}")
            }
    }

    private fun showToast(message: String) {
        // Utility function to display toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
