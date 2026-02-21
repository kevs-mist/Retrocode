package retrocode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class FileManager {

    private static File currentFile = null;

    public static File getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(File file) {
        currentFile = file;
    }

    public static JMenuBar buildMenu(JFrame frame, RetroTextArea editor, FolderExplorerPanel folderExplorerPanel) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem openFolderItem = new JMenuItem("Open Folder");

        newItem.addActionListener(e -> newFile(frame, editor.getTextArea()));
        openItem.addActionListener(e -> openFile(frame, editor.getTextArea()));
        saveItem.addActionListener(e -> saveFile(frame, editor.getTextArea()));
        saveAsItem.addActionListener(e -> saveFileAs(frame, editor.getTextArea()));
        openFolderItem.addActionListener(e -> openFolderItem(frame, folderExplorerPanel));

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        menuBar.add(fileMenu);
        menuBar.add(openFolderItem);

        return menuBar;
    }

    public static void newFile(JFrame frame, RSyntaxTextArea textArea) {
        int option = JOptionPane.showConfirmDialog(frame, "Do you want to save the current file before creating a new one?",
                "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);

        if (option == JOptionPane.CANCEL_OPTION) return;

        if (option == JOptionPane.YES_OPTION) {
            saveFile(frame, textArea);
        }

        textArea.setText("");
        currentFile = null;
        frame.setTitle("RetroCode - Untitled");
    }

    public static void openFile(JFrame frame, RSyntaxTextArea textArea) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Code files", "txt", "java", "py", "js", "cpp"));
        int result = chooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileReader reader = new FileReader(file)) {
                textArea.read(reader, null);
                currentFile = file;
                frame.setTitle("RetroCode - " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to open file.");
            }
        }
    }

    public static void saveFile(JFrame frame, RSyntaxTextArea textArea) {
        if (currentFile != null) {
            try (FileWriter writer = new FileWriter(currentFile)) {
                textArea.write(writer);
                frame.setTitle("RetroCode - " + currentFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save file.");
            }
        } else {
            saveFileAs(frame, textArea);
        }
    }

    public static void saveFileAs(JFrame frame, RSyntaxTextArea textArea) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Code files", "txt", "java", "py", "js", "cpp"));
        int result = chooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                textArea.write(writer);
                currentFile = file;
                frame.setTitle("RetroCode - " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to save file.");
            }
        }
    }

    public static void openFolderItem(JFrame frame, FolderExplorerPanel folderExplorerPanel) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            folderExplorerPanel.setRootFolder(folder); // update the tree to show this folder
            folderExplorerPanel.openFolderItem(folder); // open in OS as well
        }
    }
}
