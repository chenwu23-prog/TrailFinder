package com.example.trailfinder

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Solid background to ensure no map "bleed-through"
        view.setBackgroundColor(requireContext().getColor(android.R.color.white))

        // ✅ Basic demo content
        val usernameView = view.findViewById<TextView>(R.id.profileUsername)
        val avatarView = view.findViewById<ImageView>(R.id.profileAvatar)
        val historyView = view.findViewById<TextView>(R.id.profileHistory)
        val titleView = view.findViewById<TextView>(R.id.profileScreenTitle)

        usernameView.text = "Demo User"
        avatarView.setImageResource(R.drawable.ic_person_placeholder)
        historyView.text = "User history placeholder"
        titleView.text = "Profile Screen"
    }
}