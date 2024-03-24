// Sign_in activity

package com.example.sound_sculpt_final

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sound_sculpt_final.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Sign_in : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            val userId = user.uid // Retrieve the user ID
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("USER_ID", userId) // Pass user ID to MainActivity
                            startActivity(intent)
                        }
                    } else {
                        // Sign-in failed
                        binding.passwordLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
                        // You may also reset the border color to its original color after a certain duration if needed.
                    }
                }
            }
        }

        // Handle Google Sign-In button click
        binding.actionImage.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Handle the result of Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI accordingly
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // Authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    // Proceed with your app logic, such as navigating to the main activity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // Update UI accordingly or display a message
                }
            }
    }
}
