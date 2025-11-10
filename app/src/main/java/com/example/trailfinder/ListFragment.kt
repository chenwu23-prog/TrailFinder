package com.example.trailfinder.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trailfinder.R
import edu.vt.trailreview.util.TrailSeed
import edu.vt.trailreview.util.JsonLoader

class TrailListFragment : Fragment(R.layout.fragment_trail_list) {

    private lateinit var adapter: TrailAdapter
    private val repo = JsonLoader
    private var allTrails: List<TrailSeed> = emptyList() // cache to avoid multiple repo calls

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.trailRecyclerView)
        adapter = TrailAdapter(emptyList()) { trail ->
            // TODO: Navigate to Trail Detail screen
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        allTrails = repo.loadTrails(requireContext())
        adapter.updateList(allTrails)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.trail_list_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search by trail name"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (newText.isNullOrBlank()) {
                    allTrails
                } else {
                    allTrails.filter {
                        it.name.contains(newText, ignoreCase = true)
                    }
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sortedList = when (item.itemId) {
            R.id.sort_distance -> allTrails.sortedBy { it.distanceKm }
            R.id.sort_difficulty -> allTrails.sortedBy { it.difficulty }
            else -> return super.onOptionsItemSelected(item)
        }
        adapter.updateList(sortedList)
        return true
    }
}