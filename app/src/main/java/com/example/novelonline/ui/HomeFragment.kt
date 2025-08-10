package com.example.novelonline.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.novelonline.adapters.BookAdapter
import com.example.novelonline.databinding.FragmentHomeBinding
import com.example.novelonline.repository.BookRepository
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.novelonline.models.Book

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var featuredBooksAdapter: BookAdapter
    private lateinit var topRankedAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        loadBooksData()
    }

    private fun setupRecyclerViews() {
        // 1. Define the click listener logic once to avoid repeating code.
        val onBookClick: (Book) -> Unit = { book ->
            // 2. Get the book's ID from your Book data model.
            val novelId = book.id

            // 3. Create the navigation action using the generated Directions class.
            val action = HomeFragmentDirections.actionHomeFragmentToNovelDetailsFragment(novelId)

            // 4. Use the NavController to perform the navigation.
            findNavController().navigate(action)
        }

        // 5. Pass the same click listener to both of your adapters.
        featuredBooksAdapter = BookAdapter(BookAdapter.VIEW_TYPE_HORIZONTAL, onBookClick)
        binding.rvFeaturedBooks.adapter = featuredBooksAdapter

        topRankedAdapter = BookAdapter(BookAdapter.VIEW_TYPE_VERTICAL, onBookClick)
        binding.rvTopRankedBooks.adapter = topRankedAdapter
    }

    private fun loadBooksData() {
        binding.homeProgressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            val books = BookRepository.getBooks()
            binding.homeProgressBar.visibility = View.GONE

            if (isAdded && books.isNotEmpty()) {
                featuredBooksAdapter.submitList(books)
                topRankedAdapter.submitList(books)
            } else if (isAdded) {
                Toast.makeText(context, "Failed to load books.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}