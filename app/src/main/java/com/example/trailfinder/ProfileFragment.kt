package com.example.trailfinder

import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.profileUsername).text = "Demo User"
        view.findViewById<TextView>(R.id.profileAvatar)
            .setImageResource(R.drawable.ic_person_placeholder)
    }
}
