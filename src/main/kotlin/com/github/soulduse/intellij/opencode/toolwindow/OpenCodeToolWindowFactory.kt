package com.github.soulduse.intellij.opencode.toolwindow

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.github.soulduse.intellij.opencode.services.OpenCodeService
import com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class OpenCodeToolWindowFactory : ToolWindowFactory, DumbAware {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val openCodeToolWindow = OpenCodeToolWindow(project)
        val content = ContentFactory.getInstance().createContent(
            openCodeToolWindow.content,
            OpenCodeBundle.message("toolwindow.title"),
            false
        )
        toolWindow.contentManager.addContent(content)
        
        // Auto start server if enabled
        if (OpenCodeSettingsState.getInstance().autoStartServer) {
            OpenCodeService.getInstance(project).startServer()
        }
    }
    
    override fun shouldBeAvailable(project: Project): Boolean = true
    
    override fun init(toolWindow: ToolWindow) {
        toolWindow.stripeTitle = OpenCodeBundle.message("toolwindow.title")
    }
}
