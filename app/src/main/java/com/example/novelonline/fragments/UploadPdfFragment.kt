package com.example.novelonline.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.databinding.FragmentUploadPdfBinding

class UploadPdfFragment : Fragment() {

    private var _binding: FragmentUploadPdfBinding? = null
    private val binding get() = _binding!!

    // ActivityResultLauncher to handle the PDF file selection
    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pdfUri: Uri? = result.data?.data
            if (pdfUri != null) {
                // Here you would handle the URI of the selected PDF
                // For now, we'll just show a success message
                Toast.makeText(
                    requireContext(),
                    "PDF selected: ${pdfUri.path}",
                    Toast.LENGTH_LONG
                ).show()

                // TODO: You would now proceed to upload the PDF or
                // store the URI to be used later.
            }
        } else {
            Toast.makeText(
                requireContext(),
                "PDF selection cancelled.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button to navigate back
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Upload PDF button to open the file picker
        binding.uploadPdfButton.setOnClickListener {
            openPdfFilePicker()
        }

        // Complete Book Info button
        binding.completeBookInfoButton.setOnClickListener {
            // TODO: Navigate to the next screen or show a message
            Toast.makeText(
                requireContext(),
                "Navigate to complete book info screen.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openPdfFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        try {
            pdfPickerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "No app available to handle PDF selection.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}