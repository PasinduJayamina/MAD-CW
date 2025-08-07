package com.example.novelonline.fragments // Adjust package as needed

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentEditBookDetailsBinding
import com.example.novelonline.models.Book

class EditBookDetailsFragment : Fragment() {

    private var _binding: FragmentEditBookDetailsBinding? = null
    private val binding get() = _binding!!

    // Views from XML
    private lateinit var backArrow: TextView
    private lateinit var topBarBookTitle: TextView
    private lateinit var createChapterButton: Button
    private lateinit var uploadCoverButton: Button
    private lateinit var bookCoverImageView: ImageView
    private lateinit var deleteBookButton: Button

    // Basic Information Section
    private lateinit var titleRow: LinearLayout
    private lateinit var bookTitleValue: TextView
    private lateinit var titleArrow: ImageView
    private lateinit var titleEditContainer: LinearLayout
    private lateinit var titleEditText: EditText

    private lateinit var languageRow: LinearLayout
    private lateinit var languageValue: TextView
    private lateinit var languageArrow: ImageView
    private lateinit var languageOptionsContainer: LinearLayout
    private lateinit var optionAmericanEnglish: TextView
    private lateinit var optionUkEnglish: TextView

    private lateinit var typeRow: LinearLayout
    private lateinit var typeValue: TextView
    private lateinit var typeArrow: ImageView
    private lateinit var typeOptionsContainer: LinearLayout
    private lateinit var optionFanFiction: TextView
    private lateinit var optionNovels: TextView
    private lateinit var optionShortStory: TextView

    private lateinit var genreRow: LinearLayout
    private lateinit var genreValue: TextView
    private lateinit var genreArrow: ImageView
    private lateinit var genreOptionsContainer: LinearLayout
    private lateinit var optionRomance: TextView
    private lateinit var optionMystery: TextView
    private lateinit var optionFantasy: TextView
    private lateinit var optionScienceFiction: TextView
    private lateinit var optionThriller: TextView
    private lateinit var optionHistoricalFiction: TextView

    private lateinit var synopsisRow: LinearLayout
    private lateinit var synopsisValue: TextView
    private lateinit var synopsisArrow: ImageView
    private lateinit var synopsisEditContainer: LinearLayout
    private lateinit var synopsisEditText: EditText

    private lateinit var tagsRow: LinearLayout
    private lateinit var tagsValue: TextView
    private lateinit var tagsArrow: ImageView
    private lateinit var tagsEditContainer: LinearLayout
    private lateinit var tagsEditText: EditText

    // More Information Section
    private lateinit var warningNoticeRow: LinearLayout
    private lateinit var warningNoticeValue: TextView
    private lateinit var warningNoticeArrow: ImageView
    private lateinit var warningNoticeOptionsContainer: LinearLayout
    private lateinit var optionNoWarning: TextView
    private lateinit var optionMatureContent: TextView
    private lateinit var optionGraphicViolence: TextView

    private lateinit var lengthRow: LinearLayout
    private lateinit var lengthValue: TextView
    private lateinit var lengthArrow: ImageView
    private lateinit var lengthOptionsContainer: LinearLayout
    private lateinit var optionShortLength: TextView
    private lateinit var optionMediumLength: TextView
    private lateinit var optionLongLength: TextView

    private lateinit var writingContestRow: LinearLayout
    private lateinit var writingContestValue: TextView
    private lateinit var writingContestArrow: ImageView
    private lateinit var writingContestOptionsContainer: LinearLayout
    private lateinit var optionParticipating: TextView
    private lateinit var optionNotParticipating: TextView

    private lateinit var statusValue: TextView // Just a TextView, no click/expand

    // ActivityResultLaunchers for camera and gallery
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Image captured successfully, 'imageUri' now holds the URI
            // You can now upload this URI to Firebase Storage and then update the UI
            Log.d("EditBookDetails", "Image captured: $imageUri")
            imageUri?.let { uri ->
                Glide.with(this).load(uri).into(bookCoverImageView)
                // TODO: Upload 'uri' to Firebase Storage
            }
        } else {
            Toast.makeText(requireContext(), "Image capture cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Image picked successfully, 'uri' holds the URI
            Log.d("EditBookDetails", "Image picked: $uri")
            Glide.with(this).load(uri).into(bookCoverImageView)
            // TODO: Upload 'uri' to Firebase Storage
        } else {
            Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private var imageUri: Uri? = null // To store URI for camera capture

    // Book data (simulated, would come from Firestore)
    private var currentBook: Book? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Initialize Views ---
        backArrow = binding.backArrow
        topBarBookTitle = binding.topBarBookTitle
        createChapterButton = binding.createChapterButton
        uploadCoverButton = binding.uploadCoverButton
        bookCoverImageView = binding.bookCoverImageView
        deleteBookButton = binding.deleteBookButton

        // Basic Information Section
        titleRow = binding.titleRow
        bookTitleValue = binding.bookTitleValue
        titleArrow = binding.titleArrow
        titleEditContainer = binding.titleEditContainer
        titleEditText = binding.titleEditText

        languageRow = binding.languageRow
        languageValue = binding.languageValue
        languageArrow = binding.languageArrow
        languageOptionsContainer = binding.languageOptionsContainer
        optionAmericanEnglish = binding.optionAmericanEnglish
        optionUkEnglish = binding.optionUkEnglish // Example of findViewById if not in binding

        typeRow = binding.typeRow
        typeValue = binding.typeValue
        typeArrow = binding.typeArrow
        typeOptionsContainer = binding.typeOptionsContainer
        optionFanFiction = binding.optionFanFiction
        optionNovels = binding.optionNovels
        optionShortStory = binding.optionShortStory

        genreRow = binding.genreRow
        genreValue = binding.genreValue
        genreArrow = binding.genreArrow
        genreOptionsContainer = binding.genreOptionsContainer
        optionRomance = binding.optionRomance
        optionMystery = binding.optionMystery
        optionFantasy = binding.optionFantasy
        optionScienceFiction = binding.optionScienceFiction
        optionThriller = binding.optionThriller
        optionHistoricalFiction = binding.optionHistoricalFiction

        synopsisRow = binding.synopsisRow
        synopsisValue = binding.synopsisValue
        synopsisArrow = binding.synopsisArrow
        synopsisEditContainer = binding.synopsisEditContainer
        synopsisEditText = binding.synopsisEditText

        tagsRow = binding.tagsRow
        tagsValue = binding.tagsValue
        tagsArrow = binding.tagsArrow
        tagsEditContainer = binding.tagsEditContainer
        tagsEditText = binding.tagsEditText

        // More Information Section
        warningNoticeRow = binding.warningNoticeRow
        warningNoticeValue = binding.warningNoticeValue
        warningNoticeArrow = binding.warningNoticeArrow
        warningNoticeOptionsContainer = binding.warningNoticeOptionsContainer
        optionNoWarning = binding.optionNoWarning
        optionMatureContent = binding.optionMatureContent
        optionGraphicViolence = binding.optionGraphicViolence

        lengthRow = binding.lengthRow
        lengthValue = binding.lengthValue
        lengthArrow = binding.lengthArrow
        lengthOptionsContainer = binding.lengthOptionsContainer
        optionShortLength = binding.optionShortLength
        optionMediumLength = binding.optionMediumLength
        optionLongLength = binding.optionLongLength


        statusValue = binding.statusValue


        // --- Get Book ID from arguments and load data ---
        val bookId = arguments?.getString("bookId") // Assuming bookId is passed as an argument
        if (bookId != null) {
            loadBookDetails(bookId)
        } else {
            // Handle case where no bookId is provided (e.g., creating a new book)
            // Or navigate back if this fragment requires a bookId
            Toast.makeText(requireContext(), "No book ID provided.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        // --- Set up Click Listeners ---

        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        createChapterButton.setOnClickListener {
            // TODO: Logic to create a new chapter for this book
            findNavController().navigate(R.id.action_editBookDetailFragment_to_writeChaptersFragment)
            Toast.makeText(requireContext(), "Create Chapter clicked!", Toast.LENGTH_SHORT).show()
        }

        uploadCoverButton.setOnClickListener {
            showImageSourceDialog()
        }

        deleteBookButton.setOnClickListener {
            // TODO: Implement delete book logic (similar to YourWorksFragment)
            Toast.makeText(requireContext(), "Delete Book clicked!", Toast.LENGTH_SHORT).show()
        }

        // Expandable rows logic
        setupExpandableRow(titleRow, titleEditContainer, titleArrow, bookTitleValue)
        setupExpandableRow(languageRow, languageOptionsContainer, languageArrow, languageValue)
        setupExpandableRow(typeRow, typeOptionsContainer, typeArrow, typeValue)
        setupExpandableRow(genreRow, genreOptionsContainer, genreArrow, genreValue)
        setupExpandableRow(synopsisRow, synopsisEditContainer, synopsisArrow, synopsisValue)
        setupExpandableRow(tagsRow, tagsEditContainer, tagsArrow, tagsValue)
        setupExpandableRow(warningNoticeRow, warningNoticeOptionsContainer, warningNoticeArrow, warningNoticeValue)
        setupExpandableRow(lengthRow, lengthOptionsContainer, lengthArrow, lengthValue)
        setupExpandableRow(writingContestRow, writingContestOptionsContainer, writingContestArrow, writingContestValue)

        // Option selection listeners for Language
        optionAmericanEnglish.setOnClickListener { selectOption(languageValue, languageOptionsContainer, languageArrow, "American English") }
        optionUkEnglish.setOnClickListener { selectOption(languageValue, languageOptionsContainer, languageArrow, "UK English") }

        // Option selection listeners for Type
        optionFanFiction.setOnClickListener { selectOption(typeValue, typeOptionsContainer, typeArrow, "Fan-fiction") }
        optionNovels.setOnClickListener { selectOption(typeValue, typeOptionsContainer, typeArrow, "Novels") }
        optionShortStory.setOnClickListener { selectOption(typeValue, typeOptionsContainer, typeArrow, "Short story") }

        // Option selection listeners for Genre
        optionRomance.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Romance") }
        optionMystery.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Mystery") }
        optionFantasy.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Fantasy") }
        optionScienceFiction.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Science Fiction") }
        optionThriller.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Thriller") }
        optionHistoricalFiction.setOnClickListener { selectOption(genreValue, genreOptionsContainer, genreArrow, "Historical Fiction") }

        // Option selection listeners for Warning Notice
        optionNoWarning.setOnClickListener { selectOption(warningNoticeValue, warningNoticeOptionsContainer, warningNoticeArrow, "None") }
        optionMatureContent.setOnClickListener { selectOption(warningNoticeValue, warningNoticeOptionsContainer, warningNoticeArrow, "Mature Content") }
        optionGraphicViolence.setOnClickListener { selectOption(warningNoticeValue, warningNoticeOptionsContainer, warningNoticeArrow, "Graphic Violence") }

        // Option selection listeners for Length
        optionShortLength.setOnClickListener { selectOption(lengthValue, lengthOptionsContainer, lengthArrow, "Short") }
        optionMediumLength.setOnClickListener { selectOption(lengthValue, lengthOptionsContainer, lengthArrow, "Medium") }
        optionLongLength.setOnClickListener { selectOption(lengthValue, lengthOptionsContainer, lengthArrow, "Long") }

        // Option selection listeners for Writing Contest
        optionParticipating.setOnClickListener { selectOption(writingContestValue, writingContestOptionsContainer, writingContestArrow, "Participating") }
        optionNotParticipating.setOnClickListener { selectOption(writingContestValue, writingContestOptionsContainer, writingContestArrow, "Not participating") }

        // Text change listener for Title EditText to update the value TextView
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bookTitleValue.text = s.toString().ifEmpty { "Edit Title" } // Update value, show hint if empty
                topBarBookTitle.text = s.toString().ifEmpty { "Edit Book Details" } // Update top bar title
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Text change listener for Synopsis EditText
        synopsisEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                synopsisValue.text = s.toString().ifEmpty { "Add Synopsis" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Text change listener for Tags EditText
        tagsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tagsValue.text = s.toString().ifEmpty { "Add Tags" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Function to load book details (simulated)
    private fun loadBookDetails(bookId: String) {
        // TODO: Replace with actual Firebase Firestore data fetching
        // Example: firestore.collection("books").document(bookId).get().addOnSuccessListener { document -> ... }

        // Simulate fetching book data
        currentBook = Book(
            id = bookId,
            coverImageUrl  = "https://placehold.co/96x128/000000/FFFFFF?text=BookCover", // Example image URL
            title = "The Ancient Scroll",
            chapterCount = 10,
            lastUpdated = "July 29, 2025",
            createdOn = "Jan 1, 2025"
        )

        currentBook?.let { book ->
            // Update UI with book data
            topBarBookTitle.text = book.title.ifEmpty { "Edit Book Details" }
            bookTitleValue.text = book.title.ifEmpty { "Edit Title" }
            titleEditText.setText(book.title)

            // Load book cover image
            if (!book.coverImageUrl .isNullOrEmpty()) {
                Glide.with(this)
                    .load(book.coverImageUrl )
                    .placeholder(R.drawable.placeholder_book_cover) // Default if loading
                    .error(R.drawable.placeholder_book_cover) // Default if error
                    .into(bookCoverImageView)
            } else {
                bookCoverImageView.setImageResource(R.drawable.placeholder_book_cover) // Show placeholder if no URL
            }

            // TODO: Update other fields with actual book data from 'book' object
            // languageValue.text = book.language
            // typeValue.text = book.type
            // genreValue.text = book.genre
            // synopsisValue.text = book.synopsis.ifEmpty { "Add Synopsis" }
            // tagsValue.text = book.tags.joinToString(", ").ifEmpty { "Add Tags" }
            // warningNoticeValue.text = book.warningNotice
            // lengthValue.text = book.length
            // statusValue.text = book.status
        }
    }

    // Function to show dialog for image source selection
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

    // Function to launch camera
    private fun takePicture() {
        // Create a temporary file URI for the image
        imageUri = createImageUri()
        imageUri?.let { uri ->
            takePictureLauncher.launch(uri)
        } ?: Toast.makeText(requireContext(), "Could not create image file.", Toast.LENGTH_SHORT).show()
    }

    // Helper function to create a temporary URI for camera output
    private fun createImageUri(): Uri? {
        val contentResolver = requireContext().contentResolver
        val fileName = "book_cover_${System.currentTimeMillis()}.jpg"
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        })
    }

    // Function to launch gallery
    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    /**
     * Toggles the visibility of an options container and animates its arrow.
     * Also handles hiding other open containers if only one should be open at a time.
     */
    private fun setupExpandableRow(
        rowLayout: LinearLayout,
        optionsContainer: LinearLayout,
        arrowImageView: ImageView,
        valueTextView: TextView? = null // Optional: for editable text fields
    ) {
        rowLayout.setOnClickListener {
            // Close other open containers (optional, but good for single-open behavior)
            // This part requires you to keep track of all expandable containers and their arrows.
            // For simplicity, I'm just toggling the clicked one.
            // If you want "only one open at a time", you'd need a list of all containers/arrows
            // and loop through them to close before opening the current one.

            if (optionsContainer.visibility == View.GONE) {
                optionsContainer.visibility = View.VISIBLE
                arrowImageView.animate().rotation(90f).setDuration(300).start()
                // If it's an EditText container, request focus
                if (optionsContainer.id == R.id.title_edit_container ||
                    optionsContainer.id == R.id.synopsis_edit_container ||
                    optionsContainer.id == R.id.tags_edit_container) {
                    val editText = optionsContainer.getChildAt(0) as? EditText
                    editText?.requestFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
            } else {
                optionsContainer.visibility = View.GONE
                arrowImageView.animate().rotation(0f).setDuration(300).start()
                // Hide keyboard if it was an EditText container
                if (optionsContainer.id == R.id.title_edit_container ||
                    optionsContainer.id == R.id.synopsis_edit_container ||
                    optionsContainer.id == R.id.tags_edit_container) {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(optionsContainer.windowToken, 0)
                }
            }
        }
    }

    /**
     * Sets the selected option text, hides the options container, and animates the arrow back.
     */
    private fun selectOption(textView: TextView, container: LinearLayout, arrow: ImageView, selectedText: String) {
        textView.text = selectedText
        container.visibility = View.GONE
        arrow.animate().rotation(0f).setDuration(300).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Optional: A factory method to create instances of the fragment
    companion object {
        fun newInstance(bookId: String) = EditBookDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("bookId", bookId)
            }
        }
    }
}