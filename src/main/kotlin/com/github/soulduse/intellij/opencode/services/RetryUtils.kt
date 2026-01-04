package com.github.soulduse.intellij.opencode.services

import kotlinx.coroutines.delay

/**
 * Utility class for retry logic with exponential backoff.
 */
object RetryUtils {
    
    /**
     * Execute a suspending block with retry logic.
     * 
     * @param times Maximum number of retry attempts
     * @param initialDelayMs Initial delay between retries in milliseconds
     * @param maxDelayMs Maximum delay between retries in milliseconds
     * @param factor Multiplier for exponential backoff
     * @param shouldRetry Predicate to determine if retry should occur based on exception
     * @param block The suspending block to execute
     */
    suspend fun <T> withRetry(
        times: Int = 3,
        initialDelayMs: Long = 1000,
        maxDelayMs: Long = 10000,
        factor: Double = 2.0,
        shouldRetry: (Throwable) -> Boolean = { true },
        block: suspend (attempt: Int) -> T
    ): T {
        var currentDelay = initialDelayMs
        var lastException: Throwable? = null
        
        repeat(times) { attempt ->
            try {
                return block(attempt)
            } catch (e: Throwable) {
                lastException = e
                
                if (!shouldRetry(e) || attempt == times - 1) {
                    throw e
                }
                
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            }
        }
        
        throw lastException ?: IllegalStateException("Retry failed without exception")
    }
    
    /**
     * Check if an exception is retryable (network errors, timeouts, etc.)
     */
    fun isRetryable(e: Throwable): Boolean {
        return when {
            e is java.net.ConnectException -> true
            e is java.net.SocketTimeoutException -> true
            e is java.net.UnknownHostException -> true
            e is java.io.IOException && e.message?.contains("Connection reset") == true -> true
            e.cause != null -> isRetryable(e.cause!!)
            else -> false
        }
    }
    
    /**
     * Get a user-friendly error message from an exception.
     */
    fun getUserFriendlyMessage(e: Throwable): String {
        return when {
            e is java.net.ConnectException -> 
                "Cannot connect to OpenCode server. Make sure it's running."
            e is java.net.SocketTimeoutException -> 
                "Request timed out. The server might be busy or unresponsive."
            e is java.net.UnknownHostException -> 
                "Cannot resolve server address. Check your network connection."
            e is java.io.IOException && e.message?.contains("Connection reset") == true ->
                "Connection was reset. The server might have restarted."
            e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true ->
                "Authentication failed. Check your API key configuration."
            e.message?.contains("403") == true || e.message?.contains("Forbidden") == true ->
                "Access denied. You may not have permission for this operation."
            e.message?.contains("404") == true || e.message?.contains("Not Found") == true ->
                "Resource not found. The session or endpoint may not exist."
            e.message?.contains("429") == true || e.message?.contains("Too Many") == true ->
                "Rate limited. Please wait before making more requests."
            e.message?.contains("500") == true || e.message?.contains("Internal Server") == true ->
                "Server error. Please try again later."
            e.message?.contains("502") == true || e.message?.contains("Bad Gateway") == true ->
                "Server temporarily unavailable. Please try again."
            e.message?.contains("503") == true || e.message?.contains("Service Unavailable") == true ->
                "Service unavailable. The server may be overloaded."
            else -> e.message ?: "An unknown error occurred"
        }
    }
}
