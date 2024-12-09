package org.example.server.view;

import org.example.client.ServerParams;
import org.example.commons.model.MasterPersistence;
import org.example.server.controller.ClientHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginScreen {
    private final JFrame frame;
    private final JButton startServerButton;

    public LoginScreen() {
        frame = new JFrame();
        startServerButton = new JButton();
        createUIComponents();
    }

    private void createUIComponents() {
        initStartServerButton();
        initFrame();
    }

    private void initFrame() {
        frame.setTitle("Server Control Panel");
        ImageIcon logo = new ImageIcon("org/example/server/assets/lesco-pk-logo.png");
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(560, 190, 400, 200);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.add(startServerButton);
        frame.setMinimumSize(new Dimension(400, 200));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Initialize shared resources like MasterPersistence
                MasterPersistence.getInstance();
            }
        });
    }

    private void initStartServerButton() {
        startServerButton.setBounds(150, 70, 100, 30);
        startServerButton.setText("Start Server");
        startServerButton.addActionListener(e -> startServer());
    }

    private void startServer() {
        startServerButton.setEnabled(false); // Disable the button once clicked

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(ServerParams.ServerPort)) {
                JOptionPane.showMessageDialog(frame,
                        "Server started successfully on port " + ServerParams.ServerPort,
                        "Server Status", JOptionPane.INFORMATION_MESSAGE);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected to client!");
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame,
                        "Error starting server: " + e.getMessage(),
                        "Server Error", JOptionPane.ERROR_MESSAGE);
                startServerButton.setEnabled(true); // Re-enable the button if server fails to start
            }
        }).start();
    }
}
