package com.institute.ims.data.repository

import com.institute.ims.data.model.User

interface UserRepository {
    fun getSelectableUsers(): List<User>
    fun getUser(userId: String): User?
}
