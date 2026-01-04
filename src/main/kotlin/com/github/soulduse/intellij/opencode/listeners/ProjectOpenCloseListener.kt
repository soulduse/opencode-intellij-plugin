package com.github.soulduse.intellij.opencode.listeners

import com.github.soulduse.intellij.opencode.services.OpenCodeService
import com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class ProjectOpenCloseListener : ProjectManagerListener {
    
    private val logger = Logger.getInstance(ProjectOpenCloseListener::class.java)
    
    override fun projectOpened(project: Project) {
        logger.info("Project opened: ${project.name}")
        
        // Auto start server if enabled
        if (OpenCodeSettingsState.getInstance().autoStartServer) {
            try {
                val service = OpenCodeService.getInstance(project)
                service.startServer()
            } catch (e: Exception) {
                logger.warn("Failed to auto-start OpenCode server", e)
            }
        }
    }
    
    override fun projectClosed(project: Project) {
        logger.info("Project closed: ${project.name}")
        
        try {
            val service = OpenCodeService.getInstance(project)
            service.stopServer()
        } catch (e: Exception) {
            logger.warn("Failed to stop OpenCode server", e)
        }
    }
}
