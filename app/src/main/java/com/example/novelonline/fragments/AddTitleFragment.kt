package com.example.novelonline.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button // Import Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentAddTitleBinding

class AddTitleFragment : Fragment() {

    private var _binding: FragmentAddTitleBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var backArrow: TextView
    private lateinit var addTitleButton: Button // Changed to Button
    private lateinit var mainTitleEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views using the binding object
        backArrow = binding.root.findViewById(R.id.back_arrow)
        addTitleButton = binding.root.findViewById(R.id.add_title_button) // Initialize as Button
        mainTitleEditText = binding.root.findViewById(R.id.main_title_edit_text)

        // Set click listener for the back arrow
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Set click listener for the "Add Title" button
        addTitleButton.setOnClickListener {
            mainTitleEditText.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mainTitleEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddTitleFragment()
    }
}