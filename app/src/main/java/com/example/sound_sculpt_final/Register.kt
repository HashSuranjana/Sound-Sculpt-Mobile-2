package com.example.sound_sculpt_final

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sound_sculpt_final.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.textView.setOnClickListener {
            val intent = Intent(this, Sign_in::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            // Set border color based on whether fields are empty
            if (username.isEmpty()) {
                binding.usernameLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.purple_700)
            } else {
                binding.usernameLayout.boxStrokeColor = Color.BLACK
            }

            if (email.isEmpty()) {
                binding.emailLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.purple_700)
            } else {
                binding.emailLayout.boxStrokeColor = Color.BLACK
            }

            if (pass.isEmpty()) {
                binding.passwordLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.purple_700)
            } else {
                binding.passwordLayout.boxStrokeColor = Color.BLACK
            }

            if (confirmPass.isEmpty()) {
                binding.confirmPasswordLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.purple_700)
            } else {
                binding.confirmPasswordLayout.boxStrokeColor = Color.BLACK
            }

            // Proceed with sign-up logic
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Your existing sign-up logic here
                    if (pass == confirmPass) {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = firebaseAuth.currentUser
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build()
                                    user?.updateProfile(profileUpdates)
                                        ?.addOnCompleteListener { profileTask ->
                                            if (profileTask.isSuccessful) {
                                                val userData = UserData(username)
                                                val uid = user.uid
                                                databaseReference.child(uid).setValue(userData)
                                                    .addOnCompleteListener { databaseTask ->
                                                        if (databaseTask.isSuccessful) {
                                                            Toast.makeText(
                                                                this,
                                                                "Registration Successful",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            val intent =
                                                                Intent(this, Sign_in::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        } else {
                                                            Toast.makeText(
                                                                this,
                                                                "Failed to store user data",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                binding.confirmPasswordLayout.boxStrokeColor =
                                                    ContextCompat.getColor(this, R.color.red)
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        }
    }
}
