package com.example.novelonline.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.novelonline.adapters.OfflineBookAdapter
import com.example.novelonline.database.AppDatabase
import com.example.novelonline.databinding.FragmentLibraryBinding
import com.example.novelonline.database.OfflineBook
import kotlinx.coroutines.launch

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var libraryBookAdapter: OfflineBookAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        database = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        loadLibraryBooks()
    }

    private fun setupRecyclerView() {
        libraryBookAdapter = OfflineBookAdapter { offlineBook ->
            // Navigate to the ReadChapterFragment, passing the local file path
            val action = LibraryFragmentDirections.actionLibraryFragmentToNovelDetailsFragment(offlineBook.localFilePath)
            findNavController().navigate(action)
        }
        binding.recyclerViewLibraryBooks.adapter = libraryBookAdapter
    }

    private fun loadLibraryBooks() {
        binding.progressBarLoading.visibility = View.VISIBLE
        binding.recyclerViewLibraryBooks.visibility = View.GONE
        binding.textViewNoLibraryBooks.visibility = View.GONE

        lifecycleScope.launch {
            val offlineBooks = database.offlineBookDao().getAllOfflineBooks()

            binding.progressBarLoading.visibility = View.GONE
            libraryBookAdapter.submitList(offlineBooks)

            if (offlineBooks.isEmpty()) {
                binding.textViewNoLibraryBooks.visibility = View.VISIBLE
                binding.recyclerViewLibraryBooks.visibility = View.GONE
            } else {
                binding.textViewNoLibraryBooks.visibility = View.GONE
                binding.recyclerViewLibraryBooks.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}