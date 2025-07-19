package aerodesk.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Icon management utility for AeroDesk Pro
 * Handles icon creation and loading for consistent display across systems
 */
public class IconManager {
    
    /**
     * Create a text-based icon that works across all systems
     * Returns a simple text representation that should work everywhere
     */
    public static String getTextIcon(String iconType) {
        switch (iconType.toLowerCase()) {
            case "flight":
            case "plane":
                return "✈";
            case "user":
            case "passenger":
            case "person":
                return "👤";
            case "baggage":
            case "luggage":
                return "📦";
            case "gate":
            case "door":
                return "🚪";
            case "status":
            case "dashboard":
                return "📊";
            case "api":
            case "web":
                return "🌐";
            case "reports":
            case "logs":
                return "📋";
            case "logout":
            case "exit":
                return "❌";
            case "add":
            case "create":
                return "➕";
            case "edit":
            case "update":
                return "✏";
            case "delete":
            case "remove":
                return "🗑";
            case "search":
            case "find":
                return "🔍";
            case "refresh":
            case "reload":
                return "🔄";
            case "save":
                return "💾";
            case "print":
                return "🖨";
            case "export":
                return "📤";
            case "import":
                return "📥";
            case "settings":
            case "config":
                return "⚙";
            case "help":
                return "❓";
            case "info":
                return "ℹ";
            case "warning":
                return "⚠";
            case "error":
                return "❌";
            case "success":
                return "✅";
            default:
                return "●";
        }
    }
    
    /**
     * Create an ImageIcon for buttons and labels that will work across all systems
     * This method creates a proper ImageIcon instead of relying on text-based icons
     */
    public static ImageIcon createButtonIcon(String iconType, Color color, int size) {
        return new ImageIcon(createColoredIcon(iconType, color, size));
    }
    
    /**
     * Create a simple text label with icon that works across all systems
     * Uses HTML formatting to ensure proper display
     */
    public static String createIconLabel(String iconType, String text) {
        return "<html><font size='+1'>" + getTextIcon(iconType) + "</font> " + text + "</html>";
    }
    
    /**
     * Create a simple colored icon as a BufferedImage
     */
    public static BufferedImage createColoredIcon(String iconType, Color color, int size) {
        BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Set color
        g2d.setColor(color);
        
        // Draw icon based on type
        switch (iconType.toLowerCase()) {
            case "flight":
            case "plane":
                drawPlaneIcon(g2d, size);
                break;
            case "user":
            case "passenger":
                drawUserIcon(g2d, size);
                break;
            case "baggage":
            case "luggage":
                drawBaggageIcon(g2d, size);
                break;
            case "gate":
            case "door":
                drawGateIcon(g2d, size);
                break;
            case "status":
            case "dashboard":
                drawStatusIcon(g2d, size);
                break;
            case "api":
            case "web":
                drawApiIcon(g2d, size);
                break;
            case "reports":
            case "logs":
                drawReportsIcon(g2d, size);
                break;
            default:
                drawDefaultIcon(g2d, size);
                break;
        }
        
        g2d.dispose();
        return icon;
    }
    
    // Icon drawing methods
    private static void drawPlaneIcon(Graphics2D g2d, int size) {
        int margin = size / 4;
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Draw plane body
        g2d.fillPolygon(
            new int[]{margin, centerX, size - margin},
            new int[]{centerY, margin, centerY},
            3
        );
        
        // Draw wings
        g2d.fillRect(centerX - 2, centerY - 8, 4, 16);
    }
    
    private static void drawUserIcon(Graphics2D g2d, int size) {
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = size / 4;
        
        // Draw head
        g2d.fillOval(centerX - radius, centerY - radius - 2, radius * 2, radius * 2);
        
        // Draw body
        g2d.fillRect(centerX - radius/2, centerY + radius - 2, radius, radius * 2);
    }
    
    private static void drawBaggageIcon(Graphics2D g2d, int size) {
        int margin = size / 4;
        int width = size - 2 * margin;
        int height = width * 2 / 3;
        
        // Draw suitcase
        g2d.fillRect(margin, margin, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(margin, margin, width, height);
        
        // Draw handle
        g2d.fillRect(margin + width/3, margin - 4, width/3, 4);
    }
    
    private static void drawGateIcon(Graphics2D g2d, int size) {
        int margin = size / 4;
        int width = size - 2 * margin;
        int height = width * 3 / 2;
        
        // Draw gate frame
        g2d.fillRect(margin, margin, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(margin, margin, width, height);
        
        // Draw door opening
        g2d.fillRect(margin + width/4, margin + height/4, width/2, height/2);
    }
    
    private static void drawStatusIcon(Graphics2D g2d, int size) {
        int margin = size / 4;
        int width = size - 2 * margin;
        int height = width;
        
        // Draw chart bars
        int barWidth = width / 4;
        int[] heights = {height/2, height*3/4, height/4, height};
        
        for (int i = 0; i < 4; i++) {
            g2d.fillRect(margin + i * barWidth, margin + height - heights[i], 
                        barWidth - 2, heights[i]);
        }
    }
    
    private static void drawApiIcon(Graphics2D g2d, int size) {
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = size / 4;
        
        // Draw globe
        g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        // Draw latitude lines
        g2d.drawLine(centerX - radius, centerY, centerX + radius, centerY);
        g2d.drawLine(centerX - radius, centerY - radius/2, centerX + radius, centerY - radius/2);
        g2d.drawLine(centerX - radius, centerY + radius/2, centerX + radius, centerY + radius/2);
    }
    
    private static void drawReportsIcon(Graphics2D g2d, int size) {
        int margin = size / 4;
        int width = size - 2 * margin;
        int height = width * 4 / 3;
        
        // Draw clipboard
        g2d.fillRect(margin, margin, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(margin, margin, width, height);
        
        // Draw lines
        for (int i = 1; i <= 3; i++) {
            int y = margin + height/4 * i;
            g2d.drawLine(margin + 4, y, margin + width - 4, y);
        }
    }
    
    private static void drawDefaultIcon(Graphics2D g2d, int size) {
        int centerX = size / 2;
        int centerY = size / 2;
        int radius = size / 4;
        
        g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }
} 