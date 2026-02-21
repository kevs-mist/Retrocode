package retrocode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;

public class EditorWindow {
    private JFrame frame;
    private RetroTextArea editor;
    private FolderExplorerPanel folderPanel;
    private JLabel statusLabel;
    private JLabel lineColLabel;
    private java.io.File currentFile;

    public void showEditor() {
        // Initialize with dark theme
        ThemeManager.setTheme(ThemeManager.Theme.DARK);

        frame = new JFrame("RetroCode - Untitled");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        

        // Created a panel for the main content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        JPanel toolbar = createToolbar();
        contentPanel.add(toolbar, BorderLayout.NORTH);

        editor = new RetroTextArea();
        contentPanel.add(editor.getScrollPane(), BorderLayout.CENTER);

        // Folder panel setup
        folderPanel = new FolderExplorerPanel();
        folderPanel.setFileOpenListener(file -> {
            try (java.io.FileReader reader = new java.io.FileReader(file)) {
                editor.getTextArea().read(reader, null);
                frame.setTitle("RetroCode - " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to open file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // collapsible panel container for folder panel
        JPanel folderPanelContainer = new JPanel(new BorderLayout()) {
            private boolean isCollapsed = false;
            private int originalWidth = 200;
            
            {
                //  button to the folder panel
                folderPanel.getCollapseButton().addActionListener(e -> {
                    isCollapsed = !isCollapsed;
                    if (isCollapsed) {
                        setPreferredSize(new Dimension(40, 600));
                        folderPanel.setVisible(false);
                    } else {
                        setPreferredSize(new Dimension(originalWidth, 600));
                        folderPanel.setVisible(true);
                    }
                    revalidate();
                    repaint();
                });
            }
        };
        folderPanelContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        folderPanelContainer.add(folderPanel, BorderLayout.CENTER);
        contentPanel.add(folderPanelContainer, BorderLayout.WEST);
        folderPanelContainer.setPreferredSize(new Dimension(200, 600));

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
        
        // Setup editor listeners
        setupEditorListeners();
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private void setupEditorListeners() {
        editor.getTextArea().addCaretListener(e -> {
            updateLineColDisplay();
        });
        
        editor.getTextArea().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStatus(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStatus(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStatus(); }
        });
    }
    
    private void updateStatus() {
        if (currentFile != null) {
            statusLabel.setText("File: " + currentFile.getName());
        } else {
            statusLabel.setText("Untitled");
        }
    }
    
    private void updateLineColDisplay() {
        try {
            int caretPosition = editor.getTextArea().getCaretPosition();
            int line = editor.getTextArea().getLineOfOffset(caretPosition);
            int column = caretPosition - editor.getTextArea().getLineStartOffset(line);
            lineColLabel.setText("Line: " + (line + 1) + " | Col: " + (column + 1));
        } catch (Exception e) {
            lineColLabel.setText("Line: 1 | Col: 1");
        }
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new java.awt.BorderLayout());
        statusBar.setBackground(ThemeManager.getPanelBackground());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        statusBar.setPreferredSize(new Dimension(0, 24));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        statusLabel.setForeground(ThemeManager.getButtonForeground());
        
        lineColLabel = new JLabel("Line: 1 | Col: 1");
        lineColLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lineColLabel.setForeground(ThemeManager.getButtonForeground());
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(lineColLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void setupKeyboardShortcuts() {
        // Create input map and action map for keyboard shortcuts
        javax.swing.InputMap inputMap = editor.getTextArea().getInputMap();
        javax.swing.ActionMap actionMap = editor.getTextArea().getActionMap();
        
        // Ctrl+N for new file
        inputMap.put(javax.swing.KeyStroke.getKeyStroke("control N"), "newFile");
        actionMap.put("newFile", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                FileManager.newFile(frame, editor.getTextArea());
                updateStatus();
            }
        });
        
        // Ctrl+O for open file
        inputMap.put(javax.swing.KeyStroke.getKeyStroke("control O"), "openFile");
        actionMap.put("openFile", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                FileManager.openFile(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            }
        });
        
        // Ctrl+S for save file
        inputMap.put(javax.swing.KeyStroke.getKeyStroke("control S"), "saveFile");
        actionMap.put("saveFile", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                FileManager.saveFile(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            }
        });
        
        // Ctrl+Shift+S for save as
        inputMap.put(javax.swing.KeyStroke.getKeyStroke("control shift S"), "saveFileAs");
        actionMap.put("saveFileAs", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                FileManager.saveFileAs(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            }
        });
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, ThemeManager.getToolbarGradientStart(), getWidth(), getHeight(), ThemeManager.getToolbarGradientEnd());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        toolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        toolbar.setPreferredSize(new Dimension(0, 44));

