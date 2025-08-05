package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.databinding.ItemReviewBinding
import com.example.novelonline.models.Review
import java.text.SimpleDateFormat
import java.util.Locale

// This is the Kotlin class definition for your adapter.
class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    // This is a nested class inside ReviewAdapter.
    class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    // This is another nested class for calculating differences.
    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }

    // This is a function (method) of the ReviewAdapter class.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // This is another function of the ReviewAdapter class.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position)
        with(holder.binding) {
            usernameTextView.text = review.username
            ratingBar.rating = review.rating
            reviewTextView.text = review.reviewText

            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            dateTextView.text = sdf.format(review.timestamp)
        }
    }
}