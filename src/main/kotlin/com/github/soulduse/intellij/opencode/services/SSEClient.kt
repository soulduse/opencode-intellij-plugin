package com.github.soulduse.intellij.opencode.services

import com.github.soulduse.intellij.opencode.model.StreamEvent
import com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*

/**
 * SSE (Server-Sent Events) client for streaming OpenCode responses.
 */
class SSEClient(
    private val baseUrl: String = "http://127.0.0.1:${OpenCodeSettingsState.getInstance().serverPort}"
) {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = Long.MAX_VALUE // No timeout for SSE
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = Long.MAX_VALUE
        }
    }
    
    /**
     * Subscribe to SSE events from the server.
     * This provides real-time streaming of AI responses.
     */
    fun subscribeToEvents(sessionId: String): Flow<StreamEvent> = flow {
        try {
            client.prepareGet("$baseUrl/session/$sessionId/events") {
                header(HttpHeaders.Accept, "text/event-stream")
                header(HttpHeaders.CacheControl, "no-cache")
                header(HttpHeaders.Connection, "keep-alive")
            }.execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                val buffer = StringBuilder()
                
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    
                    when {
                        line.startsWith("data:") -> {
                            val data = line.removePrefix("data:").trim()
                            buffer.append(data)
                        }
                        line.isEmpty() && buffer.isNotEmpty() -> {
                            // End of event, process it
                            val eventData = buffer.toString()
                            buffer.clear()
                            
                            try {
                                val event = parseEvent(eventData)
                                if (event != null) {
                                    emit(event)
                                }
                            } catch (e: Exception) {
                                // Skip malformed events
                            }
                        }
                        line.startsWith(":") -> {
                            // Comment/heartbeat
                            emit(StreamEvent.Heartbeat)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(StreamEvent.Error(e.message ?: "Connection error"))
        }
    }
    
    /**
     * Stream a prompt and receive real-time updates.
     */
    fun streamPrompt(
        sessionId: String,
        text: String
    ): Flow<StreamEvent> = flow {
        try {
            val requestBody = buildJsonObject {
                put("parts", buildJsonArray {
                    add(buildJsonObject {
                        put("type", "text")
                        put("text", text)
                    })
                })
            }
            
            client.preparePost("$baseUrl/session/$sessionId/message/stream") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Accept, "text/event-stream")
                setBody(requestBody.toString())
            }.execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                val buffer = StringBuilder()
                var currentEventType = ""
                
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    
                    when {
                        line.startsWith("event:") -> {
                            currentEventType = line.removePrefix("event:").trim()
                        }
                        line.startsWith("data:") -> {
                            val data = line.removePrefix("data:").trim()
                            buffer.append(data)
                        }
                        line.isEmpty() && buffer.isNotEmpty() -> {
                            val eventData = buffer.toString()
                            buffer.clear()
                            
                            try {
                                val event = parseTypedEvent(currentEventType, eventData)
                                if (event != null) {
                                    emit(event)
                                }
                            } catch (e: Exception) {
                                // Skip malformed events
                            }
                            currentEventType = ""
                        }
                        line.startsWith(":") -> {
                            emit(StreamEvent.Heartbeat)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(StreamEvent.Error(e.message ?: "Stream error"))
        }
    }
    
    private fun parseEvent(data: String): StreamEvent? {
        if (data.isBlank()) return null
        
        return try {
            val jsonElement = json.parseToJsonElement(data)
            val jsonObject = jsonElement.jsonObject
            val type = jsonObject["type"]?.jsonPrimitive?.contentOrNull ?: return null
            
            parseTypedEvent(type, data)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseTypedEvent(type: String, data: String): StreamEvent? {
        if (data.isBlank()) return null
        
        return try {
            val jsonObject = json.parseToJsonElement(data).jsonObject
            
            when (type.lowercase()) {
                "message_start", "message.start" -> {
                    val messageId = jsonObject["message_id"]?.jsonPrimitive?.contentOrNull
                        ?: jsonObject["id"]?.jsonPrimitive?.contentOrNull
                        ?: ""
                    StreamEvent.MessageStart(messageId)
                }
                "content_block_delta", "text_delta", "delta", "text" -> {
                    val text = jsonObject["text"]?.jsonPrimitive?.contentOrNull
                        ?: jsonObject["delta"]?.jsonObject?.get("text")?.jsonPrimitive?.contentOrNull
                        ?: jsonObject["content"]?.jsonPrimitive?.contentOrNull
                        ?: ""
                    if (text.isNotEmpty()) StreamEvent.TextDelta(text) else null
                }
                "tool_use", "tool.use" -> {
                    val name = jsonObject["name"]?.jsonPrimitive?.contentOrNull ?: ""
                    val input = jsonObject["input"]?.toString() ?: ""
                    StreamEvent.ToolUse(name, input)
                }
                "tool_result", "tool.result" -> {
                    val name = jsonObject["name"]?.jsonPrimitive?.contentOrNull ?: ""
                    val result = jsonObject["result"]?.toString() ?: ""
                    StreamEvent.ToolResult(name, result)
                }
                "message_stop", "message.complete", "message_complete" -> {
                    val messageId = jsonObject["message_id"]?.jsonPrimitive?.contentOrNull
                        ?: jsonObject["id"]?.jsonPrimitive?.contentOrNull
                        ?: ""
                    StreamEvent.MessageComplete(messageId)
                }
                "error" -> {
                    val message = jsonObject["message"]?.jsonPrimitive?.contentOrNull
                        ?: jsonObject["error"]?.jsonPrimitive?.contentOrNull
                        ?: "Unknown error"
                    StreamEvent.Error(message)
                }
                "ping", "heartbeat" -> StreamEvent.Heartbeat
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun close() {
        client.close()
    }
}
