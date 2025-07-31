package com.example.novelonline.adapters // Adjust package as needed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.R
import com.example.novelonline.models.Chapter // Import your Chapter data class

class UnpublishedChaptersAdapter(
    private val chapters: List<Chapter>,
    private val onItemClick: (Chapter) -> Unit, // Lambda for click listener
    private val onDeleteClick: (Chapter) -> Unit
) : RecyclerView.Adapter<UnpublishedChaptersAdapter.ChapterViewHolder>() {

    inner class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterTitleTextView: TextView = itemView.findViewById(R.id.chapter_title_text_view)
        val savedDateTextView: TextView = itemView.findViewById(R.id.saved_date_text_view)
        val wordCountTextView: TextView = itemView.findViewById(R.id.word_count_text_view)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_chapter_button) // NEW view


        init {
            itemView.setOnClickListener {
                onItemClick(chapters[adapterPosition])
            }
            deleteButton.setOnClickListener {
                onDeleteClick(chapters[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unpublished_chapter_card, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.chapterTitleTextView.text = chapter.title
        holder.savedDateTextView.text = "Saved: ${chapter.savedDate}"
        holder.wordCountTextView.text = "Words: ${chapter.wordCount}"
    }

    override fun getItemCount(): Int = chapters.size
}