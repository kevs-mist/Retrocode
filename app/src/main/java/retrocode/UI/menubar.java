package retrocode.UI;

import java.awt.*;
import javax.swing.*;

public class menubar {
    public JMenuBar menuBar = new JMenuBar();

    public menubar() {
        menuBar.setOpaque(true);
        menuBar.setBackground(Color.decode("#0f1316"));
        menuBar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu terminal = new JMenu("Terminal");

        file.setBackground(Color.BLACK);
        edit.setBackground(Color.BLACK);
        terminal.setBackground(Color.BLACK);
        file.setForeground(Color.WHITE);
        edit.setForeground(Color.WHITE);
        terminal.setForeground(Color.WHITE);

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(terminal);

        JMenuItem mi;
        mi = new JMenuItem("New");
        file.add(mi);
        mi = new JMenuItem("Open");
        file.add(mi);
        mi = new JMenuItem("Save");
        file.add(mi);

        mi = new JMenuItem("Undo");
        edit.add(mi);
        mi = new JMenuItem("Redo");
        edit.add(mi);
        mi = new JMenuItem("Cut");
        edit.add(mi);
        mi = new JMenuItem("Copy");
        edit.add(mi);
        mi = new JMenuItem("Paste");
        edit.add(mi);
    }
}

