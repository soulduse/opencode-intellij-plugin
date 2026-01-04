package com.github.soulduse.intellij.opencode.services

import com.github.soulduse.intellij.opencode.model.*
import com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class OpenCodeClient(
    private val baseUrl: String = "http://127.0.0.1:${OpenCodeSettingsState.getInstance().serverPort}"
) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000 // 2 minutes for long operations
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 120_000
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
    
    // Health check
    suspend fun health(): Result<HealthResponse> = runCatching {
        client.get("$baseUrl/global/health").body()
    }
    
    // Session management
    suspend fun createSession(title: String? = null): Result<Session> = runCatching {
        client.post("$baseUrl/session") {
            setBody(CreateSessionRequest(title = title))
        }.body()
    }
    
    suspend fun listSessions(): Result<List<Session>> = runCatching {
        client.get("$baseUrl/session").body()
    }
    
    suspend fun getSession(id: String): Result<Session> = runCatching {
        client.get("$baseUrl/session/$id").body()
    }
    
    suspend fun deleteSession(id: String): Result<Boolean> = runCatching {
        val response = client.delete("$baseUrl/session/$id")
        response.status.isSuccess()
    }
    
    // Messages
    suspend fun getMessages(sessionId: String, limit: Int? = null): Result<List<MessageResponse>> = runCatching {
        client.get("$baseUrl/session/$sessionId/message") {
            limit?.let { parameter("limit", it) }
        }.body()
    }
    
    suspend fun sendPrompt(
        sessionId: String,
        text: String,
        model: ModelInfo? = null,
        agent: String? = null
    ): Result<MessageResponse> = runCatching {
        val parts = listOf(MessagePart(type = "text", text = text))
        client.post("$baseUrl/session/$sessionId/message") {
            setBody(PromptRequest(
                parts = parts,
                model = model,
                agent = agent
            ))
        }.body()
    }
    
    suspend fun sendPromptWithParts(
        sessionId: String,
        parts: List<MessagePart>,
        model: ModelInfo? = null,
        agent: String? = null
    ): Result<MessageResponse> = runCatching {
        client.post("$baseUrl/session/$sessionId/message") {
            setBody(PromptRequest(
                parts = parts,
                model = model,
                agent = agent
            ))
        }.body()
    }
    
    // Abort session
    suspend fun abortSession(sessionId: String): Result<Boolean> = runCatching {
        val response = client.post("$baseUrl/session/$sessionId/abort")
        response.status.isSuccess()
    }
    
    // Providers
    suspend fun getProviders(): Result<ProvidersResponse> = runCatching {
        client.get("$baseUrl/config/providers").body()
    }
    
    // File operations
    suspend fun findFiles(query: String): Result<List<String>> = runCatching {
        client.get("$baseUrl/find/file") {
            parameter("query", query)
        }.body()
    }
    
    suspend fun readFile(path: String): Result<FileContent> = runCatching {
        client.get("$baseUrl/file/content") {
            parameter("path", path)
        }.body()
    }
    
    // Project
    suspend fun getCurrentProject(): Result<Project> = runCatching {
        client.get("$baseUrl/project/current").body()
    }
    
    // Events (SSE) - simplified flow
    fun subscribeEvents(): Flow<ServerEvent> = flow {
        // Note: This is a simplified implementation
        // Full SSE implementation would require more complex handling
        val response = client.get("$baseUrl/event") {
            header(HttpHeaders.Accept, "text/event-stream")
        }
        
        val text = response.bodyAsText()
        // Parse SSE events
        text.split("\n\n").forEach { eventBlock ->
            if (eventBlock.isNotBlank()) {
                try {
                    val dataLine = eventBlock.lines().find { it.startsWith("data:") }
                    dataLine?.removePrefix("data:")?.trim()?.let { data ->
                        val event = json.decodeFromString<ServerEvent>(data)
                        emit(event)
                    }
                } catch (e: Exception) {
                    // Skip invalid events
                }
            }
        }
    }
    
    fun close() {
        client.close()
    }
    
    companion object {
        @Volatile
        private var instance: OpenCodeClient? = null
        
        fun getInstance(): OpenCodeClient {
            return instance ?: synchronized(this) {
                instance ?: OpenCodeClient().also { instance = it }
            }
        }
        
        fun resetInstance() {
            instance?.close()
            instance = null
        }
    }
}
