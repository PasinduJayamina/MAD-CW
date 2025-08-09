package com.example.novelonline.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentWriteChaptersBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Stack

class WriteChaptersFragment : Fragment() {

    private var _binding: FragmentWriteChaptersBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var backArrow: TextView
    private lateinit var wordCountTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var chapterTitleEditText: EditText
    private lateinit var mainTextEditText: EditText
    private lateinit var boldButton: TextView
    private lateinit var italicButton: TextView
    private lateinit var saveButton: TextView
    private lateinit var undoButton: TextView
    private lateinit var redoButton: TextView

    // Firebase instance
    private lateinit var firestore: FirebaseFirestore

    // Novel ID from navigation arguments
    private val args: WriteChaptersFragmentArgs by navArgs()
    private lateinit var novelId: String

    // Undo/Redo history
    private val undoStack = Stack<String>()
    private val redoStack = Stack<String>()
    private var isUndoingOrRedoing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        firestore = Firebase.firestore

        // Get novelId from arguments
        novelId = args.novelId

        // Initialize views using the binding object
        backArrow = binding.backArrow
        wordCountTextView = binding.wordCountTextView
        nextButton = binding.nextButton
        chapterTitleEditText = binding.chapterTitleEditText
        mainTextEditText = binding.mainTextEditText
        boldButton = binding.boldButton
        italicButton = binding.italicButton
        saveButton = binding.saveButton
        undoButton = binding.undoButton
        redoButton = binding.redoButton
        // Note: The author's thoughts views have been removed.

        // --- Set up click listeners ---
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_writeChaptersFragment_to_yourWorksFragment)
        }

        boldButton.setOnClickListener {
            toggleStyle(Typeface.BOLD)
        }
        italicButton.setOnClickListener {
            toggleStyle(Typeface.ITALIC)
        }

        saveButton.setOnClickListener {
            val chapterTitle = chapterTitleEditText.text.toString().trim()
            val mainText = mainTextEditText.text.toString().trim()

            if (chapterTitle.isEmpty() || mainText.isEmpty()) {
                Toast.makeText(requireContext(), "Chapter title and content cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveChapterToFirestore(chapterTitle, mainText)
        }

        undoButton.setOnClickListener {
            if (undoStack.isNotEmpty()) {
                isUndoingOrRedoing = true
                val currentState = mainTextEditText.text.toString()
                redoStack.push(currentState)
                val previousState = undoStack.pop()
                mainTextEditText.setText(previousState)
                mainTextEditText.setSelection(previousState.length)
                isUndoingOrRedoing = false
                updateUndoRedoButtons()
            }
        }

        redoButton.setOnClickListener {
            if (redoStack.isNotEmpty()) {
                isUndoingOrRedoing = true
                val currentState = mainTextEditText.text.toString()
                undoStack.push(currentState)
                val nextState = redoStack.pop()
                mainTextEditText.setText(nextState)
                mainTextEditText.setSelection(nextState.length)
                isUndoingOrRedoing = false
                updateUndoRedoButtons()
            }
        }

        // --- TextWatcher for word count and undo/redo history ---
        mainTextEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUndoingOrRedoing) {
                    val text = s?.toString() ?: ""
                    // Update word count
                    val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val wordCount = words.size
                    wordCountTextView.text = "$wordCount Word"

                    // Update undo history
                    undoStack.push(text)
                    redoStack.clear()
                    updateUndoRedoButtons()
                }
            }
        })
        updateUndoRedoButtons() // Initial state
    }

    private fun updateUndoRedoButtons() {
        undoButton.isEnabled = undoStack.isNotEmpty()
        redoButton.isEnabled = redoStack.isNotEmpty()
    }

    /**
     * Toggles a StyleSpan on the selected text in the main text editor.
     * @param style The style to toggle (e.g., Typeface.BOLD, Typeface.ITALIC).
     */
    private fun toggleStyle(style: Int) {
        val editable = mainTextEditText.text
        val start = mainTextEditText.selectionStart
        val end = mainTextEditText.selectionEnd

        if (start < 0 || end < 0 || start == end) {
            // No text is selected, do nothing.
            return
        }

        val spans = editable.getSpans(start, end, StyleSpan::class.java)
        var exists = false

        // Check if any part of the selection already has the style
        for (span in spans) {
            if (span.style == style) {
                editable.removeSpan(span)
                exists = true
            }
        }

        // If the style didn't exist, apply it to the whole selection.
        if (!exists) {
            val styleSpan = StyleSpan(style)
            editable.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Saves a new chapter to the Firestore database.
     * It first queries the number of existing chapters for the book
     * to determine the next chapter number.
     *
     * @param chapterTitle The title of the chapter.
     * @param mainText The content of the chapter.
     */
    private fun saveChapterToFirestore(chapterTitle: String, mainText: String) {
        val novelDocRef = firestore.collection("books").document(novelId)
        val chaptersCollectionRef = novelDocRef.collection("chapters")

        // First, get the count of existing chapters to determine the next number
        chaptersCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                val nextChapterNumber = querySnapshot.size() + 1
                val chapterDocId = String.format("chapter%02d", nextChapterNumber)

                val chapterData = hashMapOf(
                    "title" to chapterTitle,
                    "content" to mainText,
                    "chapterNumber" to nextChapterNumber
                )

                chaptersCollectionRef.document(chapterDocId).set(chapterData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Chapter saved successfully!", Toast.LENGTH_SHORT).show()
                        // Navigate back to the Your Works fragment after saving
                        findNavController().navigate(R.id.action_writeChaptersFragment_to_yourWorksFragment)
                    }
                    .addOnFailureListener { e ->
                        Log.e("WriteChaptersFragment", "Error saving chapter: ", e)
                        Toast.makeText(requireContext(), "Failed to save chapter. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("WriteChaptersFragment", "Error counting chapters: ", e)
                Toast.makeText(requireContext(), "Failed to save chapter due to a database error.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding object to avoid memory leaks
        _binding = null
    }

    companion object {
        fun newInstance() = WriteChaptersFragment()
    }
}
