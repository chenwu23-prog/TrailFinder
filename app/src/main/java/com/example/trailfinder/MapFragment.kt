package com.example.trailfinder

import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import com.example.trailfinder.model.Trail
import com.example.trailfinder.R
import android.widget.Toast
import org.osmdroid.views.overlay.Overlay
import android.view.MotionEvent


class MapFragment : Fragment(R.layout.fragment_map) {
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", 0)
        )

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map)
        mapView.setMultiTouchControls(true)

        // Add markers after map is ready
        addTrailMarkers()

        return view
    }

    private fun addTrailMarkers() {
        // Replace this with DB or JSON loader
        val trails = listOf(
            Trail(1, "Heritage Trail", 3.2, "Easy", 37.2296, -80.4139),
            Trail(2, "Cascade Falls", 4.0, "Moderate", 37.3531, -80.5990),
            Trail(3, "Brush Mountain", 5.5, "Hard", 37.2619, -80.4500)
        )

        for (trail in trails) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(trail.latitude, trail.longitude)
            marker.title = trail.name
            marker.subDescription =
                "Distance: ${trail.distance} mi\nDifficulty: ${trail.difficulty}"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            // Tag the marker with its trail
            marker.id = trail.id.toString()

            // Show popup on click
            marker.setOnMarkerClickListener { m, _ ->
                m.showInfoWindow()
                true
            }

            // When popup (info window) itself is tapped:
            marker.infoWindow = object : MarkerInfoWindow(R.layout.trail_info_window_osm, mapView) {
                override fun onOpen(item: Any?) {
                    super.onOpen(item)
                    val t = trails.find { it.id.toString() == (item as Marker).id }
                    if (t != null) {
                        val title = mView.findViewById<TextView>(R.id.trailName)
                        val distance = mView.findViewById<TextView>(R.id.trailDistance)
                        val difficulty = mView.findViewById<TextView>(R.id.trailDifficulty)
                        title.text = t.name
                        distance.text = "${t.distance} miles"
                        difficulty.text = t.difficulty
                    }

                    // Handle tapping popup itself:
                    mView.setOnClickListener {
                        val intent = Intent(requireContext(), TrailInfoActivity::class.java)
                        intent.putExtra("trail_id", (item as Marker).id.toInt())
                        startActivity(intent)
                    }
                }
            }

            mapView.overlays.add(marker)
        }

        mapView.overlays.add(object : Overlay() {
            override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                mapView?.overlays?.forEach {
                    if (it is Marker) it.closeInfoWindow()
                }
                return super.onSingleTapConfirmed(e, mapView)
            }
        })

        mapView.invalidate()
    }
}