import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
}
