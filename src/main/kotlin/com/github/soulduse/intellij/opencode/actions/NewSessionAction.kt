package com.github.soulduse.intellij.opencode.actions

import com.github.soulduse.intellij.opencode.services.OpenCodeService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewSessionAction : AnAction() {
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Create new session
        scope.launch {
            val service = OpenCodeService.getInstance(project)
            service.createSession()
        }
        
        // Show and focus tool window
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("OpenCode")
        toolWindow?.show()
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
