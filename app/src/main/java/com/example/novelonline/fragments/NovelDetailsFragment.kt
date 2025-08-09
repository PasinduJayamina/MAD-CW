package com.example.novelonline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.database.AppDatabase
import com.example.novelonline.database.OfflineBook
import com.example.novelonline.database.toOfflineBook
import com.example.novelonline.databinding.FragmentNovelDetailsBinding
import com.example.novelonline.models.Book
import com.example.novelonline.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class NovelDetailsFragment : Fragment() {

    private var _binding: FragmentNovelDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: NovelDetailsFragmentArgs by navArgs()
    private var currentBook: Book? = null

    // Initialize the Room database
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNovelDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the database instance
        database = AppDatabase.getDatabase(requireContext())

        val novelId = args.novelId
        fetchNovelDetails(novelId)

        setupNavigation(novelId)

        // Set up the download button click listener
        binding.downloadButton.setOnClickListener {
            currentBook?.let { book ->
                if (book.pdfUrl.isNotEmpty()) {
                    downloadBookForOfflineReading(book)
                } else {
                    Toast.makeText(context, "No downloadable file found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupNavigation(novelId: String) {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.readNowButton.setOnClickListener {
            currentBook?.let { book ->
                viewLifecycleOwner.lifecycleScope.launch {
                    // Check if the book is already downloaded
                    val offlineBook = database.offlineBookDao().getOfflineBook(book.id)
                    if (offlineBook != null) {
                        // Open the local PDF file
                        val action = NovelDetailsFragmentDirections.actionNovelDetailsToReadChaptersFragment(offlineBook.localFilePath)
                        findNavController().navigate(action)
                    } else {
                        // Open the online PDF
                        if (book.pdfUrl.isNotEmpty()) {
                            val action = NovelDetailsFragmentDirections.actionNovelDetailsToReadChaptersFragment(book.pdfUrl)
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(context, "No readable file found for this book.", Toast.LENGTH_SHORT).show()
                        }
                    }
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

    private fun downloadBookForOfflineReading(book: Book) {
        viewLifecycleOwner.lifecycleScope.launch {
            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
            try {
                // Download the file on an IO thread
                val localFile = withContext(Dispatchers.IO) {
                    val url = URL(book.pdfUrl)
                    val fileName = "${book.id}.pdf"
                    val file = File(requireContext().filesDir, fileName)
                    url.openStream().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    file
                }

                // Insert the downloaded book into the Room database
                val offlineBook = book.toOfflineBook(localFile.absolutePath)
                withContext(Dispatchers.IO) {
                    database.offlineBookDao().insert(offlineBook)
                }

                Toast.makeText(context, "Download successful! Book saved for offline reading.", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e("Download", "Download failed: ${e.message}", e)
                Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}