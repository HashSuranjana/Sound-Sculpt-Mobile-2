import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.sound_sculpt_final.LinkFiles
import com.example.sound_sculpt_final.R

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val connectButton: Button = view.findViewById(R.id.button)

        // Create a drawable with a shape and stroke for the border
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.TRANSPARENT) // Set the background color to transparent
            setStroke(2.dpToPx(requireContext()), Color.parseColor("#6366AE")) // Set the border width and color
            cornerRadius = 8.dpToPx(requireContext()).toFloat() // Set corner radius
        }

        // Apply the drawable as the background
        connectButton.background = borderDrawable

        connectButton.setOnClickListener {
            // Call a function to attempt connection to PC via WiFi
            connectToPC()
        }

        return view
    }

    private fun connectToPC() {
        // If connection is successful, navigate user to LinkFiles activity
        val intent = Intent(activity, LinkFiles::class.java)
        startActivity(intent)
    }

    // Convert dp to px
    private fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }
}
