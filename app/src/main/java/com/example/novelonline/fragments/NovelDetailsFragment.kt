package com.example.novelonline.fragments // Adjust package as needed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.databinding.FragmentNovelDetailsBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.novelonline.R

class NovelDetailsFragment : Fragment() {

    private var _binding: FragmentNovelDetailsBinding? = null
    private val binding get() = _binding!!

    private var novelId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the novelId from arguments
        arguments?.let {
            novelId = it.getString(ARG_NOVEL_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovelDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listener for the back button
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Set up click listener for the Read Now button
        binding.readNowButton.setOnClickListener {
            // TODO: Navigate to the reading screen, passing the novel ID
            // Example:
            // val action = NovelDetailsFragmentDirections.actionNovelDetailsFragmentToReadingFragment(novelId)
            // findNavController().navigate(action)
        }

        // Set up click listener for the Reviews section (e.g., the title)
        binding.reviewsTitle.setOnClickListener {
            // TODO: Navigate to the ReviewsFragment, passing the novel ID
            // Example:
            // val action = NovelDetailsFragmentDirections.actionNovelDetailsFragmentToReviewsFragment(novelId)
            // findNavController().navigate(action)
        }

        // TODO: Implement logic to load novel details based on novelId
        // fetchNovelDetails(novelId)
    }

    // Example function to load novel details (you would replace this with your actual data fetching logic)
    private fun fetchNovelDetails(id: String?) {
        if (id == null) return

        // TODO: Fetch novel data from a repository or database (e.g., Firebase)
        // For demonstration, we'll use placeholder data
        val novelTitle = "A New India"
        val authorName = "clautic"
        val coverImageUrl = "https://example.com/novel_cover.jpg" // Replace with a real URL
        val globalRank = 3
        val views = "1.2 M"

        // Update UI with the fetched data
        binding.titleTextView.text = novelTitle
        binding.authorTextView.text = "By $authorName >"
        binding.rankTextView.text = globalRank.toString()
        binding.viewsTextView.text = views

        // Use Glide to load the cover image
        // Make sure to add the Glide dependency to your build.gradle file
        Glide.with(this)
            .load(coverImageUrl)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(16)))
            .placeholder(R.drawable.baseline_book_placeholder_24) // Add a placeholder
            .error(R.drawable.baseline_error_outline_24) // Add an error drawable
            .into(binding.coverImageView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_NOVEL_ID = "novelId"

        // Factory method to create a new instance of this fragment
        fun newInstance(novelId: String): NovelDetailsFragment {
            val fragment = NovelDetailsFragment()
            val args = Bundle().apply {
                putString(ARG_NOVEL_ID, novelId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}