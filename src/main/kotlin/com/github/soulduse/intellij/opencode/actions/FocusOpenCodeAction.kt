package com.github.soulduse.intellij.opencode.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

class FocusOpenCodeAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("OpenCode")
        if (toolWindow != null) {
            if (toolWindow.isVisible) {
                // If visible, just activate/focus
                toolWindow.activate(null)
            } else {
                // If not visible, show it
                toolWindow.show()
            }
        }
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
