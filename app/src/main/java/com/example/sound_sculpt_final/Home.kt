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

        // Implement your logic to connect to PC via WiFi here

        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true

        // Now you can add code here to execute adb commands to connect to your PC via Wi-Fi
        // For example, you can execute adb commands using ProcessBuilder:

        val processBuilder = ProcessBuilder()
        processBuilder.command("adb", "connect", "your.pc.ip.address")

        try {
            processBuilder.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val connectionSuccessful = true // Example: Assume connection successful for now

        if (connectionSuccessful) {
            // If connection is successful, navigate user to LinkFiles activity
            val intent = Intent(activity, LinkFiles::class.java)
            startActivity(intent)
        } else {
            // If connection is unsuccessful, display an error message
            Toast.makeText(activity, "Failed to connect to PC", Toast.LENGTH_SHORT).show()
        }
    }
}
