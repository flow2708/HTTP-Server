package ru.flow.httpserver.gui;

import ru.flow.httpserver.core.HttpServer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerGUI extends JFrame {
    private final HttpServer server;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea consoleArea;
    private boolean isRunning = false;

    public ServerGUI() {
        this.server = new HttpServer();
        initUI();
        redirectSystemOut();
    }

    private void initUI() {
        setTitle("HTTP Server Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Console panel
        JPanel consolePanel = createConsolePanel();
        mainPanel.add(new JScrollPane(consolePanel), BorderLayout.CENTER);

        // Status bar
        JLabel statusLabel = new JLabel("Status: Stopped");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Button actions
        startButton.addActionListener((ActionEvent e) -> {
            new Thread(() -> {
                try {
                    isRunning = true;
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    statusLabel.setText("Status: Running on port 8080");
                    server.start();
                } catch (IOException ex) {
                    consoleArea.append("Error: " + ex.getMessage() + "\n");
                }
            }).start();
        });

        stopButton.addActionListener((ActionEvent e) -> {
            new Thread(() -> {
                server.stop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                statusLabel.setText("Status: Stopped");
            }).start();
        });

        add(mainPanel);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Modern buttons
        startButton = new JButton("Start Server");
        styleButton(startButton, new Color(76, 175, 80));

        stopButton = new JButton("Stop Server");
        styleButton(stopButton, new Color(244, 67, 54));
        stopButton.setEnabled(false);

        panel.add(startButton);
        panel.add(stopButton);

        return panel;
    }

    private JPanel createConsolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Server Console"));
        panel.setBackground(Color.WHITE);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(new Color(230, 230, 230));
        consoleArea.setCaretColor(Color.WHITE);

        // Auto-scroll
        DefaultCaret caret = (DefaultCaret) consoleArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(consoleArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                consoleArea.append(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                consoleArea.append(new String(b, off, len));
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Modern style tweaks
                UIManager.put("Button.arc", 20);
                UIManager.put("Component.arc", 20);
                UIManager.put("TextComponent.arc", 10);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
