package com.example.trailfinder.data.repository
import com.example.trailfinder.data.Badge
import com.example.trailfinder.data.BadgeDao
import com.example.trailfinder.data.UserBadge
import com.example.trailfinder.data.dao.UserBadgeDao

class BadgeRepository(
    private val badgeDao: BadgeDao,
    private val userBadgeDao: UserBadgeDao
) {

    var onBadgeUnlocked: ((Badge) -> Unit)? = null

    /**
     * Check which badges the user should unlock based on their review count.
     * Unlocks new badges and triggers the onBadgeUnlocked callback.
     */
    suspend fun checkAndUnlockBadges(userId: Int, reviewCount: Int) {
        // Example thresholds for unlocking badges
        val badgeThresholds = mapOf(
            1 to 1,   // badgeId 1 unlocked after 1 review
            5 to 2,   // badgeId 2 unlocked after 5 reviews
            10 to 3,   // badgeId 3 unlocked after 10 reviews
            50 to 4,   // badgeId 4 unlocked after 50 reviews
            100 to 5,  // badgeId 5 unlocked after 100 reviews
            500 to 6,  // badgeId 6 unlocked after 500 reviews
            1000 to 7 // badgeId 7 unlocked after 1000 reviews
        )

        for ((threshold, badgeId) in badgeThresholds) {
            if (reviewCount >= threshold) {
                // Check if user already has this badge
                val userBadgeIds = userBadgeDao.getUserBadges(userId)
                if (!userBadgeIds.contains(badgeId)) {
                    // Add to UserBadge table
                    val userBadge = UserBadge(userOwnerId = userId, badgeOwnerId = badgeId)
                    userBadgeDao.insertUserBadge(userBadge)

                    // Fetch the actual Badge entity
                    val badge = badgeDao.getBadgeById(badgeId)

                    // Trigger callback
                    onBadgeUnlocked?.invoke(badge)
                }
            }
        }
    }

    /**
     * Get all badges for a user
     */
    suspend fun getUserBadges(userId: Int): List<Badge> {
        val badgeIds = userBadgeDao.getUserBadges(userId)
        return badgeIds.map { id -> badgeDao.getBadgeById(id) }
    }
}