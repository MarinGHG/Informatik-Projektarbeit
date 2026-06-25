package de.couven.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import de.couven.scanner.Tokenizer;
import de.couven.parser.Parser;
import de.couven.parser.ParseResult;
import de.couven.token.Token;
import de.couven.token.TokenType;
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

                try {
                    // 3. Tokenizer erstellen und Quelltext in Tokens zerlegen
                    List<Token> tokens = new Tokenizer(quelltext).tokenize();

                    // 4. Den Zaehler in der GUI aktualisieren (nur FEHLER-Token)
                    long fehlerAnzahl = tokens.stream()
                            .filter(t -> t.type() == TokenType.FEHLER)
                            .count();
                    scanCounter.setText(String.valueOf(fehlerAnzahl));

                    // 5. Token-Nummern im Format 1->4->2 zusammenbauen
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < tokens.size(); i++) {
                        if (tokens.get(i).type().nummer() == -1) break; // EOF weglassen
                        if (sb.length() > 0) sb.append("->");
                        sb.append(tokens.get(i).type().nummer());
                    }
                    tokenAnzeige.setForeground(Color.BLACK);
                    tokenAnzeige.setText(sb.toString());
                } catch (RuntimeException ex) {
                    // Ungueltiges Zeichen o. Ae.: als ein Scan-Fehler werten
                    scanCounter.setText("1");
                    tokenAnzeige.setForeground(Color.RED);
                    tokenAnzeige.setText("Scan-Fehler: " + ex.getMessage());
                }
            }
        });
        // --- ENDE: Event-Listener ---

        // --- START: Event-Listener für den Parsen-Button ---
        parsen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Text aus dem Textfeld holen
                String quelltext = textArea.getText();

                // 2. Pruefen, ob nur der Placeholder drin steht
                if (quelltext.equals(placeholderText) || quelltext.isEmpty()) {
                    parseCounter.setText("0");
                    System.out.println("Nichts zu parsen.");
                    return;
                }

                try {
                    // 3. Quelltext scannen und anschliessend parsen
                    List<Token> tokens = new Tokenizer(quelltext).tokenize();
                    ParseResult result = new Parser(tokens).parse();

                    if (result.ok()) {
                        // Syntaktisch korrekt: kein Fehler
                        parseCounter.setText("0");
                        JOptionPane.showMessageDialog(
                                frame,
                                result.message(),
                                "Parsen erfolgreich",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // 4. Parsen fehlgeschlagen: Fehler-Popup anzeigen
                        parseCounter.setText("1");
                        String meldung = result.message();
                        if (result.errorPos() != null) {
                            meldung += "\n(" + result.errorPos() + ")";
                        }
                        JOptionPane.showMessageDialog(
                                frame,
                                meldung,
                                "Syntaxfehler",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RuntimeException ex) {
                    // Scan-Fehler (z. B. ungueltiges Zeichen) ebenfalls als Popup melden
                    parseCounter.setText("1");
                    JOptionPane.showMessageDialog(
                            frame,
                            "Scan-Fehler: " + ex.getMessage(),
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // --- ENDE: Event-Listener ---

        JScrollPane scrollPane = new JScrollPane(textArea);

        // --- Token-Dictionary (einblendbar) ---
        String[] spalten = {"Nr.", "Token", "Beispiel"};
        Object[][] zeilen = {
            { 0, "FEHLER",       "@ # $"        },
            { 1, "KLASSE",       "class"         },
            { 2, "TYP",          "int, boolean"  },
            { 3, "BLOCKAUF",     "{"             },
            { 4, "BLOCKZU",      "}"             },
            { 5, "SEMIKOLON",    ";"             },
            { 6, "KLAMMERAUF",   "("             },
            { 7, "KLAMMERZU",    ")"             },
            { 8, "ZUWEISUNGSOP", "="             },
            { 9, "PUNKTOP",      "+ - * /"       },
            {10, "STRICHOP",     "- (unär)"      },
            {11, "ZAHL",         "42, 0"         },
            {12, "NAME",         "x, myVar"      },
            {13, "VERGLOP",      "== != < >"     },
            {14, "WENN",         "if"            },
            {15, "SONST",        "else"          },
            {16, "SOLANGE",      "while"         },
            {17, "FUER",         "for"           },
        };
        DefaultTableModel tableModel = new DefaultTableModel(zeilen, spalten) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable dictTable = new JTable(tableModel);
        dictTable.setFillsViewportHeight(true);
        dictTable.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane dictScrollPane = new JScrollPane(dictTable);
        dictScrollPane.setBorder(BorderFactory.createTitledBorder("Token-Lexikon"));
        dictScrollPane.setVisible(false);

        JButton dictToggle = new JButton("Lexikon ▼");
        dictToggle.addActionListener(e -> {
            boolean sichtbar = !dictScrollPane.isVisible();
            dictScrollPane.setVisible(sichtbar);
            dictToggle.setText(sichtbar ? "Lexikon ▲" : "Lexikon ▼");
            frame.pack();
        });

        // --- Layout für die rechte Seite (Buttons + Counter) ---

        // 1. Ein Raster (Grid) für Buttons und Counter: 2 Zeilen, 2 Spalten, 10px Abstand
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGrid.add(scannen);       // Zeile 1, Spalte 1
        buttonGrid.add(scanCounter);   // Zeile 1, Spalte 2
        buttonGrid.add(parsen);        // Zeile 2, Spalte 1
        buttonGrid.add(parseCounter);  // Zeile 2, Spalte 2

        // 2. Ein Huellen-Panel, damit das GridLayout nicht in die Laenge gezogen wird.
        // Es heftet das buttonGrid einfach nach oben (NORTH).
        dictToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, dictToggle.getPreferredSize().height));
        buttonGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        dictToggle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel rightSideWrapper = new JPanel();
        rightSideWrapper.setLayout(new BoxLayout(rightSideWrapper, BoxLayout.Y_AXIS));
        rightSideWrapper.add(buttonGrid);
        rightSideWrapper.add(Box.createVerticalStrut(10));
        rightSideWrapper.add(dictToggle);

        // --- Haupt-Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new BorderLayout(15, 10));

        // Linke Seite: Eingabefeld oben, Token-Anzeige unten
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(tokenScrollPane, BorderLayout.SOUTH);

        // Elemente dem Haupt-Panel zuweisen
        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightSideWrapper, BorderLayout.EAST);
        mainPanel.add(dictScrollPane, BorderLayout.SOUTH);

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