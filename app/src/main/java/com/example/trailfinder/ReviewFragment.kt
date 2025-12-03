package com.example.trailfinder.ui.reviews

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.trailfinder.R
import kotlinx.coroutines.launch

class ReviewFragment : Fragment(R.layout.fragment_review) {

    // Use ViewModel with default DAOs; no Factory needed
    private val reviewViewModel: ReviewViewModel by viewModels()
    private lateinit var txtReviewCount: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextView
        txtReviewCount = view.findViewById(R.id.txtReviewCount)

        // Launch coroutine to fetch data
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = 1 // Replace with actual logged-in user ID

            // Get review count
            val reviewCount = reviewViewModel.getReviewCount(userId)
            txtReviewCount.text = "Reviews: " + reviewCount.toString()


            // Check and unlock badges
            reviewViewModel.checkAndUnlockBadges(userId, reviewCount)
        }
    }
}