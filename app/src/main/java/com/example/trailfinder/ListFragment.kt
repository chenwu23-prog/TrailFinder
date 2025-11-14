package com.example.trailfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
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

    private var allTrails: List<TrailEntity> = emptyList()
    private var filteredTrails: List<TrailEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        recyclerView = view.findViewById(R.id.trailRecyclerView)
        sortSpinner = view.findViewById(R.id.sortSpinner)
        searchView = view.findViewById(R.id.searchView)

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
            // Load trails from DB
            allTrails = withContext(Dispatchers.IO) {
                db.trailDao().getAllTrails()
            }

            filteredTrails = allTrails
            updateList(filteredTrails)
        }

        // Handle sorting
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Handle search text
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                applyFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilters()
                return true
            }
        })
    }

    private fun applyFilters() {
        val query = searchView.query.toString().lowercase()

        // 1️⃣ Apply search filter
        filteredTrails = allTrails.filter {
            it.name.lowercase().contains(query)
        }

        // 2️⃣ Apply sorting
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

            // Navigate to detail
            val args = Bundle().apply {
                putInt("trailId", selectedTrail.id)
            }

            (activity as MainActivity).navigateToTrailDetail(args)
        }
    }
}