package ru.laneboy.sportmove.data.network.responses

enum class UserRole(val value: String) {
    PARTICIPANT("Participant"),
    ORGANIZER("Organizer"),
    UNKNOWN("Unknown");

    companion object {
        fun String.toUserRole(): UserRole {
            for (userRole in UserRole.values()) {
                if (userRole.value.equals(this, ignoreCase = true)) {
                    return userRole
                }
            }
            return UNKNOWN
        }
    }
}