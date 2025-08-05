package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentNovelDetailsBinding
import com.example.novelonline.models.Book
import com.example.novelonline.models.Chapter

// Note: This fragment no longer uses the ChapterAdapter since the new design
// doesn't show a chapter list. We will navigate to the chapters from the "Read now" button.

class NovelDetailsFragment : Fragment() {

    // The binding object that gives us access to all the views in our layout.
    private var _binding: FragmentNovelDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding.
        _binding = FragmentNovelDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For now, we'll use dummy data. Later, you'll fetch this from a database.
        val dummyBook = createDummyBook()
        populateUi(dummyBook)

        setupClickListeners()
    }

    // This function sets up all the button clicks.
    private fun setupClickListeners() {
        // Handle the back button click.
        binding.backButton.setOnClickListener {
            findNavController().popBackStack() // Navigates to the previous screen.
        }

        // Handle the "Read now" button click.
        binding.readNowButton.setOnClickListener {
            // TODO: Navigate to the ReaderFragment.
            // For now, we'll just show a message.
            Toast.makeText(context, "Navigating to reader...", Toast.LENGTH_SHORT).show()
        }

        // Handle the download button click.
        binding.downloadButton.setOnClickListener {
            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
        }

        // Handle the "add to library" button click.
        binding.addToLibraryButton.setOnClickListener {
            Toast.makeText(context, "Added to library!", Toast.LENGTH_SHORT).show()
        }
    }

    // This function fills the UI with the book's data.
    private fun populateUi(book: Book) {
        binding.titleTextView.text = book.title
        binding.authorTextView.text = book.author
        binding.viewsTextView.text = "1.2 M" // Example static data
        binding.rankTextView.text = "3" // Example static data

        // TODO: Use a library like Glide or Coil to load the image from book.coverImageUrl
        // For now, we'll just set a placeholder background.
        binding.coverImageView.setImageResource(R.drawable.ic_launcher_background)
    }

    // This function creates some fake data to test the UI.
    private fun createDummyBook(): Book {
        // Since the new design doesn't show chapters on this page,
        // we can create an empty list for now.
        return Book(
            id = "1",
            title = "A New India",
            author = "By clautic >",
            coverImageUrl = "url_to_image",
            summary = "Best novel on india category, thanks for such good story.",
            tags = listOf("Novel", "History"),
            chapters = emptyList() // No chapter list on this screen
        )
    }

    // It's important to null out the binding when the view is destroyed to avoid memory leaks.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
