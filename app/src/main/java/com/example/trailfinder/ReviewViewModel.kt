package com.example.trailfinder.ui.reviews

import androidx.lifecycle.ViewModel
import com.example.trailfinder.data.repository.BadgeRepository
import com.example.trailfinder.data.repository.ReviewRepository

class ReviewViewModel(
    private val reviewRepository: ReviewRepository,
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    // Example: get review count for a user
    suspend fun getReviewCount(userId: Int): Int {
        return reviewRepository.getReviewCountByUser(userId)
    }

    // Example: check and unlock badges
    suspend fun checkAndUnlockBadges(userId: Int, reviewCount: Int) {
        badgeRepository.checkAndUnlockBadges(userId, reviewCount)
    }
}