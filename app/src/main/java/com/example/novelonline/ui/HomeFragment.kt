package com.example.novelonline.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.novelonline.adapters.BookAdapter
import com.example.novelonline.databinding.FragmentHomeBinding
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
        loadData()
    }

    private fun setupRecyclerViews() {
        // Adapter for the horizontal featured books list
        featuredBooksAdapter = BookAdapter(BookAdapter.VIEW_TYPE_HORIZONTAL) { book ->
            // TODO: Navigate to book detail screen (Task for member GAHDSE242F-012)
            Toast.makeText(context, "Clicked on ${book.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvFeaturedBooks.adapter = featuredBooksAdapter

        // Adapter for the vertical top-ranked books list
        topRankedAdapter = BookAdapter(BookAdapter.VIEW_TYPE_VERTICAL) { book ->
            // TODO: Navigate to book detail screen (Task for member GAHDSE242F-012)
            Toast.makeText(context, "Clicked on ${book.title}", Toast.LENGTH_SHORT).show()
        }
        binding.rvTopRankedBooks.adapter = topRankedAdapter
    }

    private fun loadData() {
        // TODO: Replace this with your actual data fetching logic from Firestore.
        // This is placeholder data to make the UI work.
        val sampleFeaturedBooks = listOf(
            Book("1", "The Dragon's Ascent", "Amelia Stone", "https://placehold.co/300x400/1e293b/ffffff?text=FALARY"),
            Book("2", "Eternal Embrace", "Ethan Blackwood", "https://placehold.co/300x400/d1d5db/1f2937?text=Eternal"),
            Book("3", "Echoes of Tomorrow", "Olivia Reed", "https://placehold.co/300x400/0ea5e9/ffffff?text=SCI-FI")
        )
        featuredBooksAdapter.submitList(sampleFeaturedBooks)

        val sampleTopRankedBooks = listOf(
            Book("1", "The Dragon's Ascent", "By Amelia Stone", "https://placehold.co/160x240/1e293b/ffffff?text=FANTASY"),
            Book("2", "Eternal Embrace", "By Ethan Blackwood", "https://placehold.co/160x240/d1d5db/1f2937?text=ROMANCE"),
            Book("3", "Echoes of Tomorrow", "By Olivia Reed", "https://placehold.co/160x240/0ea5e9/ffffff?text=SCI-FI"),
            Book("4", "Whispers of the Void", "By Leo Vance", "https://placehold.co/160x240/4f46e5/ffffff?text=MYSTERY")
        )
        topRankedAdapter.submitList(sampleTopRankedBooks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}