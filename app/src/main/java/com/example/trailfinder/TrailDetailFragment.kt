package com.example.trailfinder

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class TrailDetailFragment : Fragment() {

    private var trailId: Int = -1
    private lateinit var ratingSummary: TextView

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

        val toolbar = view.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.detailToolbar)
        toolbar.setNavigationOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

        val photo = view.findViewById<ImageView>(R.id.trailPhoto)
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
                if (!it.photoUri.isNullOrEmpty()) {
                    photo.visibility = View.VISIBLE
                    mapPreview.visibility = View.GONE
                    photo.setImageURI(Uri.parse(it.photoUri))
                } else {
                    photo.visibility = View.GONE
                    mapPreview.visibility = View.VISIBLE
                }

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

                mapPreview.controller.setZoom(14.0)
                mapPreview.controller.setCenter(GeoPoint(it.lat, it.lng))
            }

            refreshRatings()
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

            // ⭐ Average stars
            val avg = ratings.map { it.stars }.average()
            val stars = "⭐".repeat(avg.toInt()) + "☆".repeat(5 - avg.toInt())

            // 💬 Get the latest 2 or 3 comments
            val recentComments = ratings
                .mapNotNull { it.comment }
                .filter { it.isNotBlank() }
                .take(3)        // ← change to 2 if you want only two

            // 📝 Build display text for comments
            val commentsBlock = if (recentComments.isEmpty()) {
                ""
            } else {
                "\n" + recentComments.joinToString(
                    separator = "\n• ",
                    prefix = "• "
                )
            }

            // Combine stars + comments
            ratingSummary.text = "$stars (${String.format("%.1f", avg)})$commentsBlock"
        }
    }

}

