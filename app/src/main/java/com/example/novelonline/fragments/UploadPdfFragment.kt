package com.example.novelonline.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.novelonline.databinding.FragmentUploadPdfBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class UploadPdfFragment : Fragment() {

    private var _binding: FragmentUploadPdfBinding? = null
    private val binding get() = _binding!!

    // Firebase instances
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Novel ID received via Safe Args
    private var novelId: String? = null

    // Class-level variable to hold the URI of the selected PDF
    private var selectedPdfUri: Uri? = null

    // ActivityResultLauncher to handle the PDF file selection
    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedPdfUri = result.data?.data
            if (selectedPdfUri != null) {
                // Display the selected file name to the user
                binding.pdfStatusText.text = "PDF selected: ${selectedPdfUri!!.lastPathSegment}"
                binding.completeBookInfoButton.isEnabled = true
            }
        } else {
            Toast.makeText(requireContext(), "PDF selection cancelled.", Toast.LENGTH_SHORT).show()
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

        // Initialize Firebase instances
        storage = Firebase.storage
        firestore = Firebase.firestore
        auth = Firebase.auth

        // Retrieve the novelId from arguments using Safe Args
        val args: UploadPdfFragmentArgs by navArgs()
        novelId = args.novelId.takeIf { it != "null" }

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
            selectedPdfUri?.let { uri ->
                // Start the process of uploading the PDF
                uploadPdfToStorage(uri)
            } ?: run {
                Toast.makeText(requireContext(), "Please select a PDF file first.", Toast.LENGTH_SHORT).show()
            }
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

    private fun uploadPdfToStorage(pdfUri: Uri) {
        // Show a loading or uploading state
        binding.pdfStatusText.text = "Uploading PDF..."
        binding.completeBookInfoButton.isEnabled = false

        // Get the current user's ID
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "You must be signed in to upload a book.", Toast.LENGTH_SHORT).show()
            binding.pdfStatusText.text = "Please sign in."
            binding.completeBookInfoButton.isEnabled = true
            return
        }

        // Check if novelId is null. If so, create a new one.
        if (novelId == null) {
            // -- CHANGED: New document is created in the "books" collection
            val newBookRef = firestore.collection("books").document()
            novelId = newBookRef.id
            // Save a placeholder to the new document, including the authorId
            newBookRef.set(mapOf("title" to "Untitled Novel", "authorId" to userId))
                .addOnSuccessListener {
                    Log.d("UploadPdfFragment", "New novelId created: $novelId with authorId: $userId")
                    performPdfUpload(pdfUri, novelId!!, userId)
                }
                .addOnFailureListener { e ->
                    handleUploadError("Failed to create new novel ID.", e)
                }
        } else {
            // novelId already exists, proceed with the upload.
            performPdfUpload(pdfUri, novelId!!, userId)
        }
    }

    private fun performPdfUpload(pdfUri: Uri, novelId: String, userId: String) {
        val filename = UUID.randomUUID().toString() + ".pdf"
        // -- CHANGED: The storage path is now "books"
        val pdfRef = storage.reference.child("books/$novelId/$filename")

        pdfRef.putFile(pdfUri)
            .addOnSuccessListener {
                pdfRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val pdfUrl = downloadUrl.toString()
                    Log.d("UploadPdfFragment", "PDF uploaded successfully. URL: $pdfUrl")

                    // -- CHANGED: The Firestore document is updated in the "books" collection
                    firestore.collection("books").document(novelId)
                        .update(mapOf("pdfUrl" to pdfUrl, "authorId" to userId))
                        .addOnSuccessListener {
                            binding.pdfStatusText.text = "Upload complete!"
                            Toast.makeText(requireContext(), "PDF uploaded successfully!", Toast.LENGTH_SHORT).show()
                            // Navigate to the next fragment and pass the URL
                            val action = UploadPdfFragmentDirections.actionUploadPdfFragmentToEditBookDetailsFragment(novelId, pdfUrl)
                            findNavController().navigate(action)
                        }
                        .addOnFailureListener { e ->
                            handleUploadError("Failed to update Firestore with PDF URL and authorId", e)
                        }
                }.addOnFailureListener { e ->
                    handleUploadError("Failed to get download URL", e)
                }
            }
            .addOnFailureListener { e ->
                handleUploadError("Upload failed", e)
            }
    }

    private fun handleUploadError(message: String, e: Exception) {
        Log.e("UploadPdfFragment", message, e)
        binding.pdfStatusText.text = "Error: $message"
        binding.completeBookInfoButton.isEnabled = true
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}