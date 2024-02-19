import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sound_sculpt_final.R

class Home : Fragment() {

    private lateinit var connectButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        connectButton = view.findViewById(R.id.button)
        connectButton.setOnClickListener {
            connectToPC()
        }
        return view
    }

    private fun connectToPC() {
        // Enable Wi-Fi
        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true

        // Now you can add code here to execute adb commands to connect to your PC via Wi-Fi
        // For example, you can execute adb commands using ProcessBuilder:

        val processBuilder = ProcessBuilder()
        processBuilder.command("adb", "connect", "your.pc.ip.address")

        try {
            val process = processBuilder.start()
            // Check the exit value of the process
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                // Connection successful, navigate to the "files" fragment
                findNavController().navigate(R.id.action_home_to_files)
            } else {
                // Connection failed, show an error message
                // You can display an error message using Toast or Snackbar
                // For example:
                // Toast.makeText(requireContext(), "Connection failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Connection failed, show an error message
            // For example:
            // Toast.makeText(requireContext(), "Connection failed", Toast.LENGTH_SHORT).show()
        }
    }
}
