package retrocode;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.io.File;
import java.awt.event.*;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Desktop;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.ToolTipManager;


public class FolderExplorerPanel extends JPanel {
    private final JPanel headerPanel;
    private final JButton collapseButton;
    private final JPanel contentPanel;
    private final JPanel placeholderPanel;
    private final JButton openFolderButton;
    private final JScrollPane scrollPane;
    private final JTree folderTree;
    private final DefaultMutableTreeNode rootNode;
    private FileOpenListener fileOpenListener;
    private boolean collapsed = false;
    private File currentRoot = null;
    private Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
    private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    public interface FileOpenListener {
        void openFile(File file);
    }

    public void setFileOpenListener(FileOpenListener listener) {
        this.fileOpenListener = listener;
    }

    public JButton getCollapseButton() {
        return collapseButton;
    }

    public FolderExplorerPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        // Header bar for collapse/expand
        headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, new Color(50, 55, 70), getWidth(), getHeight(), new Color(30, 32, 40));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        collapseButton = new JButton("▼") {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                if (getModel().isRollover()) {
                    setBackground(new Color(70, 80, 100));
                } else {
                    setBackground(new Color(35, 38, 45));
                }
                super.paintComponent(g);
            }
        };
        collapseButton.setFocusable(false);
        collapseButton.setMargin(new Insets(0, 6, 0, 6));
        collapseButton.setFont(new Font("JetBrains Mono", Font.BOLD, 10));
        collapseButton.setForeground(Color.WHITE);
        collapseButton.setBorderPainted(false);
        collapseButton.setOpaque(true);
        collapseButton.setContentAreaFilled(false);
        collapseButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        collapseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                collapseButton.setBackground(new Color(70, 80, 100));
                collapseButton.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                collapseButton.setBackground(new Color(35, 38, 45));
                collapseButton.setOpaque(true);
            }
        });
        headerPanel.add(collapseButton, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel (tree or placeholder)
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Placeholder panel
        placeholderPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                g2d.setColor(new Color(40, 44, 52, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        placeholderPanel.setOpaque(false);
        placeholderPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel placeholderLabel = new JLabel("No folder open");
        placeholderLabel.setForeground(Color.LIGHT_GRAY);
        placeholderLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        openFolderButton = new JButton("Open Folder...") {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                if (getModel().isRollover()) {
                    setBackground(new Color(80, 90, 110));
                } else {
                    setBackground(new Color(60, 63, 65));
                }
                super.paintComponent(g);
            }
        };
        openFolderButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        openFolderButton.setForeground(Color.WHITE);
        openFolderButton.setFocusPainted(false);
        openFolderButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        openFolderButton.setContentAreaFilled(false);
        openFolderButton.setOpaque(true);
        openFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                openFolderButton.setBackground(new Color(80, 90, 110));
                openFolderButton.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                openFolderButton.setBackground(new Color(60, 63, 65));
                openFolderButton.setOpaque(true);
            }
        });
        JPanel innerPanel = new JPanel();
        innerPanel.setOpaque(false);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.add(placeholderLabel);
        innerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        innerPanel.add(openFolderButton);
        placeholderPanel.add(innerPanel);

        // Tree setup
        rootNode = new DefaultMutableTreeNode("Root");
        folderTree = new JTree(rootNode) {
            public String getToolTipText(MouseEvent evt) {
                TreePath path = getPathForLocation(evt.getX(), evt.getY());
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof File) {
                        return ((File) userObject).getAbsolutePath();
                    }
                }
                return null;
            }
        };
        ToolTipManager.sharedInstance().registerComponent(folderTree);
        folderTree.setRootVisible(false);
        folderTree.setShowsRootHandles(true);
        folderTree.setCellRenderer(new FileTreeCellRenderer());
        folderTree.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        folderTree.setBackground(new Color(40, 44, 52, 220));
        folderTree.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        folderTree.setForeground(Color.WHITE);
        folderTree.setRowHeight(22);
        scrollPane = new JScrollPane(folderTree);

        // Context menu
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem revealItem = new JMenuItem("Reveal in Explorer");
        JMenuItem newFileItem = new JMenuItem("New File");
        JMenuItem newFolderItem = new JMenuItem("New Folder");
        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem deleteItem = new JMenuItem("Delete");
        contextMenu.add(openItem);
        contextMenu.add(revealItem);
        contextMenu.addSeparator();
        contextMenu.add(newFileItem);
        contextMenu.add(newFolderItem);
        contextMenu.addSeparator();
        contextMenu.add(renameItem);
        contextMenu.add(deleteItem);

        folderTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    int row = folderTree.getClosestRowForLocation(e.getX(), e.getY());
                    folderTree.setSelectionRow(row);
                    TreePath path = folderTree.getPathForRow(row);
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object userObject = node.getUserObject();
                        File file = userObject instanceof File ? (File) userObject : null;
                        openItem.setEnabled(file != null && file.isFile());
                        revealItem.setEnabled(file != null);
                        newFileItem.setEnabled(file != null && file.isDirectory());
                        newFolderItem.setEnabled(file != null && file.isDirectory());
                        renameItem.setEnabled(file != null);
                        deleteItem.setEnabled(file != null);
                        contextMenu.show(folderTree, e.getX(), e.getY());
                    }
                }
            }
        });

        // Context menu actions (implementations can be filled in as needed)
        openItem.addActionListener(e -> {
            File file = getSelectedFile();
            if (file != null && file.isFile() && fileOpenListener != null) {
                fileOpenListener.openFile(file);
            }
        });
        revealItem.addActionListener(e -> {
            File file = getSelectedFile();
            if (file != null) {
                try {
                    Desktop.getDesktop().open(file.getParentFile());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not reveal in explorer.");
                }
            }
        });
        newFileItem.addActionListener(e -> {
            File dir = getSelectedFile();
            if (dir != null && dir.isDirectory()) {
                String name = JOptionPane.showInputDialog(this, "New file name:");
                if (name != null && !name.trim().isEmpty()) {
                    File newFile = new File(dir, name);
                    try {
                        if (newFile.createNewFile()) {
                            refreshCurrentRoot();
                        } else {
                            JOptionPane.showMessageDialog(this, "File already exists.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Could not create file.");
                    }
                }
            }
        });
        newFolderItem.addActionListener(e -> {
            File dir = getSelectedFile();
            if (dir != null && dir.isDirectory()) {
                String name = JOptionPane.showInputDialog(this, "New folder name:");
                if (name != null && !name.trim().isEmpty()) {
                    File newDir = new File(dir, name);
                    if (newDir.mkdir()) {
                        refreshCurrentRoot();
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not create folder.");
                    }
                }
            }
        });
        renameItem.addActionListener(e -> {
            File file = getSelectedFile();
            if (file != null) {
                String name = JOptionPane.showInputDialog(this, "Rename to:", file.getName());
                if (name != null && !name.trim().isEmpty()) {
                    File newFile = new File(file.getParentFile(), name);
                    if (file.renameTo(newFile)) {
                        refreshCurrentRoot();
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not rename.");
                    }
                }
            }
        });
        deleteItem.addActionListener(e -> {
            File file = getSelectedFile();
            if (file != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + file.getName() + "'?", "Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (deleteRecursively(file)) {
                        refreshCurrentRoot();
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not delete.");
                    }
                }
            }
        });

        // Collapse/expand logic
        collapseButton.addActionListener(e -> {
            collapsed = !collapsed;
            updateContentPanel();
            collapseButton.setText(collapsed ? "▶" : "▼");
        });

        // Open folder button action
        openFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                setRootFolder(chooser.getSelectedFile());
                if (fileOpenListener != null) {
                    // Optionally notify about folder open
                }
            }
        });

        updateContentPanel();

        folderTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
                if (node == null) return;
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    File file = (File) userObject;
                    if (file.isFile() && fileOpenListener != null) {
                        fileOpenListener.openFile(file);
                    }
                }
            }
        });

        folderTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (node.getChildCount() == 1 && node.getChildAt(0) instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getChildAt(0);
                    if (Boolean.TRUE.equals(firstChild.getUserObject())) {
                        node.removeAllChildren();
                        File dir = (File) node.getUserObject();
                        loadSubFoldersLazy(node, dir);
                        ((DefaultTreeModel) folderTree.getModel()).reload(node);
                    }
                }
            }
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {}
        });
    }

    private void updateContentPanel() {
        contentPanel.removeAll();
        if (collapsed) {
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }
        if (currentRoot == null) {
            contentPanel.add(placeholderPanel, BorderLayout.CENTER);
        } else {
            contentPanel.add(scrollPane, BorderLayout.CENTER);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private File getSelectedFile() {
        TreePath path = folderTree.getSelectionPath();
        if (path == null) return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = node.getUserObject();
        return userObject instanceof File ? (File) userObject : null;
    }

    private void refreshCurrentRoot() {
        if (currentRoot != null) {
            setRootFolder(currentRoot);
        }
    }

    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!deleteRecursively(f)) return false;
                }
            }
        }
        return file.delete();
    }

    // Lazy loading: add dummy child to indicate expandable
    private void loadSubFoldersLazy(DefaultMutableTreeNode node, File file) {
        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            for (File subFile : subFiles) {
                DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subFile);
                node.add(subNode);
                if (subFile.isDirectory() && hasSubFolders(subFile)) {
                    subNode.add(new DefaultMutableTreeNode(Boolean.TRUE)); // dummy child
                }
            }
        }
    }

    private boolean hasSubFolders(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File f : files) {
            if (f.isDirectory()) return true;
        }
        return false;
    }

    public void clearTree() {
        rootNode.removeAllChildren();
        ((DefaultTreeModel) folderTree.getModel()).reload();
        currentRoot = null;
        updateContentPanel();
    }

    public void openFolderItem(File folder) {
        if (folder != null && folder.exists() && folder.isDirectory()) {
            try {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(folder);
                } else {
                    JOptionPane.showMessageDialog(this, "Desktop is not supported on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not open folder: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid folder.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setRootFolder(File folder) {
        clearTree();
        if (folder != null && folder.exists() && folder.isDirectory()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
            rootNode.add(node);
            if (hasSubFolders(folder)) {
                node.add(new DefaultMutableTreeNode(Boolean.TRUE)); // dummy child
            }
            currentRoot = folder;
        } else {
            currentRoot = null;
        }
        ((DefaultTreeModel) folderTree.getModel()).reload();
        updateContentPanel();
    }

    // Custom renderer for VS Code-like icons and tooltips
    private class FileTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                File file = (File) userObject;
                setText(file.getName().isEmpty() ? file.getPath() : file.getName());
                setIcon(file.isDirectory() ? folderIcon : fileIcon);
                setToolTipText(file.getAbsolutePath());
            } else if (Boolean.TRUE.equals(userObject)) {
                setText("");
            }
            setBackgroundNonSelectionColor(new Color(40, 44, 52));
            setTextNonSelectionColor(Color.WHITE);
            setTextSelectionColor(Color.WHITE);
            setBackgroundSelectionColor(new Color(60, 63, 65));
            return this;
        }
    }
}

