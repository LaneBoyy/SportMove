package ru.laneboy.sportmove.data.network.wrapper


/**
 * Ошибка возникающая при проблемах с доступом к сети интернет. Возможна когда отключена
 * передача данных на устройстве, когда прервался запрос или возникли другие проблемы транспортного уровня при
 * выполнении запроса.
 */
open class NoInternetException(
    errorCreationTime: Long = System.currentTimeMillis()
) : ApiException(
    message = "Отсутствует подключение к интернету",
    errorCreationTime = errorCreationTime
)