        JButton fileButton = createToolbarButton("\uD83D\uDCC4 File");
        JButton openFolderButton = createToolbarButton("\uD83D\uDCC1 Open Folder");
        JButton saveButton = createToolbarButton("\uD83D\uDCC3 Save");
        JButton themeButton = createThemeButton();

        fileButton.addActionListener(e -> {
            JPopupMenu fileMenu = new JPopupMenu();
            JMenuItem newItem = new JMenuItem("New (Ctrl+N)");
            JMenuItem openItem = new JMenuItem("Open (Ctrl+O)");
            JMenuItem saveItem = new JMenuItem("Save (Ctrl+S)");
            JMenuItem saveAsItem = new JMenuItem("Save As (Ctrl+Shift+S)");
            
            newItem.addActionListener(evt -> {
                FileManager.newFile(frame, editor.getTextArea());
                updateStatus();
            });
            openItem.addActionListener(evt -> {
                FileManager.openFile(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            });
            saveItem.addActionListener(evt -> {
                FileManager.saveFile(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            });
            saveAsItem.addActionListener(evt -> {
                FileManager.saveFileAs(frame, editor.getTextArea());
                currentFile = FileManager.getCurrentFile();
                updateStatus();
            });
            
            fileMenu.add(newItem);
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsItem);
            fileMenu.show(fileButton, 0, fileButton.getHeight());
        });

        openFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File folder = chooser.getSelectedFile();
                folderPanel.setRootFolder(folder);
            }
        });
        
        saveButton.addActionListener(e -> {
            if (currentFile != null) {
                FileManager.saveFile(frame, editor.getTextArea());
            } else {
                FileManager.saveFileAs(frame, editor.getTextArea());
            }
        });

        toolbar.add(fileButton);
        toolbar.add(openFolderButton);
        toolbar.add(saveButton);
        toolbar.add(themeButton);
        
        return toolbar;
    }

    private JButton createToolbarButton(String text) {
        JButton button = new JButton(text) {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(ThemeManager.getAccentColor());
                } else if (getModel().isRollover()) {
                    g2d.setColor(ThemeManager.getButtonHoverBackground());
                } else {
                    g2d.setColor(ThemeManager.getButtonBackground());
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                // Add subtle border
                g2d.setColor(ThemeManager.getBorderColor());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        button.setForeground(ThemeManager.getButtonForeground());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });
        return button;
    }
    
    private JButton createThemeButton() {
        JButton button = new JButton("\uD83C\uDF19") {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(ThemeManager.getAccentColor());
                } else if (getModel().isRollover()) {
                    g2d.setColor(ThemeManager.getButtonHoverBackground());
                } else {
                    g2d.setColor(ThemeManager.getButtonBackground());
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                // Add subtle border
                g2d.setColor(ThemeManager.getBorderColor());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 16));
        button.setForeground(ThemeManager.getButtonForeground());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.setToolTipText("Toggle Theme");
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });
        
        button.addActionListener(e -> {
            ThemeManager.Theme newTheme = ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? 
                ThemeManager.Theme.LIGHT : ThemeManager.Theme.DARK;
            
            ThemeManager.setTheme(newTheme);
            
            // Update button icon
            button.setText(newTheme == ThemeManager.Theme.DARK ? "\uD83C\uDF19" : "\u2600");
            
            // Update editor colors
            editor.applyThemeColors();
            
            // Update toolbar appearance
            frame.revalidate();
            frame.repaint();
        });
        
        return button;
    }
}
