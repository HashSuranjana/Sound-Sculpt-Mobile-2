import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sound_sculpt_final.R
import com.example.sound_sculpt_final.Sign_in

class Settings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Sign Out Button
        val btnSignOut = view.findViewById<View>(R.id.btn_sign_out)
        btnSignOut.setOnClickListener {
            // Perform sign out action
            // For example, navigate to sign in page
            val intent = Intent(requireContext(), Sign_in::class.java)
            startActivity(intent)
            requireActivity().finish() // Close current activity (settings)
        }

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(resources.getColor(R.color.start_color), resources.getColor(R.color.end_color))
        )

        // Set the shape as rectangle (you can customize this if needed)
        gradientDrawable.shape = GradientDrawable.RECTANGLE

        // Set corner radius if needed
        gradientDrawable.cornerRadius = 8.dpToPx(requireContext()).toFloat()

        // Set the gradient drawable as the background of the button
        btnSignOut.background = gradientDrawable


        // Feedback Section
        val textFeedback = view.findViewById<View>(R.id.text_feedback)
        textFeedback.setOnClickListener {
            val faqUrl = "https://www.merriam-webster.com/dictionary/feedback"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(faqUrl))
            startActivity(browserIntent)
        }

        // Help and FAQ Section
        val textHelpFAQ = view.findViewById<View>(R.id.text_help_faq)
        textHelpFAQ.setOnClickListener {
            // Open the FAQ website
            val faqUrl = "https://www.helpshift.com/glossary/faq/"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(faqUrl))
            startActivity(browserIntent)
        }

        val textabout = view.findViewById<View>(R.id.text_about)
        textabout.setOnClickListener {
            // Open the FAQ website
            val faqUrl = "https://www.helpshift.com/glossary/faq/"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(faqUrl))
            startActivity(browserIntent)
        }

        return view
    }

    private fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }
}
