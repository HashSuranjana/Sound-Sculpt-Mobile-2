package com.example.sound_sculpt_final

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sound_sculpt_final.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class User_profile : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding // Binding object
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View ? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Listen for user registration completion
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val username = user.displayName
                if (username != null) {
                    // Save user data including username in Firebase Realtime Database
                    val userData = HashMap<String, String>()
                    userData["username"] = username
                    // Add other user data as needed
                    databaseReference.child(uid!!).setValue(userData)
                        .addOnSuccessListener {
                            // Data saved successfully
                            // Print username
                            binding.welcomeMessageTextView.text = "Welcome, $username"
                        }
                        .addOnFailureListener {
                            // Failed to save data
                            // Handle failure as needed
                        }
                }
            }
        }
    }
}
