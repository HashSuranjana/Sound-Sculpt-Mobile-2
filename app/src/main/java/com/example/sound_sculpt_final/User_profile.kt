package com.example.sound_sculpt_final

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sound_sculpt_final.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth

class User_profile : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding // Binding object
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Get the current user from Firebase Authentication
        val currentUser = auth.currentUser

        // Check if user is logged in
        if (currentUser != null) {
            // Retrieve username from the currentUser object
            val username = currentUser.displayName

            // Display username in the appropriate view
            binding.welcomeMessageTextView.text = "Welcome, $username"
        } else {
            // User is not logged in, handle this case as needed
            // For example, you can redirect the user to the sign-in screen
        }
    }
}
