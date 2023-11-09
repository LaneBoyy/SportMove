package ru.laneboy.sportmove.data.network.wrapper


/**
 * Ошибка, возникающая при обращении к API с ошибочным токеном доступа.
 *
 * @param link - ссылка для получения нового токена доступа.
 */
open class TokenInvalidException() : ApiException()
