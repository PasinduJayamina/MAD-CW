package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.novelonline.databinding.ItemGenreGridBinding
import com.example.novelonline.models.Genre

class GenreAdapter(private val onGenreClicked: (Genre) -> Unit) :
    ListAdapter<Genre, GenreAdapter.GenreViewHolder>(GenreDiffCallback()) {

    inner class GenreViewHolder(private val binding: ItemGenreGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            // Glide to load the genre icon
            Glide.with(itemView.context)
                .load(genre.iconUrl)
                .into(binding.ivGenreIcon)

            binding.root.setOnClickListener {
                onGenreClicked(genre)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemGenreGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class GenreDiffCallback : DiffUtil.ItemCallback<Genre>() {
    override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem == newItem
    }
}