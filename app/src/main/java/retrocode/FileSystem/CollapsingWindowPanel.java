package retrocode.FileSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import retrocode.UI.AI_chat_panel;

import java.awt.*;

public class CollapsingWindowPanel extends JPanel {

    public CollapsingWindowPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.decode("#0b0f14"));

        JPanel explorerPanel = new JPanel(new BorderLayout(8, 8));
        explorerPanel.setBackground(Color.decode("#121419"));
        explorerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        explorerPanel.setPreferredSize(new Dimension(260, 0));

        JPanel explorerHeader = new JPanel(new BorderLayout());
        explorerHeader.setOpaque(false);
        JLabel explorerLabel = new JLabel("Explorer");
        explorerLabel.setForeground(Color.WHITE);
        explorerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel projectLabel = new JLabel("workspace");
        projectLabel.setForeground(new Color(0x8b949e));
        projectLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        explorerHeader.add(explorerLabel, BorderLayout.WEST);
        explorerHeader.add(projectLabel, BorderLayout.EAST);
        explorerPanel.add(explorerHeader, BorderLayout.NORTH);

        DefaultListModel<String> items = new DefaultListModel<>();
        items.addElement("project/");
        items.addElement("src/");
        items.addElement("README.md");

        JList<String> list = new JList<>(items);
        list.setBackground(Color.decode("#0f1316"));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(0x203247));
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(24);
        list.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane explorerScroll = new JScrollPane(list);
        explorerScroll.setBorder(BorderFactory.createLineBorder(new Color(0x1f2933)));
        explorerPanel.add(explorerScroll, BorderLayout.CENTER);

        JButton collapseButton = new JButton("<");
        collapseButton.setFocusable(false);
        collapseButton.setPreferredSize(new Dimension(28, 0));
        collapseButton.setBackground(Color.decode("#1c1f23"));
        collapseButton.setForeground(Color.WHITE);
        collapseButton.setBorder(BorderFactory.createEmptyBorder());

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBackground(Color.decode("#121419"));
        leftContainer.add(explorerPanel, BorderLayout.CENTER);
        leftContainer.add(collapseButton, BorderLayout.EAST);

        JPanel editorPanel = new JPanel(new BorderLayout(0, 8));
        editorPanel.setBackground(Color.decode("#0b0f14"));

        JPanel editorHeader = new JPanel(new BorderLayout());
        editorHeader.setBackground(Color.decode("#11161c"));
        editorHeader.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel editorTitle = new JLabel("Editor");
        editorTitle.setForeground(Color.WHITE);
        editorTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel editorFile = new JLabel("main.java");
        editorFile.setForeground(new Color(0x8b949e));
        editorFile.setFont(new Font("SansSerif", Font.PLAIN, 11));
        editorHeader.add(editorTitle, BorderLayout.WEST);
        editorHeader.add(editorFile, BorderLayout.EAST);

        JTextArea editorArea = new JTextArea();
        editorArea.setBackground(Color.decode("#071014"));
        editorArea.setForeground(Color.WHITE);
        editorArea.setCaretColor(Color.WHITE);
        editorArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        editorArea.setText("// Start coding...\n");

        JScrollPane editorScroll = new JScrollPane(editorArea);
        editorScroll.setBorder(BorderFactory.createLineBorder(new Color(0x1f2933)));

        editorPanel.add(editorHeader, BorderLayout.NORTH);
        editorPanel.add(editorScroll, BorderLayout.CENTER);

        AI_chat_panel aiPanel = new AI_chat_panel();

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, aiPanel);
        rightSplit.setResizeWeight(0.73);
        rightSplit.setOneTouchExpandable(true);
        rightSplit.setDividerSize(8);
        rightSplit.setBorder(BorderFactory.createEmptyBorder());

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightSplit);
        mainSplit.setResizeWeight(0.16);
        mainSplit.setOneTouchExpandable(true);
        mainSplit.setDividerSize(8);
        mainSplit.setBorder(BorderFactory.createEmptyBorder());

        add(mainSplit, BorderLayout.CENTER);

        collapseButton.addActionListener(e -> {
            boolean visible = explorerPanel.isVisible();
            explorerPanel.setVisible(!visible);
            collapseButton.setText(visible ? ">" : "<");
            revalidate();
            repaint();
        });
    }
}
