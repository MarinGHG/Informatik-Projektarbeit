package de.couven;

import de.couven.gui.MainWindow;

public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        new MainWindow();
    }
}
