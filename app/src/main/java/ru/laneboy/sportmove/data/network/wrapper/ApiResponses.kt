package ru.laneboy.sportmove.data.network.wrapper

import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

/**
 * Базовый класс для обработки ответов от Retrofit.
 *
 * [T] - Тип данных, которые необходимо получить.
 */
sealed class ApiResponse<T> {

    companion object {

        private const val HTTP_CODE_UNAUTHORIZED = 401
        private const val HTTP_CODE_FORBIDDEN = 403
        private const val HTTP_CODE_NOT_FOUND = 404

        /**
         * Обрабатывает полученный от Retrofit ответ и приводит его с соответствующему типу ApiResponse.
         *
         * Проверяет успешный ответ.
         *
         * Если ответ успешный:
         *  - пробует получить необходимые данные и вернуть их в результате.
         *  - если данные неверные, возвращает результат с описанием ошибки.
         *
         * Если ответ с ошибкой:
         *  - возвращает результат с описанием ошибки.
         *
         * @return ApiResponse на основе ответа Retrofit.
         */
        @Suppress("UNCHECKED_CAST", "Detekt.TooGenericExceptionCaught")
        fun <T> proceedResponse(response: Response<T>): ApiResponse<T> = try {
            if (response.isSuccessful) {
                ApiSuccessResponse(response.body()) as ApiResponse<T>
            } else {
                convertResponseToException(response)
            }
        } catch (th: Throwable) {
            createError(th)
        }

        private fun <T> convertResponseToException(response: Response<T>): ApiResponse<T> {
            return when (response.code()) {
                HTTP_CODE_UNAUTHORIZED -> createError(TokenInvalidException())
                HTTP_CODE_FORBIDDEN -> createError(TokenForbiddenException())
                else -> createError(ApiException(response.errorBody()?.string() ?: "Unknown error"))
            }
        }

        /**
         * Формирует [ApiErrorResponse] на основе переданной ошибки [Throwable].
         *
         * Слой доступа к Api должен возвращать ошибки только своего уровня, инкапсулирует все ошибки полученные при
         * обращении к Api.
         *
         * @param th - ошибка, возникшая при обработке ответа.
         */
        fun <T> createError(th: Throwable): ApiResponse<T> = when (th) {
            is ApiException -> ApiErrorResponse(th)
            is UnknownHostException, is SocketTimeoutException -> ApiErrorResponse(
                NoInternetException(Date().time)
            )
            else -> ApiErrorResponse(ApiException(message = th.message, cause = th))
        }
    }

    /**
     * Выполнение блока [block] в случае получения успешного ответа от API.
     *
     * @param block - блок для выполнения.
     */
    inline fun ifSuccess(block: (data: T) -> Unit): ApiResponse<T> {
        if (this is ApiSuccessResponse<T>) {
            block(this.data)
        }
        return this
    }

    /**
     * Выполнение блока [block] в случае получения ошибки при запросе к API.
     *
     * @param block - блок для выполнения.
     */
    inline fun ifError(block: (error: ApiException) -> Unit): ApiResponse<T> {
        if (this is ApiErrorResponse<T>) {
            block(this.error)
        }
        return this
    }
}

/**
 * Положительный ответ от API.
 *
 * @param data - Данные в полученном ответе.
 *
 * @return успешный ответ с данным.
 */
data class ApiSuccessResponse<T>(val data: T) : ApiResponse<T>()

/**
 * Ответ с ошибкой от API.
 *
 * @param error - Ошибка полученная при обращении к API.
 *
 * @return ответ с ошибкой.
 */
data class ApiErrorResponse<T>(val error: ApiException) : ApiResponse<T>()
