package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.database.OfflineBook
import com.example.novelonline.databinding.ItemOfflineBookBinding

class OfflineBookAdapter(
    private val onBookClicked: (OfflineBook) -> Unit
) : ListAdapter<OfflineBook, OfflineBookAdapter.OfflineBookViewHolder>(OfflineBookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineBookViewHolder {
        val binding = ItemOfflineBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfflineBookViewHolder(binding, onBookClicked)
    }

    override fun onBindViewHolder(holder: OfflineBookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

    class OfflineBookViewHolder(
        private val binding: ItemOfflineBookBinding,
        private val onBookClicked: (OfflineBook) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: OfflineBook) {
            binding.bookTitleTextView.text = book.title
            binding.bookAuthorTextView.text = book.author

            // Load book cover image using Glide
            Glide.with(binding.root.context)
                .load(book.coverImageUrl)
                .placeholder(R.drawable.`placeholder_book_cover`)
                .error(R.drawable.`placeholder_book_cover`)
                .into(binding.bookCoverImageView)

            // Set the click listener for the whole item
            binding.root.setOnClickListener {
                onBookClicked(book)
            }
        }
    }
}

private class OfflineBookDiffCallback : DiffUtil.ItemCallback<OfflineBook>() {
    override fun areItemsTheSame(oldItem: OfflineBook, newItem: OfflineBook): Boolean {
        return oldItem.bookId == newItem.bookId
    }

    override fun areContentsTheSame(oldItem: OfflineBook, newItem: OfflineBook): Boolean {
        return oldItem == newItem
    }
}
