package de.couven.gui;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.*;


public class MainWindow {
    public MainWindow(){

        JFrame frame = new JFrame();

        JButton button = new JButton("Parsen");
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);


        JPanel panel = new JPanel();
         panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
         panel.setLayout(new GridLayout(0,1));
         panel.add(button);


        panel.add(scrollPane);
        panel.add(button);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("ParsScann");
        frame.pack();
        frame.setVisible(true);
    }


}
