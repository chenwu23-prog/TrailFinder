package edu.vt.trailreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.vt.trailreview.ui.MapFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load MapFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MapFragment())
            .commit()

        // ADD ↓ THIS LINE INSIDE A COROUTINE SCOPE
        lifecycleScope.launch {
            SeedHelper.seedDatabaseIfEmpty(this@MainActivity)
        }
    }
}