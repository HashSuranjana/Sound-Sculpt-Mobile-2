package com.example.sound_sculpt_final

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sound_sculpt_final.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class Sign_in : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                // If either email or password is empty, set both TextInputLayout borders to red
                binding.emailLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
                binding.passwordLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        // Sign-in successful
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Sign-in failed
                        binding.passwordLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
                    }
                }
            }
        }

    }
}
