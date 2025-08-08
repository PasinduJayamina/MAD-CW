package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.databinding.ItemBookFairBinding
import com.example.novelonline.models.BookFair
import java.text.DecimalFormat

class BookFairsAdapter(
    private var bookFairs: List<BookFair>,
    private val onItemClick: (BookFair) -> Unit,
    private val onMapClick: (BookFair) -> Unit
) : RecyclerView.Adapter<BookFairsAdapter.BookFairViewHolder>() {

    inner class BookFairViewHolder(private val binding: ItemBookFairBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookFair: BookFair) {
            binding.fairNameTextView.text = bookFair.name
            binding.fairAddressTextView.text = bookFair.address
            binding.fairContactTextView.text = "Contact: ${bookFair.contact}"
            binding.fairDatesTextView.text = "Starts: ${bookFair.startDate} - Closes: ${bookFair.endDate}"

            // Display the most accurate distance available
            when {
                bookFair.drivingDistance != null -> {
                    binding.fairDistanceTextView.text = "${bookFair.drivingDistance} away"
                }
                bookFair.straightLineDistanceKm != null -> {
                    val df = DecimalFormat("#.##")
                    binding.fairDistanceTextView.text = "${df.format(bookFair.straightLineDistanceKm)} km away (approx.)"
                }
                else -> {
                    binding.fairDistanceTextView.text = "Calculating distance..."
                }
            }

            binding.root.setOnClickListener { onItemClick(bookFair) }
            binding.mapIcon.setOnClickListener { onMapClick(bookFair) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookFairViewHolder {
        val binding =
            ItemBookFairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookFairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookFairViewHolder, position: Int) {
        holder.bind(bookFairs[position])
    }

    override fun getItemCount(): Int {
        return bookFairs.size
    }

    fun updateData(newBookFairs: List<BookFair>) {
        this.bookFairs = newBookFairs
        notifyDataSetChanged()
    }
}