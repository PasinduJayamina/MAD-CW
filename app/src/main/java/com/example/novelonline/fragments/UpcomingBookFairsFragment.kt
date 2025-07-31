import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.novelonline.R

class UpcomingBookFairsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_book_fairs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapPreview: ImageView = view.findViewById(R.id.map_preview)
        val backArrow: TextView = view.findViewById(R.id.back_arrow)

        // Handle the map click
        mapPreview.setOnClickListener {
            // Coordinates for Hall de Galle
            val latitude = 6.0251
            val longitude = 80.2194
            val locationName = "Hall de Galle"

            // Create a Uri to show a map with a marker
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($locationName)")

            // Create an Intent to view the Uri
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Ensures it opens in Google Maps

            // Verify that Google Maps is installed before launching
            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_LONG).show()
            }
        }

        // Handle the back arrow click
        backArrow.setOnClickListener {
            // Go back to the previous screen
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}