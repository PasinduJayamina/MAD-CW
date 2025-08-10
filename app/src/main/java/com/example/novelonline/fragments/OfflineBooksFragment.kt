package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.novelonline.adapters.OfflineBookAdapter
import com.example.novelonline.database.AppDatabase
import com.example.novelonline.databinding.FragmentOfflineBooksBinding
import com.example.novelonline.database.OfflineBook // Make sure this is the correct import for your OfflineBook model
import kotlinx.coroutines.launch

class OfflineBooksFragment : Fragment() {

    private var _binding: FragmentOfflineBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var offlineBookAdapter: OfflineBookAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database
        database = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        loadOfflineBooks()
    }

    private fun setupRecyclerView() {
        offlineBookAdapter = OfflineBookAdapter { offlineBook ->
            // Navigate to the ReadChapterFragment, passing the local file path
            val action = OfflineBooksFragmentDirections.actionOfflineBooksFragmentToReadChaptersFragment(offlineBook.localFilePath)
            findNavController().navigate(action)
        }
        binding.recyclerViewOfflineBooks.adapter = offlineBookAdapter
    }

    private fun loadOfflineBooks() {
        binding.progressBarLoading.visibility = View.VISIBLE
        binding.recyclerViewOfflineBooks.visibility = View.GONE
        binding.textViewNoBooks.visibility = View.GONE

        lifecycleScope.launch {
            val offlineBooks = database.offlineBookDao().getAllOfflineBooks()

            binding.progressBarLoading.visibility = View.GONE
            offlineBookAdapter.submitList(offlineBooks)

            if (offlineBooks.isEmpty()) {
                binding.textViewNoBooks.visibility = View.VISIBLE
                binding.recyclerViewOfflineBooks.visibility = View.GONE
            } else {
                binding.textViewNoBooks.visibility = View.GONE
                binding.recyclerViewOfflineBooks.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}