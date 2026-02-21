package retrocode;

import java.awt.Color;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class ThemeManager {
    public enum Theme {
        LIGHT, DARK
    }
    
    private static Theme currentTheme = Theme.DARK;
    
    public static void setTheme(Theme theme) {
        currentTheme = theme;
        try {
            if (theme == Theme.DARK) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            System.err.println("Could not load theme: " + theme);
        }
    }
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public static Color getEditorBackground() {
        return currentTheme == Theme.DARK ? 
            new Color(30, 30, 30) : 
            new Color(255, 255, 255);
    }
    
    public static Color getEditorForeground() {
        return currentTheme == Theme.DARK ? 
            new Color(230, 230, 230) : 
            new Color(20, 20, 20);
    }
    
    public static Color getCaretColor() {
        return currentTheme == Theme.DARK ? 
            new Color(100, 200, 255) : 
            new Color(0, 120, 215);
    }
    
    public static Color getSelectionColor() {
        return currentTheme == Theme.DARK ? 
            new Color(50, 100, 150, 120) : 
            new Color(0, 120, 215, 80);
    }
    
    public static Color getSelectedTextColor() {
        return currentTheme == Theme.DARK ? 
            new Color(255, 255, 255) : 
            new Color(255, 255, 255);
    }
    
    public static Color getScrollPaneBackground() {
        return currentTheme == Theme.DARK ? 
            new Color(25, 25, 25, 240) : 
            new Color(250, 250, 250, 240);
    }
    
    public static Color getToolbarGradientStart() {
        return currentTheme == Theme.DARK ? 
            new Color(45, 45, 45) : 
            new Color(245, 245, 245);
    }
    
    public static Color getToolbarGradientEnd() {
        return currentTheme == Theme.DARK ? 
            new Color(25, 25, 25) : 
            new Color(235, 235, 235);
    }
    
    public static Color getButtonBackground() {
        return currentTheme == Theme.DARK ? 
            new Color(60, 60, 60) : 
            new Color(240, 240, 240);
    }
    
    public static Color getButtonHoverBackground() {
        return currentTheme == Theme.DARK ? 
            new Color(80, 80, 80) : 
            new Color(220, 220, 220);
    }
    
    public static Color getButtonForeground() {
        return currentTheme == Theme.DARK ? 
            new Color(230, 230, 230) : 
            new Color(20, 20, 20);
    }
    
    public static Color getPanelBackground() {
        return currentTheme == Theme.DARK ? 
            new Color(35, 35, 35) : 
            new Color(248, 248, 248);
    }
    
    public static Color getBorderColor() {
        return currentTheme == Theme.DARK ? 
            new Color(60, 60, 60) : 
            new Color(200, 200, 200);
    }
    
    public static Color getAccentColor() {
        return currentTheme == Theme.DARK ? 
            new Color(100, 200, 255) : 
            new Color(0, 120, 215);
    }
}
