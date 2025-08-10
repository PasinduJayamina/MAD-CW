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
import com.example.novelonline.databinding.FragmentReadChapterBinding
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.lang.Exception

class ReadChapterFragment : Fragment() {

    private var _binding: FragmentReadChapterBinding? = null
    private val binding get() = _binding!!

    private val args: ReadChapterFragmentArgs by navArgs()

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

        val pdfPathOrUrl = args.pdfUrl

        if (pdfPathOrUrl.isNotEmpty()) {
            binding.bookTitleTextView.text = "Reading Chapter"

            // Check if the string is a local file path
            val file = File(pdfPathOrUrl)
            if (file.exists() && file.length() > 0) {
                Log.d("ReadChapterFragment", "File exists, loading from local path: ${file.absolutePath}")
                loadLocalPdf(file)
            } else {
                Log.d("ReadChapterFragment", "File does not exist locally. Assuming remote URL: $pdfPathOrUrl")
                loadRemotePdf(pdfPathOrUrl)
            }
        } else {
            Toast.makeText(requireContext(), "Book not found: PDF URL is missing", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun loadLocalPdf(file: File) {
        binding.pdfView.fromFile(file)
            .onLoad { pageCount ->
                Toast.makeText(requireContext(), "PDF Loaded, page count: $pageCount", Toast.LENGTH_SHORT).show()
            }
            .onError { t ->
                Toast.makeText(requireContext(), "Error loading local PDF: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("ReadChapterFragment", "Error loading local PDF", t)
            }
            .load()
    }

    private fun loadRemotePdf(pdfUrl: String) {
        // Use a coroutine to handle the network operation
        lifecycleScope.launch {
            try {
                Log.d("ReadChapterFragment", "Attempting to download remote PDF from: $pdfUrl")
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
                        Log.e("ReadChapterFragment", "Error loading remote PDF", t)
                    }
                    .load()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to download PDF: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("ReadChapterFragment", "Failed to download PDF", e)
                findNavController().popBackStack() // Go back if download fails
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
