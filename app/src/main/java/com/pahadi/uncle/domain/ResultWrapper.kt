package com.pahadi.uncle.domain

sealed class ResultWrapper<T> {
    class Success<T>(val response: T) : ResultWrapper<T>()
    class Failure(val errorMessage: String ) : ResultWrapper<Nothing>()
}