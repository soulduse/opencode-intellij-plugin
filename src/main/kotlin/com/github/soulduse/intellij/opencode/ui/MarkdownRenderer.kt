package com.github.soulduse.intellij.opencode.ui

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlRenderer
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet

/**
 * Renders markdown content as Swing components with code highlighting and copy functionality.
 */
class MarkdownRenderer {
    
    private val parser: Parser = Parser.builder().build()
    
    /**
     * Render markdown text to a JPanel containing styled components.
     */
    fun render(markdown: String): JPanel {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = JBUI.Borders.empty()
        }
        
        val document = parser.parse(markdown)
        var node: Node? = document.firstChild
        
        while (node != null) {
            val component = renderNode(node)
            if (component != null) {
                panel.add(component)
                panel.add(Box.createVerticalStrut(4))
            }
            node = node.next
        }
        
        return panel
    }
    
    private fun renderNode(node: Node): JComponent? {
        return when (node) {
            is Paragraph -> renderParagraph(node)
            is Heading -> renderHeading(node)
            is FencedCodeBlock -> renderCodeBlock(node.literal, node.info)
            is IndentedCodeBlock -> renderCodeBlock(node.literal, null)
            is BlockQuote -> renderBlockQuote(node)
            is BulletList -> renderList(node, ordered = false)
            is OrderedList -> renderList(node, ordered = true)
            is ThematicBreak -> renderHorizontalRule()
            else -> null
        }
    }
    
    private fun renderParagraph(paragraph: Paragraph): JComponent {
        val html = renderInlineToHtml(paragraph)
        return createHtmlLabel(html)
    }
    
    private fun renderHeading(heading: Heading): JComponent {
        val html = renderInlineToHtml(heading)
        val fontSize = when (heading.level) {
            1 -> 20
            2 -> 18
            3 -> 16
            4 -> 14
            else -> 13
        }
        
        return JLabel("<html><b>$html</b></html>").apply {
            font = font.deriveFont(Font.BOLD, fontSize.toFloat())
            border = JBUI.Borders.emptyTop(if (heading.level <= 2) 8 else 4)
            alignmentX = Component.LEFT_ALIGNMENT
        }
    }
    
    private fun renderCodeBlock(code: String, language: String?): JComponent {
        val codePanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.customLine(JBColor.border(), 1)
            background = JBColor(Color(245, 245, 245), Color(43, 43, 43))
            alignmentX = Component.LEFT_ALIGNMENT
        }
        
        // Header with language and copy button
        val headerPanel = JPanel(BorderLayout()).apply {
            background = JBColor(Color(235, 235, 235), Color(53, 53, 53))
            border = JBUI.Borders.empty(4, 8)
        }
        
        val languageLabel = JLabel(language?.uppercase() ?: "CODE").apply {
            font = font.deriveFont(Font.BOLD, 10f)
            foreground = JBColor.GRAY
        }
        headerPanel.add(languageLabel, BorderLayout.WEST)
        
        val copyButton = JButton("Copy").apply {
            font = font.deriveFont(10f)
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(50, 20)
            
            addActionListener {
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(code.trim()), null)
                text = "Copied!"
                Timer(1500) { text = "Copy" }.apply { 
                    isRepeats = false
                    start()
                }
            }
        }
        headerPanel.add(copyButton, BorderLayout.EAST)
        
        // Code area
        val codeArea = JTextArea(code.trim()).apply {
            font = Font(Font.MONOSPACED, Font.PLAIN, 12)
            background = codePanel.background
            foreground = JBColor.foreground()
            isEditable = false
            border = JBUI.Borders.empty(8)
            caretColor = JBColor.foreground()
        }
        
        val scrollPane = JScrollPane(codeArea).apply {
            border = null
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
        }
        
        codePanel.add(headerPanel, BorderLayout.NORTH)
        codePanel.add(scrollPane, BorderLayout.CENTER)
        
        // Set max height
        codePanel.maximumSize = Dimension(Int.MAX_VALUE, 400)
        
        return codePanel
    }
    
    private fun renderBlockQuote(blockQuote: BlockQuote): JComponent {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, JBColor.GRAY),
                JBUI.Borders.empty(4, 12, 4, 4)
            )
            background = JBColor(Color(248, 248, 248), Color(45, 45, 45))
            alignmentX = Component.LEFT_ALIGNMENT
        }
        
        var child: Node? = blockQuote.firstChild
        while (child != null) {
            val component = renderNode(child)
            if (component != null) {
                panel.add(component)
            }
            child = child.next
        }
        
        return panel
    }
    
    private fun renderList(listNode: Node, ordered: Boolean): JComponent {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = JBUI.Borders.emptyLeft(16)
            alignmentX = Component.LEFT_ALIGNMENT
        }
        
        var child: Node? = listNode.firstChild
        var index = 1
        
        while (child != null) {
            if (child is ListItem) {
                val bullet = if (ordered) "$index." else "\u2022"
                val itemPanel = JPanel(BorderLayout()).apply {
                    isOpaque = false
                    alignmentX = Component.LEFT_ALIGNMENT
                }
                
                val bulletLabel = JLabel("$bullet ").apply {
                    verticalAlignment = SwingConstants.TOP
                    border = JBUI.Borders.emptyRight(4)
                }
                
                val contentPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    isOpaque = false
                }
                
                var itemChild: Node? = child.firstChild
                while (itemChild != null) {
                    val component = renderNode(itemChild)
                    if (component != null) {
                        contentPanel.add(component)
                    }
                    itemChild = itemChild.next
                }
                
                itemPanel.add(bulletLabel, BorderLayout.WEST)
                itemPanel.add(contentPanel, BorderLayout.CENTER)
                panel.add(itemPanel)
                
                index++
            }
            child = child.next
        }
        
        return panel
    }
    
    private fun renderHorizontalRule(): JComponent {
        return JSeparator().apply {
            maximumSize = Dimension(Int.MAX_VALUE, 2)
            alignmentX = Component.LEFT_ALIGNMENT
        }
    }
    
    private fun renderInlineToHtml(node: Node): String {
        val sb = StringBuilder()
        var child: Node? = node.firstChild
        
        while (child != null) {
            when (child) {
                is Text -> sb.append(escapeHtml(child.literal))
                is Code -> sb.append("<code style='background-color:#f0f0f0;padding:2px 4px;border-radius:3px;font-family:monospace;'>${escapeHtml(child.literal)}</code>")
                is Emphasis -> sb.append("<i>${getInlineText(child)}</i>")
                is StrongEmphasis -> sb.append("<b>${getInlineText(child)}</b>")
                is Link -> sb.append("<a href='${child.destination}'>${getInlineText(child)}</a>")
                is SoftLineBreak -> sb.append(" ")
                is HardLineBreak -> sb.append("<br>")
            }
            child = child.next
        }
        
        return sb.toString()
    }
    
    private fun getInlineText(node: Node): String {
        val sb = StringBuilder()
        var child: Node? = node.firstChild
        
        while (child != null) {
            when (child) {
                is Text -> sb.append(escapeHtml(child.literal))
                is Code -> sb.append("<code>${escapeHtml(child.literal)}</code>")
                is Emphasis -> sb.append("<i>${getInlineText(child)}</i>")
                is StrongEmphasis -> sb.append("<b>${getInlineText(child)}</b>")
            }
            child = child.next
        }
        
        return sb.toString()
    }
    
    private fun createHtmlLabel(html: String): JComponent {
        return JEditorPane().apply {
            contentType = "text/html"
            isEditable = false
            isOpaque = false
            border = null
            
            val kit = HTMLEditorKit()
            val styleSheet = StyleSheet()
            styleSheet.addRule("body { font-family: ${UIManager.getFont("Label.font").family}; font-size: 13pt; margin: 0; padding: 0; }")
            styleSheet.addRule("code { background-color: #f0f0f0; padding: 2px 4px; border-radius: 3px; font-family: monospace; }")
            styleSheet.addRule("a { color: #589df6; }")
            kit.styleSheet = styleSheet
            editorKit = kit
            
            text = "<html><body>$html</body></html>"
            alignmentX = Component.LEFT_ALIGNMENT
            
            // Handle link clicks
            addHyperlinkListener { e ->
                if (e.eventType == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.url.toURI())
                    } catch (ex: Exception) {
                        // Ignore
                    }
                }
            }
        }
    }
    
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
    }
}
