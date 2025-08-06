package com.example.novelonline.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.novelonline.adapters.BookAdapter
import com.example.novelonline.databinding.FragmentGenreDetailBinding
import com.example.novelonline.models.Book

class GenreDetailFragment : Fragment() {

    private var _binding: FragmentGenreDetailBinding? = null
    private val binding get() = _binding!!

    // Use the 'by navArgs()' delegate to get the arguments passed from the previous fragment.
    private val args: GenreDetailFragmentArgs by navArgs()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title from the passed argument
        binding.tvGenreTitle.text = args.genreName

        setupRecyclerView()
        loadBooksForGenre(args.genreName)

        // Handle back navigation
        binding.ivBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(BookAdapter.VIEW_TYPE_GRID) { book ->
            // TODO: Navigate to book detail screen (Task for member GAHDSE242F-012)
            Toast.makeText(context, "Clicked on ${book.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvGenreBooks.adapter = bookAdapter
    }

    private fun loadBooksForGenre(genreName: String) {
        // TODO: This is where you would query Firestore for books with the matching genre.
        // Using placeholder data for now.
        Toast.makeText(context, "Loading books for $genreName", Toast.LENGTH_LONG).show()

        val allBooks = listOf(
            Book("101", "The Dragon's Legacy", "Nia Quinn", "https://placehold.co/300x440/042f2e/ffffff?text=Fantasy+1"),
            Book("102", "Whispers of the Ancient", "R.L. Thorne", "https://placehold.co/300x440/064e3b/ffffff?text=Fantasy+2"),
            Book("103", "The Shadow Weaver's Secret", "Elara Vance", "https://placehold.co/300x440/134e4a/ffffff?text=Fantasy+3"),
            Book("104", "Echoes of the Forgotten", "Jaxon Reed", "https://placehold.co/300x440/115e59/ffffff?text=Fantasy+4")
        )

        // In a real app, you would filter this list based on the genreName.
        // For this example, we just show the same list for any genre.
        bookAdapter.submitList(allBooks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}