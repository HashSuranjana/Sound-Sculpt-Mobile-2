package com.example.sound_sculpt_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sound_sculpt_final.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this,Sign_in::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmpass = binding.confirmPassEt.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()){

                if(pass.equals(confirmpass)){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this,Sign_in::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please enter relevants", Toast.LENGTH_SHORT).show()
            }
        }
    }
}