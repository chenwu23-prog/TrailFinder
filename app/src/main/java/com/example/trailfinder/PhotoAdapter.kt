package com.example.trailfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val photos: List<String>,
    private val onPhotoClick: (String) -> Unit    // 👈 NEW
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uriString = photos[position]

        Glide.with(holder.itemView.context)
            .load(uriString)
            .centerCrop()
            .into(holder.image)

        // 👇 Handle click to zoom
        holder.itemView.setOnClickListener {
            onPhotoClick(uriString)
        }
    }

    override fun getItemCount(): Int = photos.size
}
