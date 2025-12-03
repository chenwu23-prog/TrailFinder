package com.example.trailfinder.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trailfinder.R
import com.example.trailfinder.data.Badge

class BadgeAdapter : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    private val badges = mutableListOf<Badge>()

    // 🔥 THIS IS THE METHOD ProfileFragment CALLS
    fun setItems(list: List<Badge>) {
        badges.clear()
        badges.addAll(list)
        notifyDataSetChanged()
    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.badgeName)
        val description: TextView = itemView.findViewById(R.id.badgeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.name.text = badge.badgeName
        holder.description.text = badge.badgeDescription
    }

    override fun getItemCount(): Int = badges.size
}