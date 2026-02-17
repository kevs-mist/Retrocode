package retrocode.UI;

import javax.swing.*;
import java.awt.*;

import retrocode.FileSystem.CollapsingWindowPanel;
import retrocode.Terminal.TerminalWindowPanel;

public class Mainwindow {
    private JFrame frame;

    public void showEditor() {
        if (frame != null) {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            frame.toFront();
            frame.requestFocus();
            return;
        }

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screen.width * 0.95);
        int height = (int) (screen.height * 0.9);

        frame = new JFrame("Retrocode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(900, 620));
        frame.getContentPane().setBackground(Color.decode("#0b0f14"));

        frame.setJMenuBar(new menubar().menuBar);

        CollapsingWindowPanel center = new CollapsingWindowPanel();
        TerminalWindowPanel terminal = new TerminalWindowPanel();
        terminal.setPreferredSize(new Dimension(0, 220));

        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, terminal);
        verticalSplit.setResizeWeight(0.78);
        verticalSplit.setOneTouchExpandable(true);
        verticalSplit.setDividerSize(10);
        verticalSplit.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        frame.add(verticalSplit, BorderLayout.CENTER);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> verticalSplit.setDividerLocation(0.78));
    }
}
