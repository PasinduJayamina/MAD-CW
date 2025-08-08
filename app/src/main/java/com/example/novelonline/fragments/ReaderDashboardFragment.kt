package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentReaderDashboardBinding

class ReaderDashboardFragment : Fragment() {

    private var _binding: FragmentReaderDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the click listeners for navigation
        setupClickListeners()
        // Here you would also load the user's name and profile picture from your database
        loadUserProfile()
    }

    private fun setupClickListeners() {
        // Navigate to the "Become a Writer" screen when the card is clicked
        binding.becomeWriterCard.setOnClickListener {
            // Assuming this action is defined in your nav_graph.xml
            findNavController().navigate(R.id.action_readerDashboardFragment_to_becomeWriterFragment)
        }

        // Navigate to the "Your Works" screen when the card is clicked
        binding.yourWorksCard.setOnClickListener {
            // Assuming this action is defined in your nav_graph.xml
            findNavController().navigate(R.id.action_readerDashboardFragment_to_yourWorksFragment)
        }

        // Navigate to the "Book Fairs" screen
        binding.bookFairsCard.setOnClickListener {
            // Assuming you have a BookFairsFragment and this action is defined
            findNavController().navigate(R.id.action_readerDashboardFragment_to_bookFairsFragment)
        }

        // Handle the profile picture edit icon click
        binding.editProfileIcon.setOnClickListener {
            // Handle the logic to edit the profile picture (e.g., open a gallery or camera)
            // You might want to show a bottom sheet or a dialog here
        }
    }

    private fun loadUserProfile() {
        // TODO: Implement the logic to fetch and display user data from Firebase or another source.
        // binding.userNameTextView.text = "John Doe"
        // Glide.with(this).load("user_profile_url").into(binding.profilePictureImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}