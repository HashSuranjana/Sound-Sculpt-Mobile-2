import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sound_sculpt_final.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()




        // Example usage: saving profile information
        saveProfile("John Doe", "path/to/audio/clip")
    }

    private fun saveProfile(name: String, audioClipUri: String) {
        val userId = auth.currentUser?.uid

        userId?.let {
            // Save profile information to Firestore
            val user = hashMapOf(
                "name" to name,
                "audioClipUri" to audioClipUri,

                // Add other profile information here if needed
            )

            firestore.collection("users").document(it)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving profile: $e", Toast.LENGTH_SHORT).show()
                }

            // Upload audio clip to Firebase Storage
            val storageRef = storage.reference
            val audioClipRef = storageRef.child("audioClips/$userId/audio.mp3")

            // Replace the following with actual audio file upload logic
            // For example:
            // val file = Uri.fromFile(File(audioClipUri))
            // audioClipRef.putFile(file)
            //     .addOnSuccessListener { /* Handle successful upload */ }
            //     .addOnFailureListener { /* Handle failed upload */ }
        }
    }
}
