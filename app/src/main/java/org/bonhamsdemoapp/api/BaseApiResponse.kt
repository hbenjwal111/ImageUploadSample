package org.bonhamsdemoapp.api

import retrofit2.Response

/**
 * Base api response
 *
 * @constructor Create Base api response
 */
abstract class BaseApiResponse {
    /**
     * Safe api call
     *
     * @param T
     * @param apiCall
     * @receiver
     * @return
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    /**
     * Error
     *
     * @param T
     * @param errorMessage
     * @return
     */
    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Api call failed $errorMessage")
}