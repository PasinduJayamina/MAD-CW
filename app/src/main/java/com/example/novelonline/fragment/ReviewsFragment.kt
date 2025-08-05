package com.example.novelonline.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novelonline.adapters.ReviewAdapter
import com.example.novelonline.databinding.FragmentReviewsBinding
import com.example.novelonline.models.Review
import java.util.Date

class ReviewsFragment : Fragment() {

    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Handle back button click
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Handle submit review button click
        binding.submitReviewButton.setOnClickListener {
            val rating = binding.addReviewRatingBar.rating
            val reviewText = binding.addReviewEditText.text.toString()

            if (rating == 0f) {
                Toast.makeText(context, "Please add a rating.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (reviewText.isBlank()) {
                Toast.makeText(context, "Please write a review.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Submit the review to your database (e.g., Firebase)
            Toast.makeText(context, "Review submitted! Rating: $rating", Toast.LENGTH_LONG).show()

            // Clear the input fields after submission
            binding.addReviewRatingBar.rating = 0f
            binding.addReviewEditText.text?.clear()
        }
    }

    private fun setupRecyclerView() {
        // Create dummy data for now. Later, you will get this from your database.
        val dummyReviews = createDummyReviews()

        val reviewAdapter = ReviewAdapter(dummyReviews)
        binding.reviewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }
    }

    private fun createDummyReviews(): List<Review> {
        return listOf(
            Review("1", "1", "Pasindu Udara", "", 5.0f, "Best novel on india category, thanks for such good story", Date()),
            Review("2", "1", "Jane Doe", "", 4.0f, "A really enjoyable read with great characters. The middle was a bit slow, but the ending was fantastic.", Date()),
            Review("3", "1", "John Smith", "", 4.5f, "I couldn't put it down! Highly recommended for anyone who loves historical fiction.", Date()),
            Review("4", "1", "Emily White", "", 3.0f, "It was okay. Not my favorite, but not bad either. The plot felt a little predictable.", Date())
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
