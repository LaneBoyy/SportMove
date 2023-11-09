package ru.laneboy.sportmove.domain

import ru.laneboy.sportmove.data.network.responses.UserRole

data class AuthData(
    val userId: Int,
    val userName: String?,
    val userEmail: String,
    val userPassword: String,
    val userRole: UserRole
)
