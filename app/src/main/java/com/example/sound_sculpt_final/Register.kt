package com.example.sound_sculpt_final

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sound_sculpt_final.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val uid = firebaseAuth.currentUser?.uid // Retrieve current user's UID
        databaseReference = FirebaseDatabase.getInstance().getReference("Users") // Reference to 'Users' node in Firebase

        // Set OnClickListener for textView to navigate to Sign_in activity
        binding.textView.setOnClickListener {
            val intent = Intent(this, Sign_in::class.java)
            startActivity(intent)
        }

        // Set OnClickListener for button to handle registration process
        binding.button.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()


            // Check if all fields are filled
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty()) {
                val user1 = UserData(username) // Create UserData object with username

                // Check if user is already signed in (existing user)
                if (uid != null) {
                    // Update user's data in database
                    databaseReference.child(uid).setValue(user1)
                }
                // If not, proceed with registration process
                else if (pass == confirmPass) {
                    // Create user in Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(user1.toString()) // Set display name to UserData's toString() value (likely the username)
                                    .build()

                                // Update user profile with the provided display name
                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            // Username successfully updated in user's profile
                                            val userId = user.uid
                                            val userReference = database.reference.child("users").child(userId)
                                            userReference.child("username").setValue(user1) // Set username in Firebase database

                                            // Navigate to User_profile activity with username information
                                            val intent = Intent(this, User_profile::class.java).apply {
                                                putExtra("USERNAME", username)
                                            }
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Failed to update username",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                            } else {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter relevant information", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
