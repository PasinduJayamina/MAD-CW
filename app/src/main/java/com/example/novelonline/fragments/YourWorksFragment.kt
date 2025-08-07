package com.example.novelonline.fragments // Adjust package as needed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.R
import com.example.novelonline.adapters.BooksAdapter
import com.example.novelonline.adapters.UnpublishedChaptersAdapter
import com.example.novelonline.databinding.FragmentYourWorksBinding
import com.example.novelonline.models.Book
import com.example.novelonline.models.Chapter
import java.util.UUID

class YourWorksFragment : Fragment() {

    private var _binding: FragmentYourWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var backArrow: TextView
    private lateinit var createNewBookButton: Button
    private lateinit var unpublishedChaptersRecyclerView: RecyclerView
    private lateinit var booksRecyclerView: RecyclerView

    // Dummy data to simulate a data source. In a real app, this would be from Firestore.
    private val chapters = mutableListOf(
        Chapter("c1", "b1", "Chapter 1: The Beginning", "July 28, 2025", 1500),
        Chapter("c2", "b1", "Chapter 2: The Journey", "July 27, 2025", 2100),
        Chapter("c3", "b2", "Untitled Chapter", "July 26, 2025", 800)
    )
    private val books = mutableListOf(
        Book(
            id = "b1",
            title = "The Dragon's Ascent",
            author = "Your Name", // Added author
            coverImageUrl = "", // Must be a String, not null or a number
            chapterCount = 15,
            lastUpdated = "July 29, 2025",
            createdOn = "June 1, 2025"
        ),
        Book(
            id = "b2",
            title = "Eternal Embrace",
            author = "Your Name",
            coverImageUrl = "",
            chapterCount = 8,
            lastUpdated = "July 20, 2025",
            createdOn = "May 10, 2025"
        ),
        Book(
            id = "b3",
            title = "Echoes of Tomorrow",
            author = "Your Name",
            coverImageUrl = "",
            chapterCount = 20,
            lastUpdated = "July 25, 2025",
            createdOn = "April 1, 2025"
        )
    )

    // Adapters need to be class-level to be able to notify them of data changes
    private lateinit var unpublishedChaptersAdapter: UnpublishedChaptersAdapter
    private lateinit var booksAdapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourWorksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backArrow = binding.backArrow
        createNewBookButton = binding.createNewBookButton
        unpublishedChaptersRecyclerView = binding.unpublishedChaptersRecyclerView
        booksRecyclerView = binding.booksRecyclerView

        // --- Set up RecyclerViews ---

        // Unpublished Chapters RecyclerView
        unpublishedChaptersRecyclerView.layoutManager = LinearLayoutManager(context)
        unpublishedChaptersAdapter = UnpublishedChaptersAdapter(
            chapters,
            onItemClick = { chapter ->
                // TODO: Handle click on an unpublished chapter (e.g., open editor)
                Log.d("YourWorksFragment", "Chapter card clicked: ${chapter.title}")
            },
            onDeleteClick = { chapter ->
                showDeleteConfirmationDialog(
                    "Are you sure you want to delete this chapter?",
                    "This action cannot be undone.",
                    chapter.id,
                    "chapter"
                )
            }
        )
        unpublishedChaptersRecyclerView.adapter = unpublishedChaptersAdapter

        // Books RecyclerView
        booksRecyclerView.layoutManager = LinearLayoutManager(context)
        booksAdapter = BooksAdapter(
            books,
            onItemClick = { book ->
                // TODO: Handle click on a book (e.g., open book details, list published chapters)
                Log.d("YourWorksFragment", "Book card clicked: ${book.title}")
            },
            onDeleteClick = { book ->
                showDeleteConfirmationDialog(
                    "Are you sure you want to delete the book \"${book.title}\"?",
                    "Deleting a book will also delete all its chapters. This action cannot be undone.",
                    book.id,
                    "book"
                )
            }
        )
        booksRecyclerView.adapter = booksAdapter

        // --- Set up click listeners ---

        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        createNewBookButton.setOnClickListener {
            // TODO: Navigate to the "Become a Writer" screen or directly to "Add Title"
            // findNavController().navigate(R.id.action_yourWorks_to_becomeWriterFragment)
        }
    }

    // New function to show a delete confirmation dialog
    private fun showDeleteConfirmationDialog(title: String, message: String, itemId: String, itemType: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes, I'm sure") { dialog, _ ->
                // Show a second, more serious confirmation dialog
                showFinalDeleteConfirmation(itemId, itemType)
                dialog.dismiss()
            }
            .setNegativeButton("No, cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // New function for the final, more serious confirmation dialog
    private fun showFinalDeleteConfirmation(itemId: String, itemType: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Are you REALLY sure?")
            .setMessage("This will permanently delete this $itemType and all associated data. This cannot be recovered.")
            .setPositiveButton("Yes, delete it!") { dialog, _ ->
                // Call the actual deletion logic
                deleteItem(itemId, itemType)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // New function to handle the actual deletion logic
    private fun deleteItem(itemId: String, itemType: String) {
        // Here you would implement your Firebase Firestore deletion logic
        // For demonstration, we'll just remove the item from the dummy list
        Log.d("YourWorksFragment", "Attempting to delete $itemType with ID: $itemId")

        if (itemType == "book") {
            val bookToRemove = books.find { it.id == itemId }
            if (bookToRemove != null) {
                val index = books.indexOf(bookToRemove)
                books.removeAt(index)
                booksAdapter.notifyItemRemoved(index)
                Log.d("YourWorksFragment", "Book deleted: ${bookToRemove.title}")
            }
        } else if (itemType == "chapter") {
            val chapterToRemove = chapters.find { it.id == itemId }
            if (chapterToRemove != null) {
                val index = chapters.indexOf(chapterToRemove)
                chapters.removeAt(index)
                unpublishedChaptersAdapter.notifyItemRemoved(index)
                Log.d("YourWorksFragment", "Chapter deleted: ${chapterToRemove.title}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = YourWorksFragment()
    }
}