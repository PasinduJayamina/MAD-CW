package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.novelonline.databinding.ItemBookCardGridBinding
import com.example.novelonline.databinding.ItemBookCardHorizontalBinding
import com.example.novelonline.databinding.ItemBookCardVerticalBinding
import com.example.novelonline.models.Book

// A versatile adapter that can handle different view types for books.
class BookAdapter(
    private val viewType: Int,
    private val onBookClicked: (Book) -> Unit
) : ListAdapter<Book, RecyclerView.ViewHolder>(BookDiffCallback()) {

    companion object {
        const val VIEW_TYPE_HORIZONTAL = 1
        const val VIEW_TYPE_VERTICAL = 2
        const val VIEW_TYPE_GRID = 3
    }

    // ViewHolder for horizontal book items
    inner class HorizontalViewHolder(private val binding: ItemBookCardHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            Glide.with(itemView.context).load(book.coverImageUrl).into(binding.ivBookCover)
            binding.root.setOnClickListener { onBookClicked(book) }
        }
    }

    // ViewHolder for vertical book items
    inner class VerticalViewHolder(private val binding: ItemBookCardVerticalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            binding.tvBookAuthor.text = book.author
            Glide.with(itemView.context).load(book.coverImageUrl).into(binding.ivBookCover)
            binding.root.setOnClickListener { onBookClicked(book) }
        }
    }

    // ViewHolder for grid book items
    inner class GridViewHolder(private val binding: ItemBookCardGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.tvBookTitle.text = book.title
            Glide.with(itemView.context).load(book.coverImageUrl).into(binding.ivBookCover)
            binding.root.setOnClickListener { onBookClicked(book) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HORIZONTAL -> HorizontalViewHolder(ItemBookCardHorizontalBinding.inflate(inflater, parent, false))
            VIEW_TYPE_VERTICAL -> VerticalViewHolder(ItemBookCardVerticalBinding.inflate(inflater, parent, false))
            VIEW_TYPE_GRID -> GridViewHolder(ItemBookCardGridBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val book = getItem(position)
        when (holder) {
            is HorizontalViewHolder -> holder.bind(book)
            is VerticalViewHolder -> holder.bind(book)
            is GridViewHolder -> holder.bind(book)
        }
    }
}

// DiffUtil helps the ListAdapter determine which items in the list have changed.
class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}