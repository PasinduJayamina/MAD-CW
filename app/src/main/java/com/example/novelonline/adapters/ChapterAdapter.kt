package com.example.novelonline.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.novelonline.R
import com.example.novelonline.models.Chapter

class ChapterAdapter(private val chapters: List<Chapter>) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.chapterTitleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.title.text = "Chapter ${chapter.chapterNumber}: ${chapter.chapterTitle}"
        // TODO: Set an onClickListener here to navigate to the ReaderFragment
    }

    override fun getItemCount() = chapters.size
}