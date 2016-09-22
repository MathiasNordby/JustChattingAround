package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by mikkel on 12-09-2016.
 */
public class Client {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private String username;

    public Client (){

    }

    public void start(){
        try {
            Scanner scan = new Scanner(System.in);
            display("Insert ip/server address:");
            String serverAddress = scan.next();

            display("Insert server port:");
            int serverPort = scan.nextInt();

            socket = new Socket(serverAddress, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream  = new DataInputStream(socket.getInputStream());

            Boolean usernameInUse = true;
            while (usernameInUse){
                display("Insert username:");
                username = scan.next();

                outputStream.writeBytes("JOIN {" + username + "}, {" + serverAddress + "}:{" + serverPort +"}");
                outputStream.flush();
                System.out.println("TEST1");
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                MessageClient message = null;
                while(!in.ready()){

                    message = new MessageClient(in.readLine());
                }
                System.out.println("TEST3");
                System.out.println("TEST2" + in.readLine());


                if(message.getType() == MessageClient.J_OK){
                    usernameInUse = false;
                    display("Login successful, welcome to the chat " + username);
                }

                else if (message.getType() == MessageClient.J_ERR){
                    display("Username " + username + " already in use, try again");
                }

                else{
                    display("Unknown username error");
                }
            }

        } catch (Exception e){
            display("Unsuccessful login: " + e);
            disconnect();
        }

        connected = true;
        Thread serverListener = new Thread(() -> {
            while(connected) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    MessageClient message = new MessageClient(in.readLine());
                    if(message.getType() == MessageClient.DATA){
                        display(message.getUser_name() + ": " + message.getText());
                    } else if (message.getType() == MessageClient.LIST){
                        display(message.getText());
                    }
                } catch (IOException e) {
                    display("Could not read message: " + e);
                }
                System.out.println("");
            }
        }
        );
        serverListener.start();

        Thread heartBeat = new Thread(()-> {
            while(connected) {
                try {
                    Thread.sleep(60000);
                    outputStream.writeBytes("ALVE");
                    outputStream.flush();
                }
                catch(IOException e) {
                    display("Server timed out: " + e);
                    disconnect();
                    break;
                } catch (InterruptedException e) {
                    display("Server timed out: " + e);
                    disconnect();
                    break;
                }
            }
        });

        heartBeat.start();

        Thread scannerListner = new Thread(()-> {
            while(connected) {
                if(connected){
                    System.out.print("Insert text: ");
                    Scanner scan = new Scanner(System.in);

                    String inputText = scan.nextLine();

                    if (inputText == "#EXIT"){
                        try {
                            outputStream.writeBytes("QUIT");
                            outputStream.flush();
                        } catch (IOException e) {
                            display("Error when quiting: " + e);
                        }
                    }
                    else {
                        try {
                            outputStream.writeBytes("DATA {" + username + "}: {" + inputText + "}");
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
    }

    public void display(String textMessage) {
        System.out.println(textMessage);
    }

    public void disconnect(){
        connected = false;
        try {
            if(inputStream != null){
                inputStream.close();
            }
            if(outputStream != null){
                outputStream.close();
            }
            if(socket != null){
                socket.close();
            }
            display("Successfully disconnected");
            start();
        }
        catch(Exception ex){
            display("Exception when disconnecting: " + ex);
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
