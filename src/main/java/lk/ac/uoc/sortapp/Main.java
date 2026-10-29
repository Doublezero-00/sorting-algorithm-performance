package lk.ac.uoc.sortapp;

import lk.ac.uoc.sortapp.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Starts the Swing GUI on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}