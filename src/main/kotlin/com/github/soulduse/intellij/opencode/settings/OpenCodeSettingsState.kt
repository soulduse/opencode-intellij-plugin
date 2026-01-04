package com.github.soulduse.intellij.opencode.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.github.soulduse.intellij.opencode.settings.OpenCodeSettingsState",
    storages = [Storage("OpenCodeSettings.xml")]
)
class OpenCodeSettingsState : PersistentStateComponent<OpenCodeSettingsState> {
    
    // General settings
    var opencodeCommand: String = "opencode"
    var serverPort: Int = 4096
    var autoStartServer: Boolean = true
    
    // Appearance settings
    var theme: Theme = Theme.SYSTEM
    var language: Language = Language.ENGLISH
    
    // Advanced settings
    var suppressCommandNotFound: Boolean = false
    
    enum class Theme {
        LIGHT, DARK, SYSTEM
    }
    
    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        KOREAN("ko", "한국어")
    }
    
    override fun getState(): OpenCodeSettingsState = this
    
    override fun loadState(state: OpenCodeSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
    
    companion object {
        @JvmStatic
        fun getInstance(): OpenCodeSettingsState {
            return ApplicationManager.getApplication().getService(OpenCodeSettingsState::class.java)
        }
    }
}
