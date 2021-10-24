package com.amalkina.beautydiary.domain.common

sealed class Result {
    object Loading : Result()
    data class Success<T>(val value: T) : Result()
    data class Error(val message: String) : Result()
}