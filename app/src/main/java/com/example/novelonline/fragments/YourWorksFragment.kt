package com.example.novelonline.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.R
import com.example.novelonline.adapters.BooksAdapter
import com.example.novelonline.databinding.FragmentYourWorksBinding
import com.example.novelonline.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class YourWorksFragment : Fragment() {

    private var _binding: FragmentYourWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var backArrow: TextView
    private lateinit var createNewBookButton: Button
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var myBooksTitle: TextView

    // Firebase instances
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    // Lists for holding books from different collections and the combined list
    private val books = mutableListOf<Book>()
    private val booksFromBooksCollection = mutableListOf<Book>()
    private val booksFromUploadedBooksCollection = mutableListOf<Book>()

    // Adapter for the combined list
    private lateinit var booksAdapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourWorksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        firestore = Firebase.firestore
        auth = Firebase.auth
        userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "You must be logged in to view your works.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        // Initialize UI elements
        backArrow = binding.backArrow
        createNewBookButton = binding.createNewBookButton
        booksRecyclerView = binding.booksRecyclerView
        myBooksTitle = binding.myBooks

        // --- Set up RecyclerView ---
        booksRecyclerView.layoutManager = LinearLayoutManager(context)
        booksAdapter = BooksAdapter(
            books,
            onItemClick = { book ->
                val novelId = book.id
                val pdfUrl = book.pdfUrl // Assume your 'Book' class has a 'pdfUrl' property

                // Use Safe Args to navigate and pass the arguments.
                val action = YourWorksFragmentDirections.actionYourWorksFragmentToEditBookDetailFragment2(
                    novelId = novelId,
                    pdfUrl = pdfUrl
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { book ->
                showDeleteConfirmationDialog(
                    "Are you sure you want to delete the book \"${book.title}\"?",
                    "Deleting this book will also delete all its chapters. This action cannot be undone.",
                    book.id,
                    book.sourceCollection // Pass the source collection for deletion
                )
            }
        )
        booksRecyclerView.adapter = booksAdapter

        // --- Fetch data from Firestore ---
        listenForBooks()

        // --- Set up click listeners ---
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        createNewBookButton.setOnClickListener {
            findNavController().navigate(R.id.action_yourWorksFragment_to_becomeWriterFragment)
        }
    }

    /**
     * Listens for real-time changes to the user's books from both "books" and "uploaded books"
     * Firestore collections and combines them into a single list.
     */
    private fun listenForBooks() {
        if (userId == null) return

        // Listen to the "books" collection
        firestore.collection("books")
            .whereEqualTo("authorId", userId)
            .addSnapshotListener { booksSnapshot, e ->
                if (e != null) {
                    Log.w("YourWorksFragment", "Listen failed for 'books' collection.", e)
                    return@addSnapshotListener
                }

                if (booksSnapshot != null) {
                    booksFromBooksCollection.clear()
                    for (doc in booksSnapshot.documents) {
                        // --- CORRECTED: Use the document ID for the 'id' field ---
                        val book = doc.toObject(Book::class.java)?.copy(id = doc.id, sourceCollection = "books")
                        book?.let {
                            booksFromBooksCollection.add(it)
                        }
                    }
                    updateCombinedListAndNotify()
                } else {
                    Log.d("YourWorksFragment", "Books snapshot is null.")
                }
            }

        // Listen to the "uploaded books" collection
        firestore.collection("uploaded books")
            .whereEqualTo("authorId", userId)
            .addSnapshotListener { uploadedBooksSnapshot, e2 ->
                if (e2 != null) {
                    Log.w("YourWorksFragment", "Listen failed for 'uploaded books' collection.", e2)
                    return@addSnapshotListener
                }

                if (uploadedBooksSnapshot != null) {
                    booksFromUploadedBooksCollection.clear()
                    for (doc in uploadedBooksSnapshot.documents) {
                        // --- CORRECTED: Use the document ID for the 'id' field ---
                        val book = doc.toObject(Book::class.java)?.copy(id = doc.id, sourceCollection = "uploaded books")
                        book?.let {
                            booksFromUploadedBooksCollection.add(it)
                        }
                    }
                    updateCombinedListAndNotify()
                } else {
                    Log.d("YourWorksFragment", "Uploaded books snapshot is null.")
                }
            }
    }

    private fun updateCombinedListAndNotify() {
        books.clear()
        books.addAll(booksFromBooksCollection)
        books.addAll(booksFromUploadedBooksCollection)
        // Sort the combined list by a relevant field, e.g., creation date
        books.sortByDescending { it.createdOn }
        booksAdapter.notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(title: String, message: String, itemId: String, itemType: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes, I'm sure") { dialog, _ ->
                showFinalDeleteConfirmation(itemId, itemType)
                dialog.dismiss()
            }
            .setNegativeButton("No, cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showFinalDeleteConfirmation(itemId: String, itemType: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Are you REALLY sure?")
            .setMessage("This will permanently delete this book and all associated data. This cannot be recovered.")
            .setPositiveButton("Yes, delete it!") { dialog, _ ->
                deleteItem(itemId, itemType)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteItem(bookId: String, sourceCollection: String) {
        firestore.collection(sourceCollection).document(bookId)
            .delete()
            .addOnSuccessListener {
                Log.d("YourWorksFragment", "Book deleted successfully from $sourceCollection.")
                Toast.makeText(requireContext(), "Book deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("YourWorksFragment", "Error deleting book from $sourceCollection: ", e)
                Toast.makeText(requireContext(), "Failed to delete book.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}