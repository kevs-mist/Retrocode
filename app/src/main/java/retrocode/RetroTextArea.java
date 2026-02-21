package retrocode;

import java.awt.Font;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class RetroTextArea {
    private RSyntaxTextArea textarea;
    private RTextScrollPane scrollPane;

    public RetroTextArea() {
        textarea = new RSyntaxTextArea(20, 60);
        textarea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textarea.setCodeFoldingEnabled(true);
        textarea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        textarea.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 20, 16, 20));
        textarea.setTabSize(4);
        textarea.setAutoIndentEnabled(true);
        textarea.setBracketMatchingEnabled(true);
        textarea.setHighlightCurrentLine(true);
        
        // Rounded corners for scroll pane
        scrollPane = new RTextScrollPane(textarea) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ThemeManager.getScrollPaneBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        scrollPane.setOpaque(false);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        // Apply initial theme colors after scrollPane is initialized
        applyThemeColors();
    }
    
    public void applyThemeColors() {
        textarea.setBackground(ThemeManager.getEditorBackground());
        textarea.setForeground(ThemeManager.getEditorForeground());
        textarea.setCaretColor(ThemeManager.getCaretColor());
        textarea.setSelectionColor(ThemeManager.getSelectionColor());
        textarea.setSelectedTextColor(ThemeManager.getSelectedTextColor());
        textarea.setCurrentLineHighlightColor(ThemeManager.getSelectionColor());
        textarea.setMatchedBracketBGColor(ThemeManager.getAccentColor());
        textarea.setMatchedBracketBorderColor(ThemeManager.getAccentColor());
        
        if (scrollPane != null) {
            scrollPane.repaint();
        }
    }

    public RSyntaxTextArea getTextArea() {
        return textarea;
    }

    public RTextScrollPane getScrollPane() {
        return scrollPane;
    }
}
