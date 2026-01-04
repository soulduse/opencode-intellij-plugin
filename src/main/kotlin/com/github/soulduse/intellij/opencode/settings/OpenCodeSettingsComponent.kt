package com.github.soulduse.intellij.opencode.settings

import com.github.soulduse.intellij.opencode.OpenCodeBundle
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class OpenCodeSettingsComponent {
    
    val panel: JPanel
    
    // General settings
    private val commandField = JBTextField()
    private val portField = JBTextField()
    private val autoStartCheckbox = JBCheckBox(OpenCodeBundle.message("settings.general.autoStart"))
    
    // Appearance settings
    private val themeComboBox = ComboBox(OpenCodeSettingsState.Theme.entries.toTypedArray())
    private val languageComboBox = ComboBox(OpenCodeSettingsState.Language.entries.toTypedArray())
    
    // Advanced settings
    private val suppressNotificationsCheckbox = JBCheckBox(OpenCodeBundle.message("settings.advanced.suppressNotifications"))
    
    init {
        // Set tooltips
        commandField.toolTipText = OpenCodeBundle.message("settings.general.command.tooltip")
        portField.toolTipText = OpenCodeBundle.message("settings.general.port.tooltip")
        autoStartCheckbox.toolTipText = OpenCodeBundle.message("settings.general.autoStart.tooltip")
        suppressNotificationsCheckbox.toolTipText = OpenCodeBundle.message("settings.advanced.suppressNotifications.tooltip")
        
        // Configure combo boxes
        themeComboBox.renderer = ThemeListCellRenderer()
        languageComboBox.renderer = LanguageListCellRenderer()
        
        panel = FormBuilder.createFormBuilder()
            // General section
            .addSeparator()
            .addComponent(createSectionLabel(OpenCodeBundle.message("settings.general")))
            .addLabeledComponent(
                JBLabel("${OpenCodeBundle.message("settings.general.command")}:"),
                commandField,
                1,
                false
            )
            .addLabeledComponent(
                JBLabel("${OpenCodeBundle.message("settings.general.port")}:"),
                portField,
                1,
                false
            )
            .addComponent(autoStartCheckbox)
            
            // Appearance section
            .addSeparator()
            .addComponent(createSectionLabel(OpenCodeBundle.message("settings.appearance")))
            .addLabeledComponent(
                JBLabel("${OpenCodeBundle.message("settings.appearance.theme")}:"),
                themeComboBox,
                1,
                false
            )
            .addLabeledComponent(
                JBLabel("${OpenCodeBundle.message("settings.appearance.language")}:"),
                languageComboBox,
                1,
                false
            )
            
            // Advanced section
            .addSeparator()
            .addComponent(createSectionLabel(OpenCodeBundle.message("settings.advanced")))
            .addComponent(suppressNotificationsCheckbox)
            
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    
    private fun createSectionLabel(text: String): JComponent {
        return JBLabel(text).apply {
            font = font.deriveFont(font.style or java.awt.Font.BOLD)
            border = JBUI.Borders.empty(10, 0, 5, 0)
        }
    }
    
    val preferredFocusedComponent: JComponent
        get() = commandField
    
    // Getters and setters
    var opencodeCommand: String
        get() = commandField.text
        set(value) {
            commandField.text = value
        }
    
    var serverPort: Int
        get() = portField.text.toIntOrNull() ?: 4096
        set(value) {
            portField.text = value.toString()
        }
    
    var autoStartServer: Boolean
        get() = autoStartCheckbox.isSelected
        set(value) {
            autoStartCheckbox.isSelected = value
        }
    
    var theme: OpenCodeSettingsState.Theme
        get() = themeComboBox.selectedItem as? OpenCodeSettingsState.Theme ?: OpenCodeSettingsState.Theme.SYSTEM
        set(value) {
            themeComboBox.selectedItem = value
        }
    
    var language: OpenCodeSettingsState.Language
        get() = languageComboBox.selectedItem as? OpenCodeSettingsState.Language ?: OpenCodeSettingsState.Language.ENGLISH
        set(value) {
            languageComboBox.selectedItem = value
        }
    
    var suppressCommandNotFound: Boolean
        get() = suppressNotificationsCheckbox.isSelected
        set(value) {
            suppressNotificationsCheckbox.isSelected = value
        }
    
    // Custom renderers for combo boxes
    private inner class ThemeListCellRenderer : javax.swing.DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: javax.swing.JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): java.awt.Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is OpenCodeSettingsState.Theme) {
                text = when (value) {
                    OpenCodeSettingsState.Theme.LIGHT -> OpenCodeBundle.message("settings.appearance.theme.light")
                    OpenCodeSettingsState.Theme.DARK -> OpenCodeBundle.message("settings.appearance.theme.dark")
                    OpenCodeSettingsState.Theme.SYSTEM -> OpenCodeBundle.message("settings.appearance.theme.system")
                }
            }
            return this
        }
    }
    
    private inner class LanguageListCellRenderer : javax.swing.DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: javax.swing.JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): java.awt.Component {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if (value is OpenCodeSettingsState.Language) {
                text = value.displayName
            }
            return this
        }
    }
}
