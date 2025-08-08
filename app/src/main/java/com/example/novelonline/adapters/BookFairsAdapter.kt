package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.databinding.ItemBookFairBinding
import com.example.novelonline.models.BookFair

class BookFairsAdapter(
    private val bookFairs: List<BookFair>,
    private val onItemClick: (BookFair) -> Unit
) : RecyclerView.Adapter<BookFairsAdapter.BookFairViewHolder>() {

    inner class BookFairViewHolder(private val binding: ItemBookFairBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookFair: BookFair) {
            binding.fairNameTextView.text = bookFair.name
            binding.fairAddressTextView.text = bookFair.address
            binding.fairContactTextView.text = "Contact: ${bookFair.contact}"
            binding.fairDatesTextView.text = "Starts: ${bookFair.startDate} - Closes: ${bookFair.endDate}"

            // Set up the click listener for the card
            binding.bookFairCard.setOnClickListener {
                onItemClick(bookFair)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookFairViewHolder {
        val binding = ItemBookFairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookFairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookFairViewHolder, position: Int) {
        holder.bind(bookFairs[position])
    }

    override fun getItemCount(): Int {
        return bookFairs.size
    }
}