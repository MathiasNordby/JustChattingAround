package com.company.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by mikkel on 10-09-2016.
 */
public class Client  {

    private ObjectInputStream inputStream;      // Input from socket(read/receive)
    private ObjectOutputStream outputStream;    // Output to socket(write/send)
    private Socket socket;
    private String server, username;
    private int port;
    private ClientGUI clientGUI;


    /*
     *  Constructor
     *  server: the server address
     *  port: the port number
     *  username: the username
     */
    public Client(String server, int port, String username, ClientGUI clientGUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.clientGUI = clientGUI;
    }

    /*
     * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
            inputStream  = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(username);
        }
        // if it failed not much I can so
        catch(Exception ec) {
            display("Error connecting to server:" + ec);
            disconnect();
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        return true;
    }

    /*
     * To send a message to the console or the GUI
     */
    private void display(String msg) {
        clientGUI.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
    }

    /*
     * To send a message to the server
     */
    public void sendMessage(Message msg) {
        try {
            outputStream.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if(inputStream != null) inputStream.close();
        }
        catch(Exception e) {} // not much else I can do
        try {
            if(outputStream != null) outputStream.close();
        }
        catch(Exception e) {} // not much else I can do
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} // not much else I can do

    }

    /*
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    String msg = (String) inputStream.readObject();
                    clientGUI.append(msg);

                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    clientGUI.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
            }
        }
    }
}

