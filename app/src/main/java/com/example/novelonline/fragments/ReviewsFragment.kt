package com.example.novelonline.fragments // Adjust package as needed


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.novelonline.databinding.FragmentReviewsBinding

class ReviewsFragment : Fragment() {

    // Use view binding to access views in the layout
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the back button click listener
        binding.toolbar.findViewById<View>(com.example.novelonline.R.id.backButton).setOnClickListener {
            // Handle back navigation. You can pop the back stack.
            // For example:
            // findNavController().popBackStack()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Example: Set up the submit button click listener
        binding.submitReviewButton.setOnClickListener {
            val rating = binding.addReviewRatingBar.rating
            val reviewText = binding.addReviewEditText.text.toString()

            // TODO: Implement logic to submit the review to a database (e.g., Firebase)
            // You will need to get the user ID, book ID, etc., and send this data.
            // Example:
            // saveReviewToFirebase(rating, reviewText)
        }

        // TODO: Set up the RecyclerView for displaying reviews
        // This will require an adapter and a layout manager.
        // For example:
        // binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // binding.reviewsRecyclerView.adapter = ReviewsAdapter(reviewList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding when the view is destroyed to avoid memory leaks
        _binding = null
    }
}