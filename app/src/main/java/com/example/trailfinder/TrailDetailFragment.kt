package com.example.trailfinder

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class TrailDetailFragment : Fragment() {

    private var trailId: Int = -1
    private lateinit var ratingSummary: TextView
    private lateinit var photoGallery: RecyclerView

    // Photo picker
    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                addPhotoToTrail(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trailId = arguments?.getInt("trailId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_trail_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ratingSummary = view.findViewById(R.id.ratingSummary)

        val toolbar =
            view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.detailToolbar)
        toolbar.setNavigationOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

        // Photo gallery RecyclerView
        photoGallery = view.findViewById(R.id.photoGallery)
        photoGallery.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val btnAddPhoto = view.findViewById<Button>(R.id.btnAddPhoto)
        val mapPreview = view.findViewById<MapView>(R.id.mapPreview)
        val name = view.findViewById<TextView>(R.id.trailName)
        val distance = view.findViewById<TextView>(R.id.trailDistance)
        val difficulty = view.findViewById<TextView>(R.id.trailDifficulty)
        val description = view.findViewById<TextView>(R.id.trailDescription)
        val btnAddNote = view.findViewById<Button>(R.id.btnAddNote)

        mapPreview.setTileSource(TileSourceFactory.MAPNIK)
        mapPreview.setMultiTouchControls(false)

        val db = AppDatabase.getDatabase(requireContext())

        // Load trail details
        viewLifecycleOwner.lifecycleScope.launch {
            val trail = withContext(Dispatchers.IO) { db.trailDao().getTrailById(trailId) }

            trail?.let {

                // ----------------------------------------------
                // ⭐⭐ LOAD TEST PHOTOS FROM /sdcard/Pictures ⭐⭐
                // ----------------------------------------------
                val testUris = listOf(
                    "android.resource://com.example.trailfinder/${R.drawable.trail1}",
                    "android.resource://com.example.trailfinder/${R.drawable.trail2}",
                    "android.resource://com.example.trailfinder/${R.drawable.trail3}",
                    "android.resource://com.example.trailfinder/${R.drawable.trail4}",
                    "android.resource://com.example.trailfinder/${R.drawable.trail5}"
                )
                Log.d("PHOTO_DEBUG", "Using test photo URIs: $testUris")

                photoGallery.adapter = PhotoAdapter(testUris)
                photoGallery.visibility = View.VISIBLE
                mapPreview.visibility = View.GONE

                // ------------------------------
                // Text + difficulty badge
                // ------------------------------
                name.text = it.name
                distance.text = "${it.distance} miles"
                description.text = it.notes

                val color = when (it.difficulty.lowercase()) {
                    "easy" -> 0xFF4CAF50.toInt()
                    "moderate" -> 0xFFFF9800.toInt()
                    "hard" -> 0xFFF44336.toInt()
                    else -> 0xFF607D8B.toInt()
                }
                difficulty.setBackgroundColor(color)
                difficulty.text = it.difficulty

                // Map preview position
                mapPreview.controller.setZoom(14.0)
                mapPreview.controller.setCenter(GeoPoint(it.lat, it.lng))
            }

            refreshRatings()
        }

        // Add Photo
        btnAddPhoto.setOnClickListener {
            photoPicker.launch("image/*")
        }

        // Rating dialog
        btnAddNote.setOnClickListener {
            RatingDialogFragment.newInstance(trailId).apply {
                setOnDismissListener {
                    refreshRatings()
                }
            }.show(parentFragmentManager, "rateTrail")
        }
    }

    // Save added photo
    private fun addPhotoToTrail(uri: Uri) {
        val db = AppDatabase.getDatabase(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val trail = withContext(Dispatchers.IO) {
                db.trailDao().getTrailById(trailId)
            }

            trail?.let {
                val updatedList = it.photoUris + uri.toString()
                val updatedTrail = it.copy(photoUris = updatedList)

                withContext(Dispatchers.IO) {
                    db.trailDao().updateTrail(updatedTrail)
                }

                Toast.makeText(requireContext(), "Photo added!", Toast.LENGTH_SHORT).show()

                // Refresh gallery
                photoGallery.adapter = PhotoAdapter(updatedList)
                photoGallery.visibility = View.VISIBLE

                view?.findViewById<MapView>(R.id.mapPreview)?.visibility = View.GONE
            }
        }
    }

    // Ratings
    fun refreshRatings() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val ratings = withContext(Dispatchers.IO) {
                db.ratingDao().getRatingsForTrail(trailId)
            }

            if (ratings.isEmpty()) {
                ratingSummary.text = "No ratings yet"
                return@launch
            }

            val avg = ratings.map { it.stars }.average()
            val stars = "⭐".repeat(avg.toInt()) + "☆".repeat(5 - avg.toInt())

            val comments = ratings
                .mapNotNull { it.comment }
                .filter { it.isNotBlank() }
                .take(3)

            val commentsBlock = if (comments.isEmpty()) "" else
                "\n" + comments.joinToString("\n• ", prefix = "• ")

            ratingSummary.text = "$stars (${String.format("%.1f", avg)})$commentsBlock"
        }
    }
}
