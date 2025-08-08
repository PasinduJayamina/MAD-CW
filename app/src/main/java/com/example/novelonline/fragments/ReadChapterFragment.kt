package com.example.novelonline.fragments // Adjust package as needed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentReadChapterBinding
import com.google.android.material.color.MaterialColors

class ReadChapterFragment : Fragment() {

    // Use a nullable binding to prevent memory leaks,
    // and a non-null accessor for convenience.
    private var _binding: FragmentReadChapterBinding? = null
    private val binding get() = _binding!!

    // Properties to hold the novel and chapter IDs
    private var novelId: String? = null
    private var chapterId: String? = null

    // Use `sp` for font size to respect user settings, and manage it as a float.
    // The default font size should be a reasonable starting point.
    private var currentFontSizeSp: Float = 18f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the arguments, which is the correct place for this.
        arguments?.let {
            novelId = it.getString(ARG_NOVEL_ID)
            chapterId = it.getString(ARG_CHAPTER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding.
        _binding = FragmentReadChapterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        // It's good to call this from onViewCreated to ensure the view is ready.
        // TODO: fetchChapterContent(novelId, chapterId)
    }

    private fun setupClickListeners() {
        // Back button to navigate back. The ID is correctly referenced.
        binding.toolbar.findViewById<View>(R.id.backButton).setOnClickListener {
            findNavController().popBackStack()
        }

        // Settings button to show/hide the settings panel.
        binding.settingsButton.setOnClickListener {
            toggleSettingsPanelVisibility()
        }

        // Font size controls
        binding.increaseFontButton.setOnClickListener {
            // Check for a reasonable upper limit for the font size.
            if (currentFontSizeSp < 32f) {
                currentFontSizeSp += 2f
                binding.contentTextView.textSize = currentFontSizeSp
            }
        }

        binding.decreaseFontButton.setOnClickListener {
            // Check for a reasonable lower limit for the font size.
            if (currentFontSizeSp > 12f) {
                currentFontSizeSp -= 2f
                binding.contentTextView.textSize = currentFontSizeSp
            }
        }

        // Theme controls
        binding.lightModeButton.setOnClickListener {
            setTheme(isDarkMode = false)
        }

        binding.darkModeButton.setOnClickListener {
            setTheme(isDarkMode = true)
        }
    }

    private fun toggleSettingsPanelVisibility() {
        // Using `View.VISIBLE` and `View.GONE` is correct.
        if (binding.settingsPanel.visibility == View.GONE) {
            binding.settingsPanel.visibility = View.VISIBLE
        } else {
            binding.settingsPanel.visibility = View.GONE
        }
    }

    private fun setTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            // Set dark theme colors by referencing R.color
            binding.readerContainer.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_bg)
            )
            binding.contentTextView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_text)
            )
            binding.chapterTitleTextView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_text)
            )
            binding.backButton.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_text),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.settingsButton.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_text),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.settingsPanel.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.reader_dark_panel_bg)
            )
        } else {
            // Set light theme colors by referencing R.color
            binding.readerContainer.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.reader_light_bg)
            )
            binding.contentTextView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.reader_light_text)
            )
            binding.chapterTitleTextView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.reader_light_text)
            )
            binding.backButton.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.reader_light_text),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.settingsButton.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.reader_light_text),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            binding.settingsPanel.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.reader_panel_bg)
            )
        }
    }

    private fun fetchChapterContent(novelId: String?, chapterId: String?) {
        if (novelId == null || chapterId == null) {
            // It's good practice to handle the case where arguments are missing.
            // You might want to show an error message or navigate back.
            return
        }

        // TODO: Implement logic to fetch chapter content from a repository or database (e.g., Firebase)
        // For demonstration purposes, we will set placeholder text
        binding.chapterTitleTextView.text = "Chapter 1: The Beginning"
        binding.contentTextView.text = getString(R.string.placeholder_chapter_content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // This is crucial to prevent memory leaks.
        _binding = null
    }

    companion object {
        const val ARG_NOVEL_ID = "novelId"
        const val ARG_CHAPTER_ID = "chapterId"
    }
}