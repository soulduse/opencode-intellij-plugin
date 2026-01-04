package com.github.soulduse.intellij.opencode.toolwindow

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.github.soulduse.intellij.opencode.model.MessageResponse
import com.github.soulduse.intellij.opencode.model.Session
import com.github.soulduse.intellij.opencode.services.OpenCodeService
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
    
    private val mainPanel = JPanel(BorderLayout())
    private val chatPanel = JPanel()
    private lateinit var chatScrollPane: JBScrollPane
    private val inputArea = JBTextArea(3, 40)
    private val sendButton = JButton(OpenCodeBundle.message("toolwindow.button.send"))
    private val sessionComboBox = ComboBox<SessionItem>()
    private val statusLabel = JBLabel(OpenCodeBundle.message("status.disconnected"))
    private val modeToggle = JToggleButton(OpenCodeBundle.message("toolwindow.button.build"))
    
    private var isLoading = false
    
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
        
        // Bottom panel with input and buttons
        val bottomPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.emptyTop(8)
            add(inputScrollPane, BorderLayout.CENTER)
            
            val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 4, 4)).apply {
                add(modeToggle)
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
        
        // Enter to send (Shift+Enter for new line)
        inputArea.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER && !e.isShiftDown) {
                    e.consume()
                    sendMessage()
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
        return JButton("⚙").apply {
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
        return JButton("↻").apply {
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
    
    private fun sendMessage() {
        val text = inputArea.text.trim()
        if (text.isEmpty() || isLoading) return
        
        inputArea.text = ""
        isLoading = true
        sendButton.isEnabled = false
        sendButton.text = "..."
        
        // Add user message to UI
        addMessageToChat("You", text, true)
        
        scope.launch {
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
                    ApplicationManager.getApplication().invokeLater {
                        addMessageToChat("Error", error.message ?: "Unknown error", false, isError = true)
                    }
                }
            } finally {
                ApplicationManager.getApplication().invokeLater {
                    isLoading = false
                    sendButton.isEnabled = true
                    sendButton.text = OpenCodeBundle.message("toolwindow.button.send")
                }
            }
        }
    }
    
    private fun addMessageToChat(sender: String, text: String, isUser: Boolean, isError: Boolean = false) {
        // Remove empty state if present
        if (chatPanel.componentCount == 2 && chatPanel.getComponent(0) is Box.Filler) {
            chatPanel.removeAll()
        }
        
        val messagePanel = MessagePanel(sender, text, isUser, isError)
        chatPanel.add(messagePanel)
        chatPanel.add(Box.createVerticalStrut(8))
        
        chatPanel.revalidate()
        chatPanel.repaint()
        
        // Scroll to bottom
        SwingUtilities.invokeLater {
            val scrollBar = chatScrollPane.verticalScrollBar
            scrollBar.value = scrollBar.maximum
        }
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
                result.onSuccess { session ->
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
        scope.cancel()
    }
    
    // Session item for combo box
    private data class SessionItem(val session: Session?, val displayName: String) {
        override fun toString(): String = displayName
    }
    
    // Message panel component
    private inner class MessagePanel(
        sender: String,
        text: String,
        isUser: Boolean,
        isError: Boolean = false
    ) : JPanel() {
        
        init {
            layout = BorderLayout()
            border = JBUI.Borders.empty(8)
            background = if (isUser) {
                JBColor(Color(240, 240, 245), Color(50, 50, 55))
            } else if (isError) {
                JBColor(Color(255, 240, 240), Color(80, 40, 40))
            } else {
                JBColor.background()
            }
            
            val headerLabel = JBLabel(sender).apply {
                font = font.deriveFont(Font.BOLD)
                foreground = if (isError) JBColor.RED else if (isUser) JBColor.BLUE else JBColor.foreground()
            }
            
            val textArea = JBTextArea(text).apply {
                isEditable = false
                lineWrap = true
                wrapStyleWord = true
                background = this@MessagePanel.background
                border = JBUI.Borders.emptyTop(4)
                font = Font(Font.MONOSPACED, Font.PLAIN, 13)
            }
            
            add(headerLabel, BorderLayout.NORTH)
            add(textArea, BorderLayout.CENTER)
            
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
    }
}
