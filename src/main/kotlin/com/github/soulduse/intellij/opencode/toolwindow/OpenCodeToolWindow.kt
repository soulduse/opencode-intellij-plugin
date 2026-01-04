package com.github.soulduse.intellij.opencode.toolwindow

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.github.soulduse.intellij.opencode.model.MessageResponse
import com.github.soulduse.intellij.opencode.model.Session
import com.github.soulduse.intellij.opencode.model.StreamEvent
import com.github.soulduse.intellij.opencode.services.OpenCodeService
import com.github.soulduse.intellij.opencode.services.RetryUtils
import com.github.soulduse.intellij.opencode.ui.MarkdownRenderer
import kotlinx.coroutines.flow.collect
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class OpenCodeToolWindow(private val project: Project) : Disposable {
    
    private val service = OpenCodeService.getInstance(project)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val markdownRenderer = MarkdownRenderer()
    
    private val mainPanel = JPanel(BorderLayout())
    private val chatPanel = JPanel()
    private lateinit var chatScrollPane: JBScrollPane
    private val inputArea = JBTextArea(3, 40)
    private val sendButton = JButton(OpenCodeBundle.message("toolwindow.button.send"))
    private val abortButton = JButton(OpenCodeBundle.message("toolwindow.button.abort"))
    private val sessionComboBox = ComboBox<SessionItem>()
    private val statusLabel = JBLabel(OpenCodeBundle.message("status.disconnected"))
    private val modeToggle = JToggleButton(OpenCodeBundle.message("toolwindow.button.build"))
    private val loadingPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    
    private var isLoading = false
    private var currentJob: Job? = null
    private var streamingMessagePanel: StreamingMessagePanel? = null
    private var useStreaming = true // Enable streaming by default
    
    val content: JComponent
        get() = mainPanel
    
    init {
        setupUI()
        setupListeners()
        refreshSessions()
        checkConnection()
    }
    
    private fun setupUI() {
        mainPanel.border = JBUI.Borders.empty(8)
        
        // Header panel with session selector and status
        val headerPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyBottom(8)
            
            // Session selector
            val sessionPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                add(JBLabel("Session: "))
                add(sessionComboBox)
                add(Box.createHorizontalStrut(8))
                add(createNewSessionButton())
            }
            add(sessionPanel, BorderLayout.CENTER)
            
            // Status and settings
            val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 4, 0)).apply {
                add(statusLabel)
                add(createSettingsButton())
                add(createRefreshButton())
            }
            add(rightPanel, BorderLayout.EAST)
        }
        
        // Chat area
        chatPanel.layout = BoxLayout(chatPanel, BoxLayout.Y_AXIS)
        chatPanel.background = JBColor.background()
        
        chatScrollPane = JBScrollPane(chatPanel)
        chatScrollPane.border = JBUI.Borders.customLine(JBColor.border(), 1)
        chatScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        chatScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        
        // Show empty state
        showEmptyState()
        
        // Loading indicator panel
        loadingPanel.apply {
            isVisible = false
            background = JBColor.background()
            border = JBUI.Borders.empty(4, 8)
            
            add(JBLabel("Thinking...").apply {
                foreground = JBColor.GRAY
            })
            
            // Simple animated dots
            val dotsLabel = JBLabel("")
            add(dotsLabel)
            
            // Animation timer
            val dots = arrayOf(".", "..", "...", "")
            var dotIndex = 0
            Timer(400) {
                if (isVisible) {
                    dotsLabel.text = dots[dotIndex % dots.size]
                    dotIndex++
                }
            }.start()
        }
        
        // Input area
        inputArea.apply {
            border = JBUI.Borders.empty(8)
            lineWrap = true
            wrapStyleWord = true
            font = Font(Font.MONOSPACED, Font.PLAIN, 13)
            emptyText.text = OpenCodeBundle.message("toolwindow.input.placeholder")
        }
        
        val inputScrollPane = JBScrollPane(inputArea).apply {
            border = JBUI.Borders.customLine(JBColor.border(), 1)
            preferredSize = Dimension(0, 80)
        }
        
        // Abort button styling
        abortButton.apply {
            isVisible = false
            background = JBColor(Color(220, 53, 69), Color(180, 40, 50))
            foreground = JBColor.WHITE
            isFocusPainted = false
        }
        
        // Bottom panel with input and buttons
        val bottomPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyTop(8)
            
            add(loadingPanel, BorderLayout.NORTH)
            add(inputScrollPane, BorderLayout.CENTER)
            
            val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 4, 4)).apply {
                add(modeToggle)
                add(abortButton)
                add(sendButton)
            }
            add(buttonPanel, BorderLayout.SOUTH)
        }
        
        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH)
        mainPanel.add(chatScrollPane, BorderLayout.CENTER)
        mainPanel.add(bottomPanel, BorderLayout.SOUTH)
    }
    
    private fun setupListeners() {
        // Send button
        sendButton.addActionListener { sendMessage() }
        
        // Abort button
        abortButton.addActionListener { abortCurrentRequest() }
        
        // Enter to send (Shift+Enter for new line)
        inputArea.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER && !e.isShiftDown) {
                    e.consume()
                    sendMessage()
                } else if (e.keyCode == KeyEvent.VK_ESCAPE && isLoading) {
                    e.consume()
                    abortCurrentRequest()
                }
            }
        })
        
        // Session selection
        sessionComboBox.addActionListener {
            val selected = sessionComboBox.selectedItem as? SessionItem
            selected?.session?.let { session ->
                service.setCurrentSession(session)
                loadMessages()
            }
        }
        
        // Mode toggle
        modeToggle.addActionListener {
            modeToggle.text = if (modeToggle.isSelected) {
                OpenCodeBundle.message("toolwindow.button.plan")
            } else {
                OpenCodeBundle.message("toolwindow.button.build")
            }
        }
    }
    
    private fun createNewSessionButton(): JButton {
        return JButton("+").apply {
            toolTipText = OpenCodeBundle.message("toolwindow.session.new")
            preferredSize = Dimension(30, 25)
            addActionListener { createNewSession() }
        }
    }
    
    private fun createSettingsButton(): JButton {
        return JButton("\u2699").apply {
            toolTipText = OpenCodeBundle.message("toolwindow.button.settings")
            preferredSize = Dimension(30, 25)
            isBorderPainted = false
            isContentAreaFilled = false
            addActionListener {
                com.intellij.openapi.options.ShowSettingsUtil.getInstance()
                    .showSettingsDialog(project, "com.github.soulduse.intellij.opencode.settings")
            }
        }
    }
    
    private fun createRefreshButton(): JButton {
        return JButton("\u21BB").apply {
            toolTipText = OpenCodeBundle.message("toolwindow.button.refresh")
            preferredSize = Dimension(30, 25)
            isBorderPainted = false
            isContentAreaFilled = false
            addActionListener {
                checkConnection()
                refreshSessions()
            }
        }
    }
    
    private fun showEmptyState() {
        chatPanel.removeAll()
        chatPanel.add(Box.createVerticalGlue())
        
        val emptyLabel = JBLabel(OpenCodeBundle.message("message.empty")).apply {
            foreground = JBColor.GRAY
            alignmentX = Component.CENTER_ALIGNMENT
        }
        chatPanel.add(emptyLabel)
        chatPanel.add(Box.createVerticalGlue())
        chatPanel.revalidate()
        chatPanel.repaint()
    }
    
    private fun setLoadingState(loading: Boolean) {
        isLoading = loading
        sendButton.isEnabled = !loading
        sendButton.text = if (loading) "..." else OpenCodeBundle.message("toolwindow.button.send")
        abortButton.isVisible = loading
        loadingPanel.isVisible = loading
        inputArea.isEnabled = !loading
    }
    
    private fun abortCurrentRequest() {
        currentJob?.cancel()
        
        scope.launch {
            service.abort()
            ApplicationManager.getApplication().invokeLater {
                setLoadingState(false)
                addMessageToChat("System", "Request aborted.", false, isSystem = true)
            }
        }
    }
    
    private fun sendMessage() {
        val text = inputArea.text.trim()
        if (text.isEmpty() || isLoading) return
        
        inputArea.text = ""
        setLoadingState(true)
        
        // Add user message to UI
        addMessageToChat("You", text, true)
        
        // Try streaming first, fallback to regular request
        val streamFlow = if (useStreaming) service.streamMessage(text) else null
        
        if (streamFlow != null) {
            sendMessageWithStreaming(text, streamFlow)
        } else {
            sendMessageWithoutStreaming(text)
        }
    }
    
    private fun sendMessageWithStreaming(text: String, streamFlow: kotlinx.coroutines.flow.Flow<StreamEvent>) {
        // Create streaming message panel
        ApplicationManager.getApplication().invokeLater {
            removeEmptyStateIfNeeded()
            streamingMessagePanel = StreamingMessagePanel()
            chatPanel.add(streamingMessagePanel)
            chatPanel.add(Box.createVerticalStrut(8))
            chatPanel.revalidate()
            chatPanel.repaint()
            scrollToBottom()
        }
        
        currentJob = scope.launch {
            val textBuffer = StringBuilder()
            
            try {
                streamFlow.collect { event ->
                    when (event) {
                        is StreamEvent.TextDelta -> {
                            textBuffer.append(event.text)
                            ApplicationManager.getApplication().invokeLater {
                                streamingMessagePanel?.updateText(textBuffer.toString())
                                scrollToBottom()
                            }
                        }
                        is StreamEvent.ToolUse -> {
                            ApplicationManager.getApplication().invokeLater {
                                streamingMessagePanel?.showToolUse(event.name)
                            }
                        }
                        is StreamEvent.ToolResult -> {
                            ApplicationManager.getApplication().invokeLater {
                                streamingMessagePanel?.hideToolUse()
                            }
                        }
                        is StreamEvent.MessageComplete -> {
                            ApplicationManager.getApplication().invokeLater {
                                finalizeStreamingMessage(textBuffer.toString())
                            }
                        }
                        is StreamEvent.Error -> {
                            ApplicationManager.getApplication().invokeLater {
                                removeStreamingPanel()
                                addMessageToChat("Error", event.message, false, isError = true)
                            }
                        }
                        is StreamEvent.Heartbeat -> {
                            // Ignore heartbeats
                        }
                        is StreamEvent.MessageStart -> {
                            // Message started
                        }
                    }
                }
                
                // If stream completed without MessageComplete, finalize anyway
                if (textBuffer.isNotEmpty()) {
                    ApplicationManager.getApplication().invokeLater {
                        finalizeStreamingMessage(textBuffer.toString())
                    }
                }
            } catch (e: CancellationException) {
                ApplicationManager.getApplication().invokeLater {
                    removeStreamingPanel()
                }
            } catch (e: Exception) {
                // Streaming failed, fallback to non-streaming
                ApplicationManager.getApplication().invokeLater {
                    removeStreamingPanel()
                }
                sendMessageWithoutStreaming(text)
            } finally {
                ApplicationManager.getApplication().invokeLater {
                    setLoadingState(false)
                }
            }
        }
    }
    
    private fun sendMessageWithoutStreaming(text: String) {
        currentJob = scope.launch {
            try {
                val result = service.sendMessage(text)
                result.onSuccess { response ->
                    val assistantText = response.parts
                        .filter { it.type == "text" }
                        .mapNotNull { it.text }
                        .joinToString("\n")
                    
                    ApplicationManager.getApplication().invokeLater {
                        addMessageToChat("OpenCode", assistantText, false)
                    }
                }.onFailure { error ->
                    val friendlyMessage = RetryUtils.getUserFriendlyMessage(error)
                    ApplicationManager.getApplication().invokeLater {
                        addMessageToChat("Error", friendlyMessage, false, isError = true)
                    }
                }
            } catch (e: CancellationException) {
                // Request was cancelled, handled in abortCurrentRequest
            } finally {
                ApplicationManager.getApplication().invokeLater {
                    setLoadingState(false)
                }
            }
        }
    }
    
    private fun finalizeStreamingMessage(text: String) {
        removeStreamingPanel()
        if (text.isNotBlank()) {
            addMessageToChat("OpenCode", text, false)
        }
    }
    
    private fun removeStreamingPanel() {
        streamingMessagePanel?.let { panel ->
            val index = chatPanel.components.indexOf(panel)
            if (index >= 0) {
                chatPanel.remove(index)
                // Also remove the vertical strut after it
                if (index < chatPanel.componentCount) {
                    chatPanel.remove(index)
                }
            }
        }
        streamingMessagePanel = null
        chatPanel.revalidate()
        chatPanel.repaint()
    }
    
    private fun removeEmptyStateIfNeeded() {
        if (chatPanel.componentCount == 2 && chatPanel.getComponent(0) is Box.Filler) {
            chatPanel.removeAll()
        }
    }
    
    private fun scrollToBottom() {
        SwingUtilities.invokeLater {
            val scrollBar = chatScrollPane.verticalScrollBar
            scrollBar.value = scrollBar.maximum
        }
    }
    
    private fun addMessageToChat(
        sender: String, 
        text: String, 
        isUser: Boolean, 
        isError: Boolean = false,
        isSystem: Boolean = false
    ) {
        removeEmptyStateIfNeeded()
        
        val messagePanel = MessagePanel(sender, text, isUser, isError, isSystem)
        chatPanel.add(messagePanel)
        chatPanel.add(Box.createVerticalStrut(8))
        
        chatPanel.revalidate()
        chatPanel.repaint()
        
        scrollToBottom()
    }
    
    private fun checkConnection() {
        scope.launch {
            val result = service.checkHealth()
            ApplicationManager.getApplication().invokeLater {
                result.onSuccess {
                    statusLabel.text = OpenCodeBundle.message("status.connected")
                    statusLabel.foreground = JBColor.GREEN.darker()
                }.onFailure {
                    statusLabel.text = OpenCodeBundle.message("status.disconnected")
                    statusLabel.foreground = JBColor.RED
                }
            }
        }
    }
    
    private fun refreshSessions() {
        scope.launch {
            val result = service.listSessions()
            ApplicationManager.getApplication().invokeLater {
                result.onSuccess { sessions ->
                    sessionComboBox.removeAllItems()
                    sessionComboBox.addItem(SessionItem(null, OpenCodeBundle.message("toolwindow.session.new")))
                    sessions.forEach { session ->
                        sessionComboBox.addItem(SessionItem(session, session.title ?: session.id.take(8)))
                    }
                }
            }
        }
    }
    
    private fun createNewSession() {
        scope.launch {
            val result = service.createSession()
            ApplicationManager.getApplication().invokeLater {
                result.onSuccess { _ ->
                    refreshSessions()
                    chatPanel.removeAll()
                    showEmptyState()
                }
            }
        }
    }
    
    private fun loadMessages() {
        scope.launch {
            val result = service.getMessages()
            ApplicationManager.getApplication().invokeLater {
                result.onSuccess { messages ->
                    chatPanel.removeAll()
                    if (messages.isEmpty()) {
                        showEmptyState()
                    } else {
                        messages.forEach { msg ->
                            val isUser = msg.info.role == "user"
                            val text = msg.parts
                                .filter { it.type == "text" }
                                .mapNotNull { it.text }
                                .joinToString("\n")
                            if (text.isNotBlank()) {
                                addMessageToChat(
                                    if (isUser) "You" else "OpenCode",
                                    text,
                                    isUser
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    fun focusInput() {
        inputArea.requestFocusInWindow()
    }
    
    fun insertText(text: String) {
        val currentText = inputArea.text
        val caretPosition = inputArea.caretPosition
        val newText = StringBuilder(currentText)
            .insert(caretPosition, text)
            .toString()
        inputArea.text = newText
        inputArea.caretPosition = caretPosition + text.length
    }
    
    override fun dispose() {
        currentJob?.cancel()
        scope.cancel()
    }
    
    // Session item for combo box
    private data class SessionItem(val session: Session?, val displayName: String) {
        override fun toString(): String = displayName
    }
    
    // Message panel component with markdown rendering
    private inner class MessagePanel(
        sender: String,
        text: String,
        isUser: Boolean,
        isError: Boolean = false,
        isSystem: Boolean = false
    ) : JPanel() {
        
        init {
            layout = BorderLayout()
            border = JBUI.Borders.empty(8)
            background = when {
                isUser -> JBColor(Color(240, 240, 245), Color(50, 50, 55))
                isError -> JBColor(Color(255, 240, 240), Color(80, 40, 40))
                isSystem -> JBColor(Color(255, 250, 230), Color(60, 55, 40))
                else -> JBColor.background()
            }
            alignmentX = Component.LEFT_ALIGNMENT
            
            val headerLabel = JBLabel(sender).apply {
                font = font.deriveFont(Font.BOLD)
                foreground = when {
                    isError -> JBColor.RED
                    isSystem -> JBColor(Color(180, 140, 0), Color(200, 160, 50))
                    isUser -> JBColor.BLUE
                    else -> JBColor.foreground()
                }
            }
            
            add(headerLabel, BorderLayout.NORTH)
            
            // Use markdown rendering for non-user messages
            val contentComponent = if (!isUser && !isError && !isSystem) {
                markdownRenderer.render(text)
            } else {
                JBTextArea(text).apply {
                    isEditable = false
                    lineWrap = true
                    wrapStyleWord = true
                    background = this@MessagePanel.background
                    border = JBUI.Borders.emptyTop(4)
                    font = Font(Font.MONOSPACED, Font.PLAIN, 13)
                }
            }
            
            add(contentComponent, BorderLayout.CENTER)
            
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
    }
    
    // Streaming message panel for real-time updates
    private inner class StreamingMessagePanel : JPanel() {
        private val textArea = JBTextArea()
        private val toolLabel = JBLabel()
        private val headerLabel = JBLabel("OpenCode")
        
        init {
            layout = BorderLayout()
            border = JBUI.Borders.empty(8)
            background = JBColor.background()
            alignmentX = Component.LEFT_ALIGNMENT
            
            headerLabel.apply {
                font = font.deriveFont(Font.BOLD)
                foreground = JBColor.foreground()
            }
            
            textArea.apply {
                isEditable = false
                lineWrap = true
                wrapStyleWord = true
                background = this@StreamingMessagePanel.background
                border = JBUI.Borders.emptyTop(4)
                font = Font(Font.MONOSPACED, Font.PLAIN, 13)
                text = ""
            }
            
            toolLabel.apply {
                isVisible = false
                foreground = JBColor.GRAY
                border = JBUI.Borders.emptyTop(4)
            }
            
            val contentPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                isOpaque = false
                add(textArea)
                add(toolLabel)
            }
            
            add(headerLabel, BorderLayout.NORTH)
            add(contentPanel, BorderLayout.CENTER)
            
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
        
        fun updateText(text: String) {
            textArea.text = text
            revalidate()
            repaint()
        }
        
        fun showToolUse(toolName: String) {
            toolLabel.text = "\u2699 Using: $toolName..."
            toolLabel.isVisible = true
            revalidate()
            repaint()
        }
        
        fun hideToolUse() {
            toolLabel.isVisible = false
            revalidate()
            repaint()
        }
    }
}
