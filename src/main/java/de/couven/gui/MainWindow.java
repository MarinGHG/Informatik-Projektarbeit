package de.couven.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import de.couven.scanner.Scanner;
import de.couven.token.Token;
import java.util.List;

public class MainWindow {
    public MainWindow(){

        JFrame frame = new JFrame();

        // --- Buttons ---
        JButton scannen = new JButton("Scannen");
        JButton parsen = new JButton("Parsen");

        // --- Counter (JLabels) ---
        // Ich habe sie hier zur besseren Sichtbarkeit rot gefaerbt, das kannst du natuerlich aendern
        JLabel scanCounter = new JLabel("0");
        scanCounter.setForeground(Color.RED);

        JLabel parseCounter = new JLabel("0");
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

        // --- Token-Nummern-Anzeige (nicht klickbar) ---
        JTextArea tokenAnzeige = new JTextArea(3, 30);
        tokenAnzeige.setLineWrap(true);
        tokenAnzeige.setWrapStyleWord(true);
        tokenAnzeige.setEditable(false);
        tokenAnzeige.setFocusable(false);
        tokenAnzeige.setForeground(Color.GRAY);
        tokenAnzeige.setText("Token-Nummern werden hier angezeigt...");
        JScrollPane tokenScrollPane = new JScrollPane(tokenAnzeige);

        // --- START: Event-Listener für den Scannen-Button ---
        scannen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Text aus dem Textfeld holen
                String quelltext = textArea.getText();

                // 2. Pruefen, ob nur der Placeholder drin steht
                if (quelltext.equals(placeholderText) || quelltext.isEmpty()) {
                    scanCounter.setText("0");
                    System.out.println("Nichts zu scannen.");
                    return; // Abbrechen, da kein echter Code da ist
                }

                // 3. Scanner-Objekt erstellen und Text uebergeben
                Scanner scanner = new Scanner(quelltext);

                // 4. Scannen ausführen
                List<Token> tokens = scanner.scan();

                // 5. Den Zaehler in der GUI aktualisieren (nur FEHLER-Token)
                long fehlerAnzahl = tokens.stream()
                        .filter(t -> t.type() == de.couven.token.TokenType.FEHLER)
                        .count();
                scanCounter.setText(String.valueOf(fehlerAnzahl));

                // 6. Token-Nummern im Format 1->4->2 zusammenbauen
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < tokens.size(); i++) {
                    if (tokens.get(i).type().nummer() == -1) break; // EOF weglassen
                    if (sb.length() > 0) sb.append("->");
                    sb.append(tokens.get(i).type().nummer());
                }
                tokenAnzeige.setForeground(Color.BLACK);
                tokenAnzeige.setText(sb.toString());


            }
        });
        // --- ENDE: Event-Listener ---

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

        // Linke Seite: Eingabefeld oben, Token-Anzeige unten
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(tokenScrollPane, BorderLayout.SOUTH);

        // Elemente dem Haupt-Panel zuweisen
        mainPanel.add(leftPanel, BorderLayout.CENTER); // Textfelder in die Mitte
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