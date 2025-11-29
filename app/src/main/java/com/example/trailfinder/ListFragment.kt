package com.example.trailfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sortSpinner: Spinner
    private lateinit var searchView: SearchView
    private lateinit var btnFilterEasy: Button   // ⭐ NEW

    private var allTrails: List<TrailEntity> = emptyList()
    private var filteredTrails: List<TrailEntity> = emptyList()

    private var easyOnly = false   // ⭐ NEW toggle state

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.trailRecyclerView)
        sortSpinner = view.findViewById(R.id.sortSpinner)
        searchView = view.findViewById(R.id.searchView)
        btnFilterEasy = view.findViewById(R.id.btnFilterEasy) // ⭐ NEW

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load sorting options
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            allTrails = withContext(Dispatchers.IO) {
                db.trailDao().getAllTrails()
            }

            filteredTrails = allTrails
            updateList(filteredTrails)
        }

        // SORTING
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // SEARCHING
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                applyFilters()
                return true
            }
            override fun onQueryTextChange(t: String?): Boolean {
                applyFilters()
                return true
            }
        })

        // ⭐ EASY ONLY FILTER BUTTON ⭐
        btnFilterEasy.setOnClickListener {
            easyOnly = !easyOnly
            updateEasyButtonUI()
            applyFilters()
        }
    }

    // Change button color based on ON/OFF state
    private fun updateEasyButtonUI() {
        if (easyOnly) {
            btnFilterEasy.setBackgroundColor(0xFF4CAF50.toInt())  // green
            btnFilterEasy.setTextColor(0xFFFFFFFF.toInt())
        } else {
            btnFilterEasy.setBackgroundColor(0xFFE0E0E0.toInt())  // grey
            btnFilterEasy.setTextColor(0xFF000000.toInt())
        }
    }

    /** Apply Search + Sorting + EasyOnly filter */
    private fun applyFilters() {
        val query = searchView.query.toString().lowercase()

        // 1️⃣ SEARCH
        filteredTrails = allTrails.filter {
            it.name.lowercase().contains(query)
        }

        // 2️⃣ EASY ONLY (difficulty == "easy")
        if (easyOnly) {
            filteredTrails = filteredTrails.filter {
                it.difficulty.equals("easy", ignoreCase = true)
            }
        }

        // 3️⃣ SORT
        val sortOption = sortSpinner.selectedItemPosition
        filteredTrails = when (sortOption) {
            0 -> filteredTrails.sortedBy { it.name }
            1 -> filteredTrails.sortedBy { it.distance }
            2 -> filteredTrails.sortedBy { it.difficulty }
            else -> filteredTrails
        }

        updateList(filteredTrails)
    }

    private fun updateList(list: List<TrailEntity>) {
        recyclerView.adapter = TrailListAdapter(list) { selectedTrail ->
            val args = Bundle().apply {
                putInt("trailId", selectedTrail.id)
            }
            (activity as MainActivity).navigateToTrailDetail(args)
        }
    }
}
