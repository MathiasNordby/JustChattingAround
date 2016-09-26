package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mikkel on 12-09-2016.
 */
public class Client {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private String username;

    public Client() {

    }

    public void start() {
        try {
            Boolean validInput = false;
            Scanner scan = new Scanner(System.in);
            String serverAddress = "";
            while (!validInput) {
                display("Insert ip/server address:");
                serverAddress = scan.next();
                if (ipVerify(serverAddress)) {
                    validInput = true;
                }
            }

            validInput = false;
            int serverPort = 0;
            while (!validInput) {
                display("Insert server port:");
                serverPort = scan.nextInt();
                if (portVerify(serverPort)) {
                    validInput = true;
                }
            }

            socket = new Socket(serverAddress, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            Boolean usernameInUse = true;
            while (usernameInUse) {
                validInput = false;
                while (!validInput) {
                    display("Insert username (max 12 chars long, only chars, digits, ‘-‘ and ‘_’ allowed): ");
                    username = scan.next();
                    if (usernameVerify(username)) {
                        validInput = true;
                    }
                }

                display("Username: " + username);
                outputStream.writeBytes("JOIN " + username + ", " + serverAddress + ":" + serverPort + "\n");
                outputStream.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                MessageClient message = new MessageClient(in.readLine().toString());

                if (message.getType() == MessageClient.J_OK) {
                    usernameInUse = false;
                    display("Login successful, welcome to the chat " + username);
                } else if (message.getType() == MessageClient.J_ERR) {
                    display("Username " + username + " already in use, try again");
                } else {
                    display("Unknown username error");
                }
            }


            connected = true;
            Thread serverListener = new Thread(() -> {
                while (connected) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    try {

                        MessageClient message = new MessageClient(in.readLine());
                        if (message.getType() == MessageClient.DATA) {
                            display(message.getUser_name() + ": " + message.getText());
                        } else if (message.getType() == MessageClient.LIST) {
                            display("\nActive users:");
                            for (String user : message.getUserlist()) {
                                display("- " + user);
                            }
                            display("\n");
                        }
                    } catch (IOException e) {
                        if (connected) {
                            display("Lost server connection: " + e);
                            disconnect();
                        }
                    }
                }
            }
            );
            serverListener.start();

            Thread heartBeat = new Thread(() -> {
                while (connected) {
                    try {
                        Thread.sleep(55000);
                        outputStream.writeBytes("ALVE\n");
                        outputStream.flush();
                    } catch (IOException e) {
                        if (connected) {
                            display("Server timed out: " + e);
                            disconnect();
                            break;
                        }
                    } catch (InterruptedException e) {
                        if (connected) {
                            display("Server timed out: " + e);
                            disconnect();
                            break;
                        }
                    }
                }
            });

            heartBeat.start();

            Thread scannerListner = new Thread(() -> {
                while (connected) {
                    if (connected) {

                        Boolean validInputSL = false;
                        String inputText = "";
                        while (!validInputSL) {
                            inputText = scan.nextLine();
                            if (!inputText.equals("")) {
                                if (dataVerify(inputText)) {
                                    validInputSL = true;
                                } else {
                                    display("Insert valid data text. Rules: 1-250 chars long, only chars, digits, ‘-‘ and ‘_’ allowed");
                                }

                            }
                            //Can we delete stuff in console??
                        }

                        display("\b\rMe: " + inputText);
                        if (inputText.equals("#EXIT")) {
                            try {
                                outputStream.writeBytes("QUIT\n");
                                outputStream.flush();
                                disconnect();
                            } catch (IOException e) {
                                display("Error when quiting: " + e);
                            }
                        } else {
                            try {
                                outputStream.writeBytes("DATA " + username + ": " + inputText + "\n");
                                outputStream.flush();
                            } catch (IOException e) {
                                display("Error when sending message: " + e);
                            }
                        }
                    }
                }
                start();
            });

            scannerListner.start();
        } catch (Exception e) {
            display("Unsuccessful login: " + e);
            disconnect();
        }
    }

    public void display(String textMessage) {
        System.out.println(textMessage);
    }

    public void disconnect() {
        connected = false;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            display("Successfully disconnected. Restarting.");
            Thread.sleep(100);
            start();
        } catch (Exception ex) {
            display("Exception when disconnecting: " + ex);
        }
    }

    public boolean usernameVerify(String username) {
        /**
         * ^ : start of string
         * [ : beginning of character group
         * a-z : any lowercase letter
         * A-Z : any uppercase letter
         * 0-9 : any digit
         * _ : underscore
         * ] : end of character group
         * {x,y] : x min length, y max length
         * $ : end of string
         *
         */
        String pattern = "^[a-zA-Z0-9_-]{0,12}$";
        if (username.matches(pattern)) {
            return true;
        } else {
            display("Insert valid username. Max 12 chars long, only chars, digits, ‘-‘ and ‘_’ allowed");
            return false;
        }
    }

    public boolean portVerify(int port) {
        if (!(port <= 65535 && port >= 0)) {
            display("Insert valid port number. Note only numbers allowed, between 0-65535.");
            return false;
        } else {
            return true;
        }
    }

    public boolean ipVerify(String ip) {
        String ipPattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches() || ip.equals("localhost")) {
            return true;
        } else {
            display("Insert valid ip. Like 192.168.46.34 or localhost");
            return false;
        }
    }

    public boolean dataVerify(String data) {
        //See @usernameVerify for regex explain
        String pattern = "^[a-zA-Z0-9_-]{1,250}$";
        if (data.matches(pattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
