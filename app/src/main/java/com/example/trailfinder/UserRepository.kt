package com.example.trailfinder.data.repository


import com.example.trailfinder.data.Badge
import com.example.trailfinder.data.BadgeDao
import com.example.trailfinder.data.UserBadge
import com.example.trailfinder.data.dao.UserBadgeDao

class UserRepository(
    private val userBadgeDao: UserBadgeDao,
    private val badgeDao: BadgeDao
) {

    var onBadgeUnlocked: ((Badge) -> Unit)? = null

    suspend fun unlockBadges(userId: Int, badgeIds: List<Int>) {
        val badges = mutableListOf<Badge>()
        for (badgeId in badgeIds) {
            val userBadge = UserBadge(userOwnerId = userId, badgeOwnerId = badgeId)
            userBadgeDao.insertUserBadge(userBadge)
            val badge = badgeDao.getBadgeById(badgeId)
            badges.add(badge)
            onBadgeUnlocked?.invoke(badge) // call once per badge
        }
    }
}