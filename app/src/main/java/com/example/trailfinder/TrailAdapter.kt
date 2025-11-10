package com.example.trailfinder.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trailfinder.R
import edu.vt.trailreview.util.TrailSeed  // <-- use your imported Trail

class TrailAdapter(
    private var trails: List<TrailSeed>,
    private val onItemClick: (TrailSeed) -> Unit
) : RecyclerView.Adapter<TrailAdapter.TrailViewHolder>() {

    inner class TrailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tvTrailName)
        private val distance: TextView = itemView.findViewById(R.id.tvTrailDistance)
        private val difficulty: TextView = itemView.findViewById(R.id.tvTrailDifficulty)

        fun bind(trail: TrailSeed) {
            name.text = trail.name
            distance.text = "Distance: ${trail.distanceKm}"
            difficulty.text = "Difficulty: ${trail.difficulty}"
            itemView.setOnClickListener { onItemClick(trail) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trail, parent, false)
        return TrailViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailViewHolder, position: Int) {
        holder.bind(trails[position])
    }

    override fun getItemCount() = trails.size

    fun updateList(newList: List<TrailSeed>) {
        trails = newList
        notifyDataSetChanged()
    }
}