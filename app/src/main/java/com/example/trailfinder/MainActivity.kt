package com.example.trailfinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private lateinit var listFragment: ListFragment
    private lateinit var profileFragment: ProfileFragment
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Seed DB only once
        lifecycleScope.launch {
            SeedHelper.seedDatabaseIfEmpty(this@MainActivity)
        }

        // ✅ Initialize fragments
        mapFragment = MapFragment()
        listFragment = ListFragment()
        profileFragment = ProfileFragment()

        // ✅ Attach to their respective containers
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_map, mapFragment, "map")
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_list, listFragment, "list")
            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_profile, profileFragment, "profile")
            .commit()

        // Show map container by default
        showContainer("map")

        // ✅ Handle bottom nav
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    showContainer("map")
                    true
                }
                R.id.nav_list -> {
                    showContainer("list")
                    true
                }
                R.id.nav_profile -> {
                    showContainer("profile")
                    true
                }
                else -> false
            }
        }
    }

    // ✅ Helper function to toggle container visibility
    private fun showContainer(tag: String) {
        val mapView = findViewById<android.view.View>(R.id.container_map)
        val listView = findViewById<android.view.View>(R.id.container_list)
        val profileView = findViewById<android.view.View>(R.id.container_profile)

        mapView.visibility = if (tag == "map") android.view.View.VISIBLE else android.view.View.GONE
        listView.visibility = if (tag == "list") android.view.View.VISIBLE else android.view.View.GONE
        profileView.visibility = if (tag == "profile") android.view.View.VISIBLE else android.view.View.GONE
    }

    // 👉 Used by LIST tab (ListFragment)
    fun navigateToTrailDetail(args: Bundle) {
        val fragment = TrailDetailFragment()
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_list, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 👉 NEW: used by MAP tab (MapFragment)
    fun navigateToTrailDetailFromMap(args: Bundle) {
        val fragment = TrailDetailFragment()
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_map, fragment)   // ⬅️ use MAP container here
            .addToBackStack(null)
            .commit()
    }
}
