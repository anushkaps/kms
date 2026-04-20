package com.institute.ims.data.repository

import com.institute.ims.data.model.User
import com.institute.ims.data.model.UserRole

/** Two selectable users for the role gate before Dashboard. */
object FakeUserRepository : UserRepository {
    private val users = listOf(
        User(id = "user-admin", displayName = "Admin", role = UserRole.ADMIN),
        User(id = "user-faculty", displayName = "Faculty Member", role = UserRole.FACULTY),
    )

    override fun getSelectableUsers(): List<User> = users

    override fun getUser(userId: String): User? = users.find { it.id == userId }
}
