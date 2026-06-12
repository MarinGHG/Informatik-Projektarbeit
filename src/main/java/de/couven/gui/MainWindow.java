package de.couven.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MainWindow {
    public MainWindow(){

        JFrame frame = new JFrame();

        // --- Buttons ---
        JButton scannen = new JButton("Scannen");
        JButton parsen = new JButton("Parsen");

        // --- Counter (JLabels) ---
        // Ich habe sie hier zur besseren Sichtbarkeit rot gefaerbt, das kannst du natuerlich aendern
        JLabel scanCounter = new JLabel("Fehler: 0");
        scanCounter.setForeground(Color.RED);

        JLabel parseCounter = new JLabel("Fehler: 0");
        parseCounter.setForeground(Color.RED);

        // --- Textfeld ---
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // --- START: Placeholder Logik ---
        String placeholderText = "Bitte hier den Quelltext einfügen...";

        textArea.setText(placeholderText);
        textArea.setForeground(Color.GRAY);

        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholderText)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setForeground(Color.GRAY);
                    textArea.setText(placeholderText);
                }
            }
        });
        // --- ENDE: Placeholder Logik ---

        JScrollPane scrollPane = new JScrollPane(textArea);

        // --- Layout für die rechte Seite (Buttons + Counter) ---

        // 1. Ein Raster (Grid) für Buttons und Counter: 2 Zeilen, 2 Spalten, 10px Abstand
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGrid.add(scannen);       // Zeile 1, Spalte 1
        buttonGrid.add(scanCounter);   // Zeile 1, Spalte 2
        buttonGrid.add(parsen);        // Zeile 2, Spalte 1
        buttonGrid.add(parseCounter);  // Zeile 2, Spalte 2

        // 2. Ein Huellen-Panel, damit das GridLayout nicht in die Laenge gezogen wird.
        // Es heftet das buttonGrid einfach nach oben (NORTH).
        JPanel rightSideWrapper = new JPanel(new BorderLayout());
        rightSideWrapper.add(buttonGrid, BorderLayout.NORTH);

        // --- Haupt-Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new BorderLayout(15, 0));

        // Elemente dem Haupt-Panel zuweisen
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Textfeld in die Mitte
        mainPanel.add(rightSideWrapper, BorderLayout.EAST); // Unser neuen rechten Bereich einfügen

        // --- Fenster-Einstellungen ---
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("ParsScann");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // --- START: Fokus-Fix ---
        // Erlaubt dem Panel, den Fokus zu bekommen
        mainPanel.setFocusable(true);
        // Setzt den Fokus auf das Panel, weg vom Textfeld
        mainPanel.requestFocusInWindow();
        // --- ENDE: Fokus-Fix ---
    }
}