package com.example.trailfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(
    private var reviews: List<RatingEntity>
) : RecyclerView.Adapter<ReviewAdapter.ReviewHolder>() {

    inner class ReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stars: TextView = itemView.findViewById(R.id.reviewStars)
        val tags: TextView = itemView.findViewById(R.id.reviewTags)
        val comment: TextView = itemView.findViewById(R.id.reviewComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {
        val r = reviews[position]
        holder.stars.text = "⭐".repeat(r.stars)
        holder.tags.text = r.tags ?: ""
        holder.tags.visibility = if (r.tags.isNullOrBlank()) View.GONE else View.VISIBLE
        holder.comment.text = r.comment ?: "(No comment)"
    }

    override fun getItemCount(): Int = reviews.size

    fun update(newReviews: List<RatingEntity>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}