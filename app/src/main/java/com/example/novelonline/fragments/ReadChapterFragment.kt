package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.novelonline.databinding.FragmentReadChapterBinding
import com.github.barteksc.pdfviewer.PDFView
import java.io.InputStream
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadChapterFragment : Fragment() {

    private var _binding: FragmentReadChapterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        val pdfUrl = arguments?.getString("pdfUrl")

        if (pdfUrl != null) {
            binding.bookTitleTextView.text = "Reading Chapter"

            // Use a coroutine to handle the network operation
            lifecycleScope.launch {
                try {
                    Toast.makeText(requireContext(), "Downloading PDF...", Toast.LENGTH_SHORT).show()

                    // Download the PDF into a ByteArray on a background thread
                    val pdfBytes = withContext(Dispatchers.IO) {
                        URL(pdfUrl).readBytes()
                    }

                    // Load the PDF from the ByteArray into the view on the main thread
                    binding.pdfView.fromBytes(pdfBytes)
                        .onLoad { pageCount ->
                            Toast.makeText(requireContext(), "PDF Loaded, page count: $pageCount", Toast.LENGTH_SHORT).show()
                        }
                        .onError { t ->
                            Toast.makeText(requireContext(), "Error loading PDF: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                        .load()

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to download PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Book not found: PDF URL is missing", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}