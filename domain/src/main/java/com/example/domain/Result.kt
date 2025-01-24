package com.example.domain

open class Result<T>(private val value: Any?) {

    fun isFailure() = value is ResultFailure
    fun requireValue(): T = value as T
    fun valueOrNull(): T? = if (isFailure()) null else value as T
    fun requireError(): Throwable = (value as ResultFailure).requireValue()
    fun errorOrNull(): Throwable? = if (isFailure()) (value as ResultFailure).requireValue() else null

    companion object {
        fun <T : Any?> createSuccess(value: T): Result<T> {
            return Result(value)
        }

        fun <T> createFailure(value: Throwable): Result<T> {
            return Result(ResultFailure(value))
        }
    }

    internal class ResultFailure(value: Throwable) : Result<Throwable>(value) {
    }
}

suspend fun <T, R> Result<T>.fold(
    onSuccess: suspend (T) -> R,
    onError: suspend (Throwable) -> R,
): R {
    return when {
        this.isFailure() -> onError(this.requireError())
        else -> onSuccess(this.requireValue())
    }
}

fun <T> Result<T>.getResult(
    onSuccess: (T) -> Unit,
    onError: (Throwable) -> Unit,
) {
    when {
        this.isFailure() -> onError(this.requireError())
        else -> onSuccess(this.requireValue())
    }
}
