package retrocode.Terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class TerminalWindowPanel extends JPanel {

    public TerminalWindowPanel() {
        setLayout(new BorderLayout(0, 6));
        setBackground(Color.decode("#0b0f14"));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setPreferredSize(new Dimension(300, 260));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.decode("#11161c"));
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel terminalLabel = new JLabel("Terminal");
        terminalLabel.setForeground(Color.WHITE);
        terminalLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton clearButton = new JButton("Clear");
        clearButton.setFocusable(false);
        clearButton.setBackground(Color.decode("#2f3136"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        header.add(terminalLabel, BorderLayout.WEST);
        header.add(clearButton, BorderLayout.EAST);

        JTextArea terminalArea = new JTextArea();
        terminalArea.setEditable(false);
        terminalArea.setBackground(Color.black);
        terminalArea.setForeground(new Color(0x39ff14));
        terminalArea.setText("Terminal ready...\n");
        terminalArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        terminalArea.setLineWrap(true);
        terminalArea.setWrapStyleWord(true);
        terminalArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        DefaultCaret caret = (DefaultCaret) terminalArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(terminalArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x1f2933)));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        clearButton.addActionListener(e -> terminalArea.setText("Terminal cleared.\n"));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}
