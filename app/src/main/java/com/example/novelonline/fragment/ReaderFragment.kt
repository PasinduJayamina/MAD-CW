package com.example.novelonline.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.novelonline.databinding.FragmentReaderBinding

class ReaderFragment : Fragment() {

    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!

    private var isDarkMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load dummy text for now
        binding.contentText.text = "Here begins the long and exciting story of Chapter 1... ".repeat(100)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.increaseFontButton.setOnClickListener {
            changeFontSize(2f) // Increase by 2sp
        }

        binding.decreaseFontButton.setOnClickListener {
            changeFontSize(-2f) // Decrease by 2sp
        }

        binding.toggleThemeButton.setOnClickListener {
            isDarkMode = !isDarkMode
            if (isDarkMode) {
                // Dark Mode
                binding.readerContainer.setBackgroundColor(Color.BLACK)
                binding.contentText.setTextColor(Color.WHITE)
            } else {
                // Light Mode
                binding.readerContainer.setBackgroundColor(Color.WHITE)
                binding.contentText.setTextColor(Color.BLACK)
            }
        }
    }

    private fun changeFontSize(amount: Float) {
        val currentSize = binding.contentText.textSize // size in pixels
        // Convert to SP and add the amount
        val newSizeInSP = (currentSize / resources.displayMetrics.scaledDensity) + amount
        binding.contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSizeInSP)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}