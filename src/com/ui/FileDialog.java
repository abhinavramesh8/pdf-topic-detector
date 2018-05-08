package com.ui;

import com.Main;
import com.extraction.PDFExtractor;
import com.io.DirHandler;
import com.io.FileHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created on 07/05/18.
 */
public class FileDialog extends JFrame {
    private JButton buttonBrowse;
    private JButton buttonChoose;
    private static final String defaultFile = "uploaded_docs";

    public FileDialog() throws IOException {
        super("Indexer");
        setLayout(new FlowLayout());
        buttonBrowse = new JButton("Save as");
        buttonChoose = new JButton("Open file");
        deleteFile();
        buttonBrowse.addActionListener((ActionEvent arg0) -> {
            try {
                showSaveFileDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        buttonChoose.addActionListener((ActionEvent arg0) -> {
            try {
                showOpenFileDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        getContentPane().add(buttonBrowse);
        getContentPane().add(buttonChoose);
        setSize(300, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    setVisible(false);
                    dispose();
                    Main.run();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static String getDefaultFile() {
        return defaultFile;
    }

    private void showOpenFileDialog() throws IOException {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select a pdf");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "pdf");
        jfc.addChoosableFileFilter(filter);
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            writeToFile(jfc.getSelectedFile().getCanonicalPath(), true);
        }
    }

    private void showSaveFileDialog() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "pdf");
        fileChooser.addChoosableFileFilter(filter);
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            writeToFile(fileToSave.getCanonicalPath(), true);
        }
    }

    private void deleteFile() throws IOException {
        DirHandler dir = new DirHandler(PDFExtractor.getDefaultDirPath());
        FileHandler file = dir.createFile(getDefaultFile());
        file.delete();
    }

    private void writeToFile(String s, boolean newline) throws IOException {
        DirHandler dir = new DirHandler(PDFExtractor.getDefaultDirPath());
        FileHandler file = dir.createFile(getDefaultFile());
        s = newline ? s + "\n" : s;
        file.write(s, true);
    }

    public static void display() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException, InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread swingThread = new Thread() {
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            new FileDialog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        swingThread.start();
    }
}
