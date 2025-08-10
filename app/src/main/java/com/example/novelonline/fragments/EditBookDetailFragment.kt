package com.example.novelonline.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentEditBookDetailsBinding
import com.example.novelonline.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.Date
import java.util.UUID

class EditBookDetailsFragment : Fragment() {

    private var _binding: FragmentEditBookDetailsBinding? = null
    private val binding get() = _binding!!

    // Firebase instances
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Safe Args
    private lateinit var novelId: String
    private var pdfUrl: String? = null
    private var existingCoverImageUrl: String? = null

    // Class-level variables to hold current selections
    private var selectedLanguage: String? = null
    private var selectedBookType: String? = null
    private var selectedGenre: String? = null
    private var selectedWarning: String? = null
    private var selectedLength: String? = null

    // Image capture and selection launchers
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let { uri ->
                uploadCoverImage(uri)
            }
        } else {
            Toast.makeText(requireContext(), "Image capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uploadCoverImage(uri)
        } else {
            Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Firestore and Storage
        firestore = Firebase.firestore
        storage = Firebase.storage

        // Retrieve arguments passed via Safe Args
        val args: EditBookDetailsFragmentArgs by navArgs()
        novelId = args.novelId
        pdfUrl = args.pdfUrl

        // Load existing book details from Firestore
        loadBookDetails(novelId)

        setupClickListeners()
        setupTextWatchers()
    }

    private fun loadBookDetails(bookId: String) {
        // Load from the "books" collection
        val bookRef = firestore.collection("books").document(bookId)
        bookRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val book = document.toObject(Book::class.java)
                    book?.let {
                        // Populate UI with fetched data
                        binding.topBarBookTitle.text = it.title
                        binding.bookTitleValue.text = it.title
                        binding.titleEditText.setText(it.title)

                        // Populate other fields if they exist
                        binding.synopsisEditText.setText(it.synopsis)
                        binding.synopsisValue.text = it.synopsis
                        binding.tagsEditText.setText(it.tags?.joinToString(", "))
                        binding.tagsValue.text = it.tags?.joinToString(", ")

                        // Populate selected options
                        it.language?.let { selectedLanguage = it; binding.languageValue.text = it }
                        it.bookType?.let { selectedBookType = it; binding.typeValue.text = it }
                        it.genres?.let { selectedGenre = it.toString(); binding.genreValue.text =
                            it.toString()
                        }
                        it.warningNotice?.let { selectedWarning = it; binding.warningNoticeValue.text = it }
                        it.length?.let { selectedLength = it; binding.lengthValue.text = it }

                        // Store the cover image URL
                        existingCoverImageUrl = it.coverImageUrl

                        // Load cover image if it exists
                        if (!it.coverImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(it.coverImageUrl)
                                .placeholder(R.drawable.`placeholder_book_cover`)
                                .error(R.drawable.`placeholder_book_cover`)
                                .into(binding.bookCoverImageView)
                        } else {
                            binding.bookCoverImageView.setImageResource(R.drawable.`placeholder_book_cover`)
                        }
                    }
                } else {
                    Log.d("EditBookDetails", "No such document")
                    Toast.makeText(requireContext(), "Book not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditBookDetails", "Error loading book details", e)
                Toast.makeText(requireContext(), "Error loading book details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener { findNavController().navigateUp() }
        binding.uploadCoverButton.setOnClickListener { showImageSourceDialog() }
        binding.deleteBookButton.setOnClickListener { deleteBook() }

        // Expanded rows setup
        setupExpandableRow(binding.titleRow, binding.titleEditContainer, binding.titleArrow)
        setupExpandableRow(binding.languageRow, binding.languageOptionsContainer, binding.languageArrow)
        setupExpandableRow(binding.typeRow, binding.typeOptionsContainer, binding.typeArrow)
        setupExpandableRow(binding.genreRow, binding.genreOptionsContainer, binding.genreArrow)
        setupExpandableRow(binding.synopsisRow, binding.synopsisEditContainer, binding.synopsisArrow)
        setupExpandableRow(binding.tagsRow, binding.tagsEditContainer, binding.tagsArrow)
        setupExpandableRow(binding.warningNoticeRow, binding.warningNoticeOptionsContainer, binding.warningNoticeArrow)
        setupExpandableRow(binding.lengthRow, binding.lengthOptionsContainer, binding.lengthArrow)

        // Options listeners for selection
        binding.optionAmericanEnglish.setOnClickListener {
            selectOption(binding.languageValue, binding.languageOptionsContainer, binding.languageArrow, binding.optionAmericanEnglish.text.toString())
            selectedLanguage = binding.optionAmericanEnglish.text.toString()
        }
        binding.optionUkEnglish.setOnClickListener {
            selectOption(binding.languageValue, binding.languageOptionsContainer, binding.languageArrow, binding.optionUkEnglish.text.toString())
            selectedLanguage = binding.optionUkEnglish.text.toString()
        }
        binding.optionFanFiction.setOnClickListener {
            selectOption(binding.typeValue, binding.typeOptionsContainer, binding.typeArrow, binding.optionFanFiction.text.toString())
            selectedBookType = binding.optionFanFiction.text.toString()
        }
        binding.optionNovels.setOnClickListener {
            selectOption(binding.typeValue, binding.typeOptionsContainer, binding.typeArrow, binding.optionNovels.text.toString())
            selectedBookType = binding.optionNovels.text.toString()
        }
        binding.optionShortStory.setOnClickListener {
            selectOption(binding.typeValue, binding.typeOptionsContainer, binding.typeArrow, binding.optionShortStory.text.toString())
            selectedBookType = binding.optionShortStory.text.toString()
        }
        binding.optionRomance.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionRomance.text.toString())
            selectedGenre = binding.optionRomance.text.toString()
        }
        binding.optionMystery.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionMystery.text.toString())
            selectedGenre = binding.optionMystery.text.toString()
        }
        binding.optionFantasy.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionFantasy.text.toString())
            selectedGenre = binding.optionFantasy.text.toString()
        }
        binding.optionScienceFiction.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionScienceFiction.text.toString())
            selectedGenre = binding.optionScienceFiction.text.toString()
        }
        binding.optionThriller.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionThriller.text.toString())
            selectedGenre = binding.optionThriller.text.toString()
        }
        binding.optionHistoricalFiction.setOnClickListener {
            selectOption(binding.genreValue, binding.genreOptionsContainer, binding.genreArrow, binding.optionHistoricalFiction.text.toString())
            selectedGenre = binding.optionHistoricalFiction.text.toString()
        }
        binding.optionNoWarning.setOnClickListener {
            selectOption(binding.warningNoticeValue, binding.warningNoticeOptionsContainer, binding.warningNoticeArrow, binding.optionNoWarning.text.toString())
            selectedWarning = binding.optionNoWarning.text.toString()
        }
        binding.optionMatureContent.setOnClickListener {
            selectOption(binding.warningNoticeValue, binding.warningNoticeOptionsContainer, binding.warningNoticeArrow, binding.optionMatureContent.text.toString())
            selectedWarning = binding.optionMatureContent.text.toString()
        }
        binding.optionGraphicViolence.setOnClickListener {
            selectOption(binding.warningNoticeValue, binding.warningNoticeOptionsContainer, binding.warningNoticeArrow, binding.optionGraphicViolence.text.toString())
            selectedWarning = binding.optionGraphicViolence.text.toString()
        }
        binding.optionShortLength.setOnClickListener {
            selectOption(binding.lengthValue, binding.lengthOptionsContainer, binding.lengthArrow, binding.optionShortLength.text.toString())
            selectedLength = binding.optionShortLength.text.toString()
        }
        binding.optionMediumLength.setOnClickListener {
            selectOption(binding.lengthValue, binding.lengthOptionsContainer, binding.lengthArrow, binding.optionMediumLength.text.toString())
            selectedLength = binding.optionMediumLength.text.toString()
        }
        binding.optionLongLength.setOnClickListener {
            selectOption(binding.lengthValue, binding.lengthOptionsContainer, binding.lengthArrow, binding.optionLongLength.text.toString())
            selectedLength = binding.optionLongLength.text.toString()
        }


        // The main button to save all changes and navigate
        binding.createChapterButton.setOnClickListener {
            updateBookDetailsAndNavigate()
        }
    }

    private fun setupTextWatchers() {
        binding.titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.bookTitleValue.text = s.toString().ifEmpty { "Edit Title" }
                binding.topBarBookTitle.text = s.toString().ifEmpty { "Edit Book Details" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.synopsisEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.synopsisValue.text = s.toString().ifEmpty { "Write a synopsis" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tagsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tagsValue.text = s.toString().ifEmpty { "Add tags" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


     // Gathers all user input and updates the book document in Firestore.
    private fun updateBookDetailsAndNavigate() {
        val updates = hashMapOf<String, Any>()

        updates["title"] = binding.titleEditText.text.toString()
        updates["language"] = selectedLanguage ?: ""
        updates["bookType"] = selectedBookType ?: ""
        updates["genre"] = selectedGenre ?: ""
        updates["synopsis"] = binding.synopsisEditText.text.toString()
        updates["tags"] = binding.tagsEditText.text.toString().split(",").map { it.trim() }
        updates["warningNotice"] = selectedWarning ?: ""
        updates["length"] = selectedLength ?: ""
        updates["pdfUrl"] = pdfUrl ?: ""
        updates["lastUpdated"] = Date()

        // Check if there is an existing cover image URL, if not then upload the default image
        if (existingCoverImageUrl.isNullOrEmpty()) {
            updates["coverImageUrl"] = "https://placehold.co/96x128/000000/FFFFFF?text=BookCover"
        }

        // Update the book document in the "books" collection
        firestore.collection("books").document(novelId).update(updates)
            .addOnSuccessListener {
                Log.d("EditBookDetails", "Book details updated successfully.")
                Toast.makeText(requireContext(), "Book details saved!", Toast.LENGTH_SHORT).show()

                // Check for PDF URL to decide navigation
                if (!pdfUrl.isNullOrEmpty()) {
                    val action = EditBookDetailsFragmentDirections.actionEditBookDetailsFragmentToYourWorksFragment()
                    findNavController().navigate(action)
                } else {
                    val action = EditBookDetailsFragmentDirections.actionEditBookDetailsFragmentToWriteChaptersFragment(novelId)
                    findNavController().navigate(action)
                }
            }
            .addOnFailureListener { e ->
                Log.w("EditBookDetails", "Error updating book details", e)
                Toast.makeText(requireContext(), "Error saving details: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteBook() {
        // confirmation dialog before deleting
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Book")
            .setMessage("Are you sure you want to delete this book? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                performDelete()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDelete() {
        // Delete from the "books" collection
        firestore.collection("books").document(novelId).delete()
            .addOnSuccessListener {
                Log.d("EditBookDetails", "Firestore document deleted successfully.")
                // delete the files from Storage
                deleteFilesFromStorage()
                Toast.makeText(requireContext(), "Book deleted successfully.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editBookDetailsFragment_to_yourWorksFragment)
            }
            .addOnFailureListener { e ->
                Log.e("EditBookDetails", "Error deleting Firestore document", e)
                Toast.makeText(requireContext(), "Error deleting book.", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteFilesFromStorage() {
        val filesToDelete = ArrayList<String>()
        existingCoverImageUrl?.let { filesToDelete.add(it) }
        pdfUrl?.let { filesToDelete.add(it) }

        if (filesToDelete.isEmpty()) {
            return
        }

        filesToDelete.forEach { fileUrl ->
            try {
                // Get a reference from the URL
                val fileRef = storage.getReferenceFromUrl(fileUrl)
                fileRef.delete()
                    .addOnSuccessListener {
                        Log.d("EditBookDetails", "File deleted from Storage: $fileUrl")
                    }
                    .addOnFailureListener { e ->
                        Log.w("EditBookDetails", "Error deleting file from Storage: $fileUrl", e)
                    }
            } catch (e: Exception) {
                Log.w("EditBookDetails", "Invalid URL format for Firebase Storage: $fileUrl", e)
            }
        }
    }

    private fun uploadCoverImage(imageUri: Uri) {
        // Show loading indicator or toast
        Toast.makeText(requireContext(), "Uploading cover image...", Toast.LENGTH_SHORT).show()

        // Create a unique filename for the image
        val filename = UUID.randomUUID().toString() + ".jpg"

        // The storage path is now "books" for consistency
        val imageRef = storage.reference.child("books/$novelId/$filename")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Get the download URL after a successful upload
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    // Update Firestore in the "books" collection
                    firestore.collection("books").document(novelId)
                        .update("coverImageUrl", imageUrl)
                        .addOnSuccessListener {
                            existingCoverImageUrl = imageUrl // Update local variable
                            Glide.with(this).load(imageUrl).into(binding.bookCoverImageView)
                            Toast.makeText(requireContext(), "Cover image updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditBookDetails", "Error updating cover image URL in Firestore", e)
                            Toast.makeText(requireContext(), "Error updating image URL.", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditBookDetails", "Cover image upload failed", e)
                Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Upload Book Cover")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> pickImageFromGallery()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun takePicture() {
        imageUri = createImageUri()
        imageUri?.let { takePictureLauncher.launch(it) }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun createImageUri(): Uri? {
        val contentResolver = requireContext().contentResolver
        val fileName = "book_cover_${System.currentTimeMillis()}.jpg"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun setupExpandableRow(
        rowLayout: View,
        optionsContainer: View,
        arrowImageView: ImageView
    ) {
        rowLayout.setOnClickListener {
            if (optionsContainer.visibility == View.GONE) {
                optionsContainer.visibility = View.VISIBLE
                arrowImageView.animate().rotation(90f).setDuration(300).start()
                if (optionsContainer is LinearLayout && optionsContainer.getChildAt(0) is EditText) {
                    val editText = optionsContainer.getChildAt(0) as EditText
                    editText.requestFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
            } else {
                optionsContainer.visibility = View.GONE
                arrowImageView.animate().rotation(0f).setDuration(300).start()
            }
        }
    }

    private fun selectOption(textView: TextView, container: LinearLayout, arrow: ImageView, selectedText: String) {
        textView.text = selectedText
        container.visibility = View.GONE
        arrow.animate().rotation(0f).setDuration(300).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}