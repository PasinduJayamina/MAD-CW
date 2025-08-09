package com.example.novelonline.ui

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GenreDetailFragment : Fragment() {

    private var _binding: FragmentGenreDetailBinding? = null
    private val binding get() = _binding!!

    private val args: GenreDetailFragmentArgs by navArgs()
    private lateinit var bookAdapter: BookAdapter

    // Firestore instance
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenreDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = Firebase.firestore

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
        binding.progressBar.visibility = View.VISIBLE
        binding.rvGenreBooks.visibility = View.GONE

        // Query Firestore for books where the 'genre' field is equal to the genreName.
        // This assumes 'genre' is a single string field.
        firestore.collection("books")
            .whereEqualTo("genre", genreName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                binding.progressBar.visibility = View.GONE
                binding.rvGenreBooks.visibility = View.VISIBLE

                val booksForGenre = mutableListOf<Book>()
                for (document in querySnapshot.documents) {
                    val book = document.toObject(Book::class.java)
                    book?.let {
                        booksForGenre.add(it)
                    }
                }
                bookAdapter.submitList(booksForGenre)

                if (booksForGenre.isEmpty()) {
                    binding.tvNoBooksMessage.visibility = View.VISIBLE
                } else {
                    binding.tvNoBooksMessage.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Log.e("GenreDetailFragment", "Error getting books for genre: $genreName", exception)
                Toast.makeText(context, "Error loading books: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}