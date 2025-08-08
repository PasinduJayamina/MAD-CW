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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let { uri ->
                Glide.with(this).load(uri).into(bookCoverImageView)
                // TODO: Upload 'uri' to Firebase Storage
            }
        } else {
            Toast.makeText(requireContext(), "Image capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            Glide.with(this).load(uri).into(bookCoverImageView)
            // TODO: Upload 'uri' to Firebase Storage
        } else {
            Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private var imageUri: Uri? = null
    private var currentBook: Book? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()

        val bookId = arguments?.getString("bookId")
        if (bookId != null) {
            loadBookDetails(bookId)
        } else {
            Toast.makeText(requireContext(), "No book ID provided.", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        setupClickListeners()
        setupTextWatchers()
    }

    private fun initializeViews() {
        backArrow = binding.backArrow
        topBarBookTitle = binding.topBarBookTitle
        createChapterButton = binding.createChapterButton
        uploadCoverButton = binding.uploadCoverButton
        bookCoverImageView = binding.bookCoverImageView
        deleteBookButton = binding.deleteBookButton
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
        optionUkEnglish = binding.optionUkEnglish
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
    }

    private fun loadBookDetails(bookId: String) {
        // Simulate fetching book data
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        currentBook = Book(
            id = bookId,
            coverImageUrl = "https://placehold.co/96x128/000000/FFFFFF?text=BookCover",
            title = "The Ancient Scroll",
            author = "Pasin",
            chapterCount = 10,
            // Convert Date to String to match the Book model
            lastUpdated = dateFormat.format(Date()),
            createdOn = dateFormat.format(Date())
        )

        currentBook?.let { book ->
            topBarBookTitle.text = book.title.ifEmpty { "Edit Book Details" }
            bookTitleValue.text = book.title.ifEmpty { "Edit Title" }
            titleEditText.setText(book.title)

            if (!book.coverImageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(book.coverImageUrl)
                    .placeholder(R.drawable.placeholder_book_cover)
                    .error(R.drawable.placeholder_book_cover)
                    .into(bookCoverImageView)
            } else {
                bookCoverImageView.setImageResource(R.drawable.placeholder_book_cover)
            }
        }
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener { findNavController().navigateUp() }
        createChapterButton.setOnClickListener {
            findNavController().navigate(R.id.action_editBookDetailFragment_to_writeChaptersFragment)
        }
        uploadCoverButton.setOnClickListener { showImageSourceDialog() }
        deleteBookButton.setOnClickListener { Toast.makeText(requireContext(), "Delete clicked", Toast.LENGTH_SHORT).show() }

        setupExpandableRow(titleRow, titleEditContainer, titleArrow)
        setupExpandableRow(languageRow, languageOptionsContainer, languageArrow)
        // ... setup other expandable rows ...
    }

    private fun setupTextWatchers() {
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bookTitleValue.text = s.toString().ifEmpty { "Edit Title" }
                topBarBookTitle.text = s.toString().ifEmpty { "Edit Book Details" }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // ... setup other text watchers ...
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
