package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // Import Toast for user feedback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentNovelDetailsBinding
import com.example.novelonline.models.Book
import com.example.novelonline.repository.BookRepository
import kotlinx.coroutines.launch

class NovelDetailsFragment : Fragment() {

    private var _binding: FragmentNovelDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: NovelDetailsFragmentArgs by navArgs()

    // 1. (CHANGED) Add a variable to hold the currently displayed book.
    private var currentBook: Book? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovelDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val novelId = args.novelId
        fetchNovelDetails(novelId)

        // This function now relies on the `currentBook` variable
        setupNavigation(novelId)
    }

    private fun setupNavigation(novelId: String) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // 2. (CHANGED) Update the click listener to use the real data.
        binding.readNowButton.setOnClickListener {
            // Use the pdfUrl from the 'currentBook' object
            currentBook?.pdfUrl?.let { pdfUrl ->
                if (pdfUrl.isNotEmpty()) {
                    val action = NovelDetailsFragmentDirections.actionNovelDetailsToReadChaptersFragment(pdfUrl)
                    findNavController().navigate(action)
                } else {
                    // Handle case where the book might not have a PDF
                    Toast.makeText(context, "No readable file found for this book.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.reviewsTitle.setOnClickListener {
            val action = NovelDetailsFragmentDirections.actionNovelDetailsToReviewFragment(novelId)
            findNavController().navigate(action)
        }
    }

    private fun fetchNovelDetails(id: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.detailsProgressBar.visibility = View.VISIBLE

            val book = BookRepository.getBookById(id)

            binding.detailsProgressBar.visibility = View.GONE

            if (book != null) {
                // 3. (CHANGED) Store the fetched book in our class variable.
                currentBook = book
                updateUi(book)
            } else {
                Toast.makeText(context, "Could not find book details.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUi(book: Book) {
        binding.titleTextView.text = book.title
        binding.authorTextView.text = "By ${book.author} >"
        binding.synopsisTextView.text = book.synopsis

        Glide.with(this)
            .load(book.coverImageUrl)
            .placeholder(R.drawable.baseline_book_placeholder_24)
            .error(R.drawable.baseline_error_outline_24)
            .into(binding.coverImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}