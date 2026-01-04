package com.github.soulduse.intellij.opencode.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class InsertFileReferenceAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        val reference = buildFileReference(project, editor, virtualFile)
        if (reference != null) {
            // Copy to clipboard and show tool window
            copyToClipboard(reference)
            showOpenCodeToolWindow(project)
        }
    }
    
    private fun buildFileReference(project: Project, editor: Editor?, virtualFile: VirtualFile?): String? {
        if (virtualFile == null) return null
        
        val projectPath = project.basePath ?: return null
        val filePath = virtualFile.path
        
        // Get relative path
        val relativePath = if (filePath.startsWith(projectPath)) {
            filePath.removePrefix(projectPath).removePrefix("/")
        } else {
            virtualFile.name
        }
        
        // Check for selection
        if (editor != null && editor.selectionModel.hasSelection()) {
            val selectionStart = editor.selectionModel.selectionStartPosition
            val selectionEnd = editor.selectionModel.selectionEndPosition
            
            if (selectionStart != null && selectionEnd != null) {
                val startLine = selectionStart.line + 1
                val endLine = selectionEnd.line + 1
                
                return if (startLine == endLine) {
                    "@$relativePath#L$startLine"
                } else {
                    "@$relativePath#L$startLine-$endLine"
                }
            }
        }
        
        // No selection, just return file path
        return "@$relativePath"
    }
    
    private fun copyToClipboard(text: String) {
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }
    
    private fun showOpenCodeToolWindow(project: Project) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("OpenCode")
        toolWindow?.show()
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        e.presentation.isEnabledAndVisible = project != null && virtualFile != null
    }
}
