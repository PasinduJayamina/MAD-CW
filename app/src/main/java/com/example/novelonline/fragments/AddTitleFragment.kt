package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // Import Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentAddTitleBinding

class AddTitleFragment : Fragment() {

    private var _binding: FragmentAddTitleBinding? = null
    private val binding get() = _binding!!

    // Declare views
    private lateinit var backArrow: TextView
    private lateinit var mainTitleEditText: EditText
    private lateinit var addTitleButton: Button // Declare the button here

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views using the binding object directly (no need for findViewById)
        backArrow = binding.backArrow
        mainTitleEditText = binding.mainTitleEditText
        addTitleButton = binding.addTitleButton // Initialize the button here

        // Set click listener for the back arrow
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Set click listener for the "Add Title" button
        addTitleButton.setOnClickListener {
            val bookTitle = mainTitleEditText.text.toString().trim()

            // 1. Basic validation: Check if the title is not empty
            if (bookTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a book title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO 2. Perform navigation
            // To pass data (like the book title) to the next fragment, you should
            // use a Safe Args action in your nav_graph.xml.

            // Hypothetical Safe Args action call
            // val action = AddTitleFragmentDirections.actionAddTitleFragmentToCompleteBookInfoFragment(bookTitle)
            // findNavController().navigate(action)

            // For now, let's use a simple navigation call.
            // Ensure you have an action defined in your nav_graph.xml
            findNavController().navigate(R.id.action_addTitleFragment_to_completeBookInfoFragment)
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