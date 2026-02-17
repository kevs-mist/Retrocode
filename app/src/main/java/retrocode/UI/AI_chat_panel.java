package retrocode.UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AI_chat_panel extends JPanel {

    public AI_chat_panel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#0b0f14"));

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.decode("#121419"));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(340, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("AI Chat");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel status = new JLabel("online");
        status.setForeground(new Color(0x39ff14));
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));

        header.add(title, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JTextPane chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(Color.decode("#071014"));
        chatPane.setForeground(Color.WHITE);
        chatPane.setFont(new Font("Consolas", Font.PLAIN, 13));
        chatPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane chatScroll = new JScrollPane(chatPane);
        chatScroll.setBorder(BorderFactory.createLineBorder(new Color(0x1f2933)));
        card.add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.setOpaque(false);

        JTextField inputField = new JTextField();
        inputField.setBackground(Color.decode("#071014"));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x1f2933)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        inputField.setToolTipText("Type a message and press Enter");

        JButton clearButton = new JButton("Clear");
        clearButton.setFocusable(false);
        clearButton.setBackground(Color.decode("#2f3136"));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JButton sendButton = new JButton("Send");
        sendButton.setFocusable(false);
        sendButton.setBackground(Color.decode("#2a6ef2"));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(sendButton);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonsPanel, BorderLayout.EAST);
        card.add(inputPanel, BorderLayout.SOUTH);

        JButton collapseButton = new JButton(">");
        collapseButton.setFocusable(false);
        collapseButton.setPreferredSize(new Dimension(26, 0));
        collapseButton.setBackground(Color.decode("#1c1f23"));
        collapseButton.setForeground(Color.WHITE);
        collapseButton.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.decode("#121419"));
        wrap.add(collapseButton, BorderLayout.WEST);
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        javax.swing.text.StyledDocument doc = chatPane.getStyledDocument();
        javax.swing.text.Style userStyle = chatPane.addStyle("user", null);
        javax.swing.text.StyleConstants.setForeground(userStyle, Color.WHITE);
        javax.swing.text.StyleConstants.setBold(userStyle, true);

        javax.swing.text.Style aiStyle = chatPane.addStyle("ai", null);
        javax.swing.text.StyleConstants.setForeground(aiStyle, new Color(0x39ff14));

        collapseButton.addActionListener(e -> {
            boolean visible = card.isVisible();
            card.setVisible(!visible);
            collapseButton.setText(visible ? "<" : ">");
            revalidate();
            repaint();
        });

        Runnable sendAction = () -> {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                return;
            }

            try {
                String time = java.time.LocalTime.now().withNano(0).toString();
                doc.insertString(doc.getLength(), "You (" + time + "): ", userStyle);
                doc.insertString(doc.getLength(), text + "\n", null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            inputField.setText("");
            SwingUtilities.invokeLater(() -> chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum()));

            javax.swing.Timer timer = new javax.swing.Timer(500, ev -> {
                try {
                    String time = java.time.LocalTime.now().withNano(0).toString();
                    doc.insertString(doc.getLength(), "AI (" + time + "): ", aiStyle);
                    doc.insertString(doc.getLength(), "Let me help with: " + text + "\n", null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> chatScroll.getVerticalScrollBar().setValue(chatScroll.getVerticalScrollBar().getMaximum()));
            });
            timer.setRepeats(false);
            timer.start();
        };

        sendButton.addActionListener(e -> sendAction.run());
        inputField.addActionListener(e -> sendAction.run());
        clearButton.addActionListener(e -> chatPane.setText(""));
    }
}
