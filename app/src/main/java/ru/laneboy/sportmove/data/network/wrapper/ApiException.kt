package ru.laneboy.sportmove.data.network.wrapper

/**
 * Базовая ошибка, которая может возникнуть при обращении к методам  API.
 */
open class ApiException(
    message: String? = null,
    cause: Throwable? = null,
    val errorCreationTime: Long? = null
) : RuntimeException(message, cause) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiException) return false

        if (message != other.message) return false
        if (cause != other.cause) return false
        if (errorCreationTime != other.errorCreationTime) return false

        return true
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 31 * hash + if (message == null) 0 else message.hashCode()
        hash = 31 * hash + if (cause == null) 0 else cause.hashCode()
        hash = 31 * hash + (errorCreationTime?.hashCode() ?: 0)
        return hash
    }
}
