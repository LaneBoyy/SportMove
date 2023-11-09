package ru.laneboy.sportmove.data.network.wrapper

import NetworkResponseCall
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResponseAdapter<S>(
    private val successType: Type,
) : CallAdapter<S, Call<ApiResponse<S>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<ApiResponse<S>> {
        return NetworkResponseCall(call)
    }
}