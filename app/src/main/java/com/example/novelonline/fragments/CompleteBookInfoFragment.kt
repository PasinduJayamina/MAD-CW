package com.example.novelonline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.novelonline.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CompleteBookInfoFragment : Fragment() {

    // --- Declare Views ---
    private lateinit var novelId: String

    // Back Arrow
    private lateinit var backArrow: TextView

    // Language views
    private lateinit var languageRow: LinearLayout
    private lateinit var languageOptionsContainer: LinearLayout
    private lateinit var languageText: TextView
    private lateinit var languageArrow: TextView

    // Book Type views
    private lateinit var bookTypeRow: LinearLayout
    private lateinit var bookTypeOptionsContainer: LinearLayout
    private lateinit var bookTypeText: TextView
    private lateinit var bookTypeArrow: TextView

    // Genre views
    private lateinit var genreRow: LinearLayout
    private lateinit var genreOptionsContainer: LinearLayout
    private lateinit var genreText: TextView
    private lateinit var genreArrow: TextView

    // Start Writing Button
    private lateinit var startWritingButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete_book_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the novelId from the arguments
        val args: CompleteBookInfoFragmentArgs by navArgs()
        novelId = args.novelId
        // You can now use novelId to reference the document in Firestore

        // Back Arrow
        backArrow = view.findViewById(R.id.back_arrow)

        // Language section
        languageRow = view.findViewById(R.id.language_row)
        languageOptionsContainer = view.findViewById(R.id.language_options_container)
        languageText = view.findViewById(R.id.language_text)
        languageArrow = view.findViewById(R.id.language_arrow)
        val optionAmericanEnglish = view.findViewById<TextView>(R.id.option_american_english)
        val optionUkEnglish = view.findViewById<TextView>(R.id.option_uk_english)

        // Book Type section
        bookTypeRow = view.findViewById(R.id.book_type_row)
        bookTypeOptionsContainer = view.findViewById(R.id.book_type_options_container)
        bookTypeText = view.findViewById(R.id.book_type_text)
        bookTypeArrow = view.findViewById(R.id.book_type_arrow)
        val optionFanFiction = view.findViewById<TextView>(R.id.option_fan_fiction)
        val optionNovels = view.findViewById<TextView>(R.id.option_novels)
        val optionShortStory = view.findViewById<TextView>(R.id.option_short_story)

        // Genre section
        genreRow = view.findViewById(R.id.genre_row)
        genreOptionsContainer = view.findViewById(R.id.genre_options_container)
        genreText = view.findViewById(R.id.genre_text)
        genreArrow = view.findViewById(R.id.genre_arrow)
        val optionRomance = view.findViewById<TextView>(R.id.option_romance)
        val optionMystery = view.findViewById<TextView>(R.id.option_mystery)
        val optionFantasy = view.findViewById<TextView>(R.id.option_fantasy)
        val optionScienceFiction = view.findViewById<TextView>(R.id.option_science_fiction)
        val optionThriller = view.findViewById<TextView>(R.id.option_thriller)
        val optionHistoricalFiction = view.findViewById<TextView>(R.id.option_historical_fiction)

        // Start Writing Button
        startWritingButton = view.findViewById(R.id.start_writing_button)


        // --- Set up all click listeners and data ---

        // Set up the back button listener
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Language section listeners
        languageRow.setOnClickListener {
            toggleOptions(languageOptionsContainer, languageArrow)
        }
        optionAmericanEnglish.setOnClickListener {
            selectOption(languageText, languageOptionsContainer, languageArrow, optionAmericanEnglish.text.toString())
        }
        optionUkEnglish.setOnClickListener {
            selectOption(languageText, languageOptionsContainer, languageArrow, optionUkEnglish.text.toString())
        }

        // Book Type section listeners
        bookTypeRow.setOnClickListener {
            toggleOptions(bookTypeOptionsContainer, bookTypeArrow)
        }
        optionFanFiction.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, optionFanFiction.text.toString())
        }
        optionNovels.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, optionNovels.text.toString())
        }
        optionShortStory.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, optionShortStory.text.toString())
        }

        // Genre section listeners
        genreRow.setOnClickListener {
            toggleOptions(genreOptionsContainer, genreArrow)
        }
        optionRomance.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionRomance.text.toString())
        }
        optionMystery.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionMystery.text.toString())
        }
        optionFantasy.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionFantasy.text.toString())
        }
        optionScienceFiction.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionScienceFiction.text.toString())
        }
        optionThriller.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionThriller.text.toString())
        }
        optionHistoricalFiction.setOnClickListener {
            selectOption(genreText, genreOptionsContainer, genreArrow, optionHistoricalFiction.text.toString())
        }

        // Start Writing Button listener
        startWritingButton.setOnClickListener {
            // Get the selected values from your TextViews
            val selectedLanguage = languageText.text.toString()
            val selectedBookType = bookTypeText.text.toString()
            val selectedGenre = genreText.text.toString()

            // You'll also need a way to handle multiple selected genres
            // For now, let's assume one is selected.

            // Now, you can update Firestore
            updateNovelInFirestore(novelId, selectedLanguage, selectedBookType, selectedGenre)

            // Navigate to the next fragment
            findNavController().navigate(R.id.action_completeBookInfoFragment_to_writeChaptersFragment)
        }
    }
    private fun updateNovelInFirestore(
        novelId: String,
        language: String,
        bookType: String,
        genre: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val novelRef = db.collection("books").document(novelId)

        val updates = hashMapOf<String, Any>(
            "language" to language,
            "bookType" to bookType,
            "genres" to listOf(genre), // Assuming one genre for now
            "lastUpdated" to Date()
        )

        novelRef.update(updates)
            .addOnSuccessListener {
                Log.d("CompleteBookInfoFragment", "Document successfully updated!")
                Toast.makeText(requireContext(), "Book info saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("CompleteBookInfoFragment", "Error updating document", e)
                Toast.makeText(requireContext(), "Error saving info: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Toggles the visibility of an options container and animates its arrow.
     */
    private fun toggleOptions(container: LinearLayout, arrow: TextView) {
        if (container.visibility == View.GONE) {
            container.visibility = View.VISIBLE
            arrow.animate().rotation(90f).setDuration(300).start()
        } else {
            container.visibility = View.GONE
            arrow.animate().rotation(0f).setDuration(300).start()
        }
    }

    /**
     * Sets the selected option text, hides the options container, and animates the arrow back.
     */
    private fun selectOption(textView: TextView, container: LinearLayout, arrow: TextView, selectedText: String) {
        textView.text = selectedText
        container.visibility = View.GONE
        arrow.animate().rotation(0f).setDuration(300).start()
    }

    companion object {
        fun newInstance() = CompleteBookInfoFragment()
    }
}