package com.github.soulduse.intellij.opencode.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val healthy: Boolean,
    val version: String
)

@Serializable
data class Session(
    val id: String,
    val title: String? = null,
    @SerialName("parentID")
    val parentId: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    val share: String? = null
)

@Serializable
data class CreateSessionRequest(
    val title: String? = null,
    @SerialName("parentID")
    val parentId: String? = null
)

@Serializable
data class Message(
    val id: String,
    @SerialName("sessionID")
    val sessionId: String,
    val role: String,
    @SerialName("createdAt")
    val createdAt: String? = null
)

@Serializable
data class MessagePart(
    val type: String,
    val text: String? = null,
    val path: String? = null
)

@Serializable
data class MessageResponse(
    val info: Message,
    val parts: List<MessagePart>
)

@Serializable
data class PromptRequest(
    val parts: List<MessagePart>,
    val model: ModelInfo? = null,
    val agent: String? = null,
    @SerialName("messageID")
    val messageId: String? = null,
    val noReply: Boolean = false
)

@Serializable
data class ModelInfo(
    @SerialName("providerID")
    val providerId: String,
    @SerialName("modelID")
    val modelId: String
)

@Serializable
data class Provider(
    val id: String,
    val name: String,
    val models: List<ProviderModel> = emptyList()
)

@Serializable
data class ProviderModel(
    val id: String,
    val name: String
)

@Serializable
data class ProvidersResponse(
    val providers: List<Provider>,
    val default: Map<String, String> = emptyMap()
)

@Serializable
data class FileContent(
    val type: String,
    val content: String
)

@Serializable
data class Project(
    val path: String,
    val name: String? = null
)

@Serializable
data class ServerEvent(
    val type: String,
    val properties: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap()
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String? = null
)
