package com.example.novelonline.adapters // Adjust package as needed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.models.Book // Import your Book data class

// If you use Glide for image loading:
// import com.bumptech.glide.Glide
// import android.widget.ImageView

class BooksAdapter(
    private val books: List<Book>,
    private val onItemClick: (Book) -> Unit, // Lambda for click listener //
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCoverImageView: ImageView = itemView.findViewById(R.id.book_cover_image_view) // If you add ImageView
        val bookTitleTextView: TextView = itemView.findViewById(R.id.book_title_text_view)
        val chapterCountTextView: TextView = itemView.findViewById(R.id.chapter_count_text_view)
        val lastUpdatedTextView: TextView = itemView.findViewById(R.id.last_updated_text_view)
        val createdOnTextView: TextView = itemView.findViewById(R.id.created_on_text_view)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_book_button) // NEW view


        init {
            itemView.setOnClickListener {
                onItemClick(books[adapterPosition])
            }
            deleteButton.setOnClickListener {
                onDeleteClick(books[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_card, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bookTitleTextView.text = book.title
        holder.chapterCountTextView.text = "Chapters: ${book.chapterCount}"
        holder.lastUpdatedTextView.text = "Last Updated: ${book.lastUpdated}"
        holder.createdOnTextView.text = "Created On: ${book.createdOn}"

        // Load book cover image (if you're using Glide/Coil)
        book.coverImageUrl?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                // Optional: set a placeholder and error image
                .placeholder(R.drawable.placeholder_book_cover)
                .error(R.drawable.baseline_error_outline_24)
                .into(holder.bookCoverImageView)
        } ?: run {
            // Set a default image if coverUrl is null
            holder.bookCoverImageView.setImageResource(R.drawable.baseline_book_placeholder_24)
        } // Default if no URL
    }

    override fun getItemCount(): Int = books.size
}