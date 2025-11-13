package com.example.trailfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrailListAdapter(
    private var trails: List<TrailEntity>,
    private val onItemClick: (TrailEntity) -> Unit   // 🔥 click callback
) : RecyclerView.Adapter<TrailListAdapter.TrailViewHolder>() {

    inner class TrailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.itemTrailName)
        val distance: TextView = itemView.findViewById(R.id.itemTrailDistance)
        val difficulty: TextView = itemView.findViewById(R.id.itemTrailDifficulty)

        fun bind(trail: TrailEntity) {
            name.text = trail.name
            distance.text = "Distance: ${trail.distance} mi"
            difficulty.text = trail.difficulty

            // 🔥 Send item clicked back to Fragment
            itemView.setOnClickListener {
                onItemClick(trail)
            }

            // (Optional) Difficulty color coding
            when (trail.difficulty.lowercase()) {
                "easy" -> difficulty.setTextColor(0xFF4CAF50.toInt())      // green
                "moderate" -> difficulty.setTextColor(0xFFFF9800.toInt()) // orange
                "hard" -> difficulty.setTextColor(0xFFF44336.toInt())     // red
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trail_list_item, parent, false)
        return TrailViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailViewHolder, position: Int) {
        holder.bind(trails[position])
    }

    override fun getItemCount(): Int = trails.size

    // 🔄 Allow updating list after sorting or filtering
    fun updateList(newList: List<TrailEntity>) {
        trails = newList
        notifyDataSetChanged()
    }
}
