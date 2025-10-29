package edu.vt.trailreview.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.vt.trailreview.R
import edu.vt.trailreview.util.JsonLoader
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {

    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load osmdroid configuration
        Configuration.getInstance().load(
            requireContext(),
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        // Center the map (Virginia Tech coordinates)
        val center = GeoPoint(37.2296, -80.4139)
        map.controller.setZoom(12.0)
        map.controller.setCenter(center)

        // Load trails and add markers
        val trails = JsonLoader.loadTrails(requireContext())
        for (trail in trails) {
            val marker = Marker(map)
            marker.position = GeoPoint(trail.latitude, trail.longitude)
            marker.title = trail.name
            map.overlays.add(marker)
        }
    }
}