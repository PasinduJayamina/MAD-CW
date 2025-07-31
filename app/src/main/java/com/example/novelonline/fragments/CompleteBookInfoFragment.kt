package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.novelonline.R

class CompleteBookInfoFragment : Fragment() {

    // Language views
    private lateinit var languageRow: LinearLayout
    private lateinit var languageOptionsContainer: LinearLayout
    private lateinit var languageText: TextView
    private lateinit var languageArrow: TextView
    private lateinit var optionAmericanEnglish: TextView
    private lateinit var optionUkEnglish: TextView

    // Book Type views
    private lateinit var bookTypeRow: LinearLayout
    private lateinit var bookTypeOptionsContainer: LinearLayout
    private lateinit var bookTypeText: TextView
    private lateinit var bookTypeArrow: TextView
    private lateinit var optionFanFiction: TextView
    private lateinit var varoptionNovels: TextView
    private lateinit var optionShortStory: TextView

    // Genre views
    private lateinit var genreRow: LinearLayout
    private lateinit var genreOptionsContainer: LinearLayout
    private lateinit var genreText: TextView
    private lateinit var genreArrow: TextView
    private lateinit var optionRomance: TextView
    private lateinit var optionMystery: TextView
    private lateinit var optionFantasy: TextView
    private lateinit var optionScienceFiction: TextView
    private lateinit var optionThriller: TextView
    private lateinit var optionHistoricalFiction: TextView

    // Start Writing Button
    private lateinit var startWritingButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_book_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Initialize all views from the inflated layout ---
        // Language section
        languageRow = view.findViewById(R.id.language_row)
        languageOptionsContainer = view.findViewById(R.id.language_options_container)
        languageText = view.findViewById(R.id.language_text)
        languageArrow = view.findViewById(R.id.language_arrow)
        optionAmericanEnglish = view.findViewById(R.id.option_american_english)
        optionUkEnglish = view.findViewById(R.id.option_uk_english)

        // Book Type section
        bookTypeRow = view.findViewById(R.id.book_type_row)
        bookTypeOptionsContainer = view.findViewById(R.id.book_type_options_container)
        bookTypeText = view.findViewById(R.id.book_type_text)
        bookTypeArrow = view.findViewById(R.id.book_type_arrow)
        optionFanFiction = view.findViewById(R.id.option_fan_fiction)
        varoptionNovels = view.findViewById(R.id.option_novels)
        optionShortStory = view.findViewById(R.id.option_short_story)

        // Genre section
        genreRow = view.findViewById(R.id.genre_row)
        genreOptionsContainer = view.findViewById(R.id.genre_options_container)
        genreText = view.findViewById(R.id.genre_text)
        genreArrow = view.findViewById(R.id.genre_arrow)
        optionRomance = view.findViewById(R.id.option_romance)
        optionMystery = view.findViewById(R.id.option_mystery)
        optionFantasy = view.findViewById(R.id.option_fantasy)
        optionScienceFiction = view.findViewById(R.id.option_science_fiction)
        optionThriller = view.findViewById(R.id.option_thriller)
        optionHistoricalFiction = view.findViewById(R.id.option_historical_fiction)

        // Start Writing Button
        startWritingButton = view.findViewById(R.id.start_writing_button)


        // --- Set up all click listeners ---

        // Language section
        languageRow.setOnClickListener {
            toggleOptions(languageOptionsContainer, languageArrow)
        }
        optionAmericanEnglish.setOnClickListener {
            selectOption(languageText, languageOptionsContainer, languageArrow, optionAmericanEnglish.text.toString())
        }
        optionUkEnglish.setOnClickListener {
            selectOption(languageText, languageOptionsContainer, languageArrow, optionUkEnglish.text.toString())
        }

        // Book Type section
        bookTypeRow.setOnClickListener {
            toggleOptions(bookTypeOptionsContainer, bookTypeArrow)
        }
        optionFanFiction.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, optionFanFiction.text.toString())
        }
        varoptionNovels.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, varoptionNovels.text.toString())
        }
        optionShortStory.setOnClickListener {
            selectOption(bookTypeText, bookTypeOptionsContainer, bookTypeArrow, optionShortStory.text.toString())
        }

        // Genre section
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

        // Start Writing Button
        startWritingButton.setOnClickListener {
            // TODO: Implement the logic for starting to write
            // e.g., navigate to the next screen or save the selected data
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

    // Optional: a standard way to create a Fragment instance
    companion object {
        fun newInstance() = CompleteBookInfoFragment()
    }
}