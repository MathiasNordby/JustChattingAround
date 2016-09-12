package com.company.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tanja on 12/09/2016.
 */
public class LoginGUI2 extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JLabel label;
    private JTextField tf;
    private JTextField tfPort;
    private JTextField tfServer;
    private JButton login;
    private boolean connected;
    private int defaultPort;
    private String defaultHost;

    public LoginGUI2(String[] args, String host, int port) {

        super("Just Chatting Around");
        defaultHost = host;
        defaultPort = port;

        JPanel loginPanelNorth = new JPanel(new GridLayout(3,1));
        JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1,3));

        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

        serverAndPort.add(new JLabel("Server address:  "));
        serverAndPort.add(tfServer);

        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        loginPanelNorth.add(serverAndPort);

        label = new JLabel("Enter username", SwingConstants.CENTER);
        loginPanelNorth.add(label);
        tf = new JTextField("Guest");
        tf.setBackground(Color.WHITE);
        loginPanelNorth.add(tf);
        add(loginPanelNorth, BorderLayout.NORTH);

        login = new JButton("Login");
        login.addActionListener(this);


        JPanel loginPanelSouth = new JPanel();
        loginPanelSouth.add(login);
        add(loginPanelSouth, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,600);
        setVisible(true);
        tf.requestFocus();
    }

    public void connectionFailed() {
        login.setEnabled(true);
        label.setText("Enter your username");
        tf.setText("Guest");

        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);

        tfServer.setEditable(false);
        tfPort.setEditable(false);

        tf.removeActionListener(this);
        connected = false;
    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        String username = tf.getText().trim();
        if(username.length() == 0)
            return;

        String server = tfServer.getText().trim();
        if(server.length() == 0)
            return;

        String portNumber = tfPort.getText().trim();
        if(portNumber.length() == 0)
            return;
        int port = 0;

        try {
            port = Integer.parseInt(portNumber);
        }
        catch(Exception en) {
            return;
        }




    }

}
