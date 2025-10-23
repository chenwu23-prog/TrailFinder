package com.example.trailfinder

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageView   // ✅ Add this import
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.profileUsername).text = "Demo User"

        // ✅ Fix here — use ImageView instead of TextView
        view.findViewById<ImageView>(R.id.profileAvatar)
            .setImageResource(R.drawable.ic_person_placeholder)
    }
}