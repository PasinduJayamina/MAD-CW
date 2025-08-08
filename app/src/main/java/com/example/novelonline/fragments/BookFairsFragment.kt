package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.novelonline.adapters.BookFairsAdapter
import com.example.novelonline.databinding.FragmentBookFairsBinding
import com.example.novelonline.models.BookFair
import androidx.navigation.fragment.findNavController

class BookFairsFragment : Fragment() {

    private var _binding: FragmentBookFairsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookFairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the click listener for the back button
        binding.backButton.setOnClickListener {
            // Use the Navigation Component to pop the back stack
            findNavController().popBackStack()
        }

        // Create some dummy data for book fairs in Sri Lanka
        val bookFairs = listOf(
            BookFair(
                "Colombo International Book Fair",
                "Bandaranaike Memorial International Conference Hall (BMICH), Colombo 07",
                "+94 11 254 6241",
                "2025-08-01",
                "2025-08-10"
            ),
            BookFair(
                "Kandy Book Fair",
                "Kandy City Centre, Kandy",
                "+94 81 223 5432",
                "2025-09-05",
                "2025-09-12"
            ),
            BookFair(
                "Jaffna Book Fair",
                "Weerasingham Hall, Jaffna",
                "+94 21 222 3344",
                "2025-10-15",
                "2025-10-20"
            )
        )

        val adapter = BookFairsAdapter(bookFairs) { bookFair ->
            // Handle the click event for a book fair card
            // For now, we'll just show a Toast, but you could navigate to a details screen
            Toast.makeText(requireContext(), "Clicked on ${bookFair.name}", Toast.LENGTH_SHORT)
                .show()
        }

        binding.bookFairsRecyclerView.adapter = adapter
    }

    // The onDestroyView() method must be a top-level method of the fragment class,
    // not nested inside another function.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}