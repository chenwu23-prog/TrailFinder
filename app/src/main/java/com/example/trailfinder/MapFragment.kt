package com.example.trailfinder

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.tileprovider.tilesource.XYTileSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.views.overlay.TilesOverlay
import androidx.preference.PreferenceManager

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private var isTopoMode = false
    private var hikingOverlay: TilesOverlay? = null
    private val TAG = "TrailFinderMap"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map)

        // 🗺️ Base map
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(13.5)
        mapView.controller.setCenter(GeoPoint(37.2296, -80.4139)) // Blacksburg

        // 🥾 Hiking overlay
        val hikingTileSource = XYTileSource(
            "Hiking",
            0, 17, 256, ".png",
            arrayOf("https://tile.waymarkedtrails.org/hiking/")
        )
        val hikingProvider = MapTileProviderBasic(requireContext(), hikingTileSource)
        hikingOverlay = TilesOverlay(hikingProvider, requireContext()).apply {
            loadingBackgroundColor = Color.TRANSPARENT
            setLoadingLineColor(Color.TRANSPARENT)
        }

        // 🔘 Toggle topo map button
        val toggleButton = view.findViewById<FloatingActionButton>(R.id.btnToggleMap)
        toggleButton.setOnClickListener {
            isTopoMode = !isTopoMode
            toggleMapMode()
        }

        // 🧭 Load and display trail markers
        loadTrailMarkers()

        // ✅ Close popups when tapping on map
        mapView.overlays.add(object : org.osmdroid.views.overlay.Overlay() {
            override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                InfoWindow.closeAllInfoWindowsOn(mapView)
                return false
            }
        })

        return view
    }

    // 🔄 Toggle topo/hiking map
    private fun toggleMapMode() {
        if (isTopoMode) {
            Log.d(TAG, "Switching to OpenTopoMap + Hiking overlay")
            val topoSource = XYTileSource(
                "OpenTopoMap",
                0, 19, 256, ".png",
                arrayOf(
                    "https://a.tile.opentopomap.org/",
                    "https://b.tile.opentopomap.org/",
                    "https://c.tile.opentopomap.org/"
                )
            )
            mapView.setTileSource(topoSource)
            if (hikingOverlay != null && !mapView.overlays.contains(hikingOverlay)) {
                mapView.overlays.add(0, hikingOverlay) // keep under pins
            }
        } else {
            Log.d(TAG, "Switching to MAPNIK (Street map)")
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            hikingOverlay?.let { mapView.overlays.remove(it) }
        }
        mapView.invalidate()
    }

    // 📍 Load markers from DB
    private fun loadTrailMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())

            val trails = withContext(Dispatchers.IO) {
                var list = db.trailDao().getAllTrails()
                if (list.isEmpty()) {
                    // Seed sample data if empty
                    val seed = listOf(
                        TrailEntity(
                            name = "Huckleberry Trail",
                            lat = 37.2108,
                            lng = -80.4089,
                            distance = 7.0,
                            difficulty = "Easy"
                        ),
                        TrailEntity(
                            name = "Pandapas Pond Loop",
                            lat = 37.2803,
                            lng = -80.4751,
                            distance = 3.6,
                            difficulty = "Moderate"
                        ),
                        TrailEntity(
                            name = "Brush Mountain Trail",
                            lat = 37.2619,
                            lng = -80.4500,
                            distance = 5.5,
                            difficulty = "Hard"
                        )
                    )
                    db.trailDao().insertAll(seed)
                    list = seed
                }
                list
            }

            Log.d(TAG, "Loaded ${trails.size} trails from DB")
            mapView.overlays.removeAll { it is Marker } // Clear old pins

            val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_24)
            for (trail in trails) {
                val marker = Marker(mapView).apply {
                    position = GeoPoint(trail.lat, trail.lng)
                    title = trail.name
                    icon = iconDrawable
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    infoWindow = TrailInfoWindow(mapView, trail)
                    setOnMarkerClickListener { m, _ ->
                        m.showInfoWindow()
                        true
                    }
                }
                mapView.overlays.add(marker)
            }

            mapView.invalidate()
        }
    }

    // 💬 Info popup
    inner class TrailInfoWindow(mapView: MapView, private val trail: TrailEntity) :
        InfoWindow(R.layout.trail_info_window_osm, mapView) {
        override fun onOpen(item: Any?) {
            mView.findViewById<TextView>(R.id.trailName).text = trail.name
            mView.findViewById<TextView>(R.id.trailDistance).text = "Distance: ${trail.distance} mi"
            mView.findViewById<TextView>(R.id.trailDifficulty).apply {
                text = "Difficulty: ${trail.difficulty}"
                setTextColor(
                    when (trail.difficulty.lowercase()) {
                        "easy" -> Color.parseColor("#4CAF50")
                        "moderate" -> Color.parseColor("#FFC107")
                        "hard" -> Color.parseColor("#F44336")
                        else -> Color.DKGRAY
                    }
                )
            }
        }
        override fun onClose() {}
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}