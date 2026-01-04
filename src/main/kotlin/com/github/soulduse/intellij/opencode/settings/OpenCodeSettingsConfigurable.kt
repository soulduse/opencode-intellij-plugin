package com.github.soulduse.intellij.opencode.settings

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class OpenCodeSettingsConfigurable : Configurable {
    
    private var settingsComponent: OpenCodeSettingsComponent? = null
    
    override fun getDisplayName(): String = OpenCodeBundle.message("settings.title")
    
    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.preferredFocusedComponent
    }
    
    override fun createComponent(): JComponent? {
        settingsComponent = OpenCodeSettingsComponent()
        return settingsComponent?.panel
    }
    
    override fun isModified(): Boolean {
        val settings = OpenCodeSettingsState.getInstance()
        val component = settingsComponent ?: return false
        
        return component.opencodeCommand != settings.opencodeCommand ||
                component.serverPort != settings.serverPort ||
                component.autoStartServer != settings.autoStartServer ||
                component.theme != settings.theme ||
                component.language != settings.language ||
                component.suppressCommandNotFound != settings.suppressCommandNotFound
    }
    
    override fun apply() {
        val settings = OpenCodeSettingsState.getInstance()
        val component = settingsComponent ?: return
        
        settings.opencodeCommand = component.opencodeCommand
        settings.serverPort = component.serverPort
        settings.autoStartServer = component.autoStartServer
        settings.theme = component.theme
        settings.language = component.language
        settings.suppressCommandNotFound = component.suppressCommandNotFound
    }
    
    override fun reset() {
        val settings = OpenCodeSettingsState.getInstance()
        val component = settingsComponent ?: return
        
        component.opencodeCommand = settings.opencodeCommand
        component.serverPort = settings.serverPort
        component.autoStartServer = settings.autoStartServer
        component.theme = settings.theme
        component.language = settings.language
        component.suppressCommandNotFound = settings.suppressCommandNotFound
    }
    
    override fun disposeUIResources() {
        settingsComponent = null
    }
}
