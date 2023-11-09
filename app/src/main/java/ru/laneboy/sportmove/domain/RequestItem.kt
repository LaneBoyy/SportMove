package ru.laneboy.sportmove.domain


data class RequestItem(
    val requestId: Int,
    val selectedCompetition: String,
    val teamCaptain: String,
    val teamName: String,
    val requestStatus: RequestStatus
){
    enum class RequestStatus(val value:String){
        ACCEPTED("ACCEPTED"),
        REJECTED("REJECTED"),
        NOT_DETERMINED("NOT_DETERMINED"),
        UNKNOWN("UNKNOWN");

        companion object{
            fun String.toRequestStatus(): RequestStatus {
                for (requestStatus in RequestStatus.values()) {
                    if (requestStatus.value.equals(this, ignoreCase = true)) {
                        return requestStatus
                    }
                }
                return UNKNOWN
            }
        }
    }
}