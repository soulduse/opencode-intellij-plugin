package com.github.soulduse.intellij.opencode.services

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.github.soulduse.intellij.opencode.model.HealthResponse
import com.github.soulduse.intellij.opencode.model.MessagePart
import com.github.soulduse.intellij.opencode.model.MessageResponse
import com.github.soulduse.intellij.opencode.model.Session
import com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

@Service(Service.Level.PROJECT)
class OpenCodeService(private val project: Project) : Disposable {
    
    private val logger = Logger.getInstance(OpenCodeService::class.java)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var serverProcess: Process? = null
    private var client: OpenCodeClient? = null
    private var currentSession: Session? = null
    
    val isServerRunning: Boolean
        get() = serverProcess?.isAlive == true
    
    val isConnected: Boolean
        get() = client != null
    
    fun getClient(): OpenCodeClient {
        if (client == null) {
            client = OpenCodeClient()
        }
        return client!!
    }
    
    fun startServer(): Boolean {
        if (isServerRunning) {
            logger.info("OpenCode server is already running")
            return true
        }
        
        val settings = OpenCodeSettingsState.getInstance()
        val command = settings.opencodeCommand
        val port = settings.serverPort
        
        return try {
            val processBuilder = ProcessBuilder(
                command, "serve",
                "--port", port.toString(),
                "--hostname", "127.0.0.1"
            )
            
            processBuilder.redirectErrorStream(true)
            processBuilder.directory(project.basePath?.let { java.io.File(it) })
            
            serverProcess = processBuilder.start()
            
            // Wait a bit for server to start
            Thread.sleep(1000)
            
            if (serverProcess?.isAlive == true) {
                logger.info("OpenCode server started on port $port")
                showNotification(
                    OpenCodeBundle.message("notification.serverStarted", port),
                    NotificationType.INFORMATION
                )
                
                // Initialize client
                client = OpenCodeClient("http://127.0.0.1:$port")
                true
            } else {
                logger.warn("OpenCode server failed to start")
                showNotification(
                    OpenCodeBundle.message("notification.serverError", "Process exited"),
                    NotificationType.ERROR
                )
                false
            }
        } catch (e: Exception) {
            logger.error("Failed to start OpenCode server", e)
            if (!settings.suppressCommandNotFound) {
                showNotification(
                    OpenCodeBundle.message("notification.commandNotFound"),
                    NotificationType.WARNING
                )
            }
            false
        }
    }
    
    fun stopServer() {
        serverProcess?.let { process ->
            process.destroy()
            if (process.isAlive) {
                process.destroyForcibly()
            }
            serverProcess = null
            logger.info("OpenCode server stopped")
            showNotification(
                OpenCodeBundle.message("notification.serverStopped"),
                NotificationType.INFORMATION
            )
        }
        
        client?.close()
        client = null
    }
    
    suspend fun checkHealth(): Result<HealthResponse> {
        return getClient().health()
    }
    
    suspend fun createSession(title: String? = null): Result<Session> {
        val result = getClient().createSession(title)
        result.onSuccess { session ->
            currentSession = session
        }
        return result
    }
    
    suspend fun listSessions(): Result<List<Session>> {
        return getClient().listSessions()
    }
    
    fun getCurrentSession(): Session? = currentSession
    
    fun setCurrentSession(session: Session?) {
        currentSession = session
    }
    
    suspend fun sendMessage(text: String): Result<MessageResponse> {
        val session = currentSession ?: run {
            val result = createSession()
            result.getOrNull() ?: return Result.failure(Exception("Failed to create session"))
        }
        
        return getClient().sendPrompt(session.id, text)
    }
    
    suspend fun sendMessageWithParts(parts: List<MessagePart>): Result<MessageResponse> {
        val session = currentSession ?: run {
            val result = createSession()
            result.getOrNull() ?: return Result.failure(Exception("Failed to create session"))
        }
        
        return getClient().sendPromptWithParts(session.id, parts)
    }
    
    suspend fun getMessages(): Result<List<MessageResponse>> {
        val session = currentSession ?: return Result.success(emptyList())
        return getClient().getMessages(session.id)
    }
    
    suspend fun abortCurrentSession(): Result<Boolean> {
        val session = currentSession ?: return Result.success(false)
        return getClient().abortSession(session.id)
    }
    
    private fun showNotification(content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("OpenCode Notifications")
            .createNotification(content, type)
            .notify(project)
    }
    
    override fun dispose() {
        scope.cancel()
        stopServer()
    }
    
    companion object {
        fun getInstance(project: Project): OpenCodeService {
            return project.getService(OpenCodeService::class.java)
        }
    }
}
