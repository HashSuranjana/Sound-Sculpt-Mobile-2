package com.example.sound_sculpt_final

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SaveFile : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var selectedDevice: String
    lateinit var userId: String
    lateinit var max7DecibelArray: FloatArray
    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_file)

        // Initialize Firebase components and get current user's ID
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Initialize UI elements
        val audioDeviceSpinner = findViewById<Spinner>(R.id.audioDeviceSpinner)
        val audioDeviceList = listOf("Laptop", "Bookshelf", "Desktop", "Portable", "Sound Bar", "Out Door", "Floor Standing" )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, audioDeviceList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        audioDeviceSpinner.adapter = adapter

        // Retrieve max decibel values from intent
        max7DecibelArray = intent.getFloatArrayExtra("dBValues") ?: floatArrayOf()

        // Initialize upload button and set click listener
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            selectedDevice = audioDeviceSpinner.selectedItem.toString()
            showProgressBar()
            saveToDevice()
        }
    }

    // Function to save selected device and max decibel values to Firebase
    fun saveToDevice() {
        if (userId.isEmpty() || max7DecibelArray.isEmpty()) {
            showToast("User ID or max decibel values not found")
            return
        }

        // Calculate the difference based on the third index of max7DecibelArray
        val thirdIndexValue = max7DecibelArray.getOrNull(4) ?: 0f
        val dbArray = FloatArray(max7DecibelArray.size) { index ->
            if (index == 4) {
                0f
            } else {
                thirdIndexValue - max7DecibelArray[index]
            }
        }

        // Get a reference to the Firebase Realtime Database
        val database = Firebase.database

        // Reference to the users node in the database
        val usersRef = database.reference.child("users")

        // Create a new child node with the userId as the key and set its value to selectedDevice and dbArray
        val userData = hashMapOf(
            "speakerType" to selectedDevice,
            "dBValues" to dbArray.joinToString(", ")
        )

        // Set value in the database
        usersRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                // Data saved successfully
                showToast("Device and Max Decibel Values saved successfully")
                hideProgressBar()
                finish()
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful upload
                hideProgressBar()
                showToast("Failed to save device and max decibel values: ${exception.message}")
            }
    }

    // Function to display toast messages
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to show progress bar dialog
    private fun showProgressBar(){
        dialog = Dialog(this@SaveFile)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    // Function to hide progress bar dialog
    private fun hideProgressBar(){
        dialog.dismiss()
    }
}
