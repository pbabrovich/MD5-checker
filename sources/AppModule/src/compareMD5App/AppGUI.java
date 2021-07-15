package compareMD5App;

import compareMD5Logic.Logic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.swing.*;


public class AppGUI {

    private JButton button1;
    private Path path;
    Map<String, String> filesInFolder;
    Logic logic;
    
    public AppGUI() {
        initComponents();
    }
    public static void main(String[] args) {
        AppGUI appGUI = new AppGUI();
    }

    private void initComponents() {
        JFrame frame = new JFrame("MD5 compare app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createUI(frame);
        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        logic = new Logic();
//        System.setProperty("java.security.policy", "file:src/app.policy");
//        System.setSecurityManager(new SecurityManager());

    }


    private void createUI(final JFrame frame){
        JPanel panel = new JPanel();
        LayoutManager layout = new BorderLayout();
        panel.setLayout(layout);



        JButton button = new JButton("Choose path");
        final JLabel label = new JLabel("Files path: ");
        JButton buttonGenerateMD5 = new JButton("Generate new list of files with MD5");
        final JLabel label2 = new JLabel("Choose path to generate list of files");
        JButton buttonCompare = new JButton("Compare file status");
        final JLabel label3 = new JLabel("Files status: ");



        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fileChooser.showOpenDialog(frame);

                if(option == JFileChooser.APPROVE_OPTION){
                    File folder = fileChooser.getSelectedFile();
                    path = Path.of(folder.getAbsolutePath());
                    label.setText("Selected: " +  path);
                }else{
                    label.setText("Open command canceled");
                }
            }
        });

        buttonGenerateMD5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Map<String, String> filesInFolder = null;
                try {
                    filesInFolder = logic.getListOfFiles(path);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                logic.saveDirectoryState(path, filesInFolder);

                assert filesInFolder != null;
                for (Map.Entry<String, String> entry: filesInFolder.entrySet()
                ) {

                    System.out.println(entry.getKey() + ":" + entry.getValue());

                }

                System.out.println("___________");
            }
        });

        buttonCompare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                File file = null;
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int option = chooser.showOpenDialog(frame);
                if(option == JFileChooser.APPROVE_OPTION){
                    file = chooser.getSelectedFile();
                    path = Path.of(file.getParent());

                }else{
                    label.setText("Open command canceled");
                }
                Map<String, String> savedMap = logic.getDirectoryStatus(file);
                Map<String, String> tempListOfFiles = null;
                try {
                    tempListOfFiles = logic.getListOfFiles(path);
                    System.out.println("Path " + path.toString());
                    for (Map.Entry<String, String> entry: tempListOfFiles.entrySet()
                    ) {
                        System.out.println(entry.getKey() + ":" + entry.getValue());

                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    System.out.println("Cant load");
                }
                assert savedMap != null;
                assert tempListOfFiles != null;
                if(logic.areEqual(tempListOfFiles, savedMap)){
                    System.out.println("Files are the same");
                    label3.setText("Files status: there no changes" );
                } else {
                    System.out.println("Files was changed");
                    label3.setText("Files status: files was changed" );
                }


            }
        });
        frame.add(panel);
        panel.setLayout(new GridLayout(3, 1));
        panel.add(button);
        panel.add(label);
        panel.add(buttonGenerateMD5);
        panel.add(label2);
        panel.add(buttonCompare);
        panel.add(label3);
    }



}