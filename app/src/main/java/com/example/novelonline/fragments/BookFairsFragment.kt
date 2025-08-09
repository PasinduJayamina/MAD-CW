package com.example.novelonline.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.novelonline.BuildConfig
import com.example.novelonline.adapters.BookFairsAdapter
import com.example.novelonline.databinding.FragmentBookFairsBinding
import com.example.novelonline.models.BookFair
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//  Data classes for parsing the Directions API response
data class DirectionsResponse(val routes: List<Route>)
data class Route(val legs: List<Leg>)
data class Leg(val distance: Distance)
data class Distance(@SerializedName("text") val distanceText: String)

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}

class BookFairsFragment : Fragment() {

    private var _binding: FragmentBookFairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: BookFairsAdapter

    private val GOOGLE_MAPS_API_KEY = BuildConfig.MAPS_API_KEY

    private val bookFairs = mutableListOf(
        BookFair("Colombo International Book Fair", "BMICH, Colombo 07", "+94112546241", "2025-09-19", "2025-09-28", 6.9048, 79.8612),
        BookFair("Kandy Book Fair", "Kandy City Centre, Kandy", "+94812235432", "2025-09-05", "2025-09-12", 7.2906, 80.6337),
        BookFair("Jaffna Book Fair", "Weerasingham Hall, Jaffna", "+94212223344", "2025-10-15", "2025-10-20", 9.6615, 80.0255)
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getCurrentLocation()
            else Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookFairsBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        setupRecyclerView()
        checkLocationPermission()
    }

    private fun setupRecyclerView() {
        adapter = BookFairsAdapter(
            bookFairs,
            onItemClick = { bookFair ->
                Toast.makeText(requireContext(), "Clicked on ${bookFair.name}", Toast.LENGTH_SHORT).show()
            },
            onMapClick = { bookFair ->
                openMapForBookFair(bookFair)
            }
        )
        binding.bookFairsRecyclerView.adapter = adapter
    }

    private fun openMapForBookFair(bookFair: BookFair) {
        val gmmIntentUri = Uri.parse("geo:${bookFair.latitude},${bookFair.longitude}?q=${Uri.encode(bookFair.address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                calculateDistances(location)
            } else {
                Toast.makeText(requireContext(), "Could not get location. Please ensure GPS is enabled.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calculateDistances(userLocation: Location) {
        // Step 1: Calculate and immediately display the straight-line distance
        bookFairs.forEach { fair ->
            val fairLocation = Location("").apply {
                latitude = fair.latitude
                longitude = fair.longitude
            }
            fair.straightLineDistanceKm = userLocation.distanceTo(fairLocation) / 1000f
        }
        adapter.updateData(bookFairs) // Update UI with straight-line distance

        // Step 2: In the background, fetch the driving distance for each fair
        lifecycleScope.launch {
            bookFairs.forEachIndexed { index, fair ->
                try {
                    val drivingDistance = getDrivingDistance(userLocation, fair)
                    if (drivingDistance != null) {
                        bookFairs[index].drivingDistance = drivingDistance
                        adapter.notifyItemChanged(index)
                    }
                } catch (e: Exception) {
                    Log.e("BookFairsFragment", "Failed to get driving distance for ${fair.name}", e)
                }
            }
        }
    }

    private suspend fun getDrivingDistance(origin: Location, destination: BookFair): String? {
        if (GOOGLE_MAPS_API_KEY.isEmpty() || GOOGLE_MAPS_API_KEY == "YOUR_API_KEY_HERE") {
            Log.e("BookFairsFragment", "Google Maps API Key is not set in local.properties.")
            return null
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("[https://maps.googleapis.com/](https://maps.googleapis.com/)")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(DirectionsApiService::class.java)
        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"

        val response = service.getDirections(originStr, destinationStr, GOOGLE_MAPS_API_KEY)
        return response.routes.firstOrNull()?.legs?.firstOrNull()?.distance?.distanceText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}