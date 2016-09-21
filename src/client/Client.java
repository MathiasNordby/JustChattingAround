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
    private String serverAddress, username;
    private int serverPort;
    private boolean connected;

    public Client (){

    }

    public void start(){
        try {
            Scanner scan = new Scanner(System.in);
            display("Insert ip/server address:");
            serverAddress = scan.next();

            display("Insert server port:");
            serverPort = scan.nextInt();

            socket = new Socket(serverAddress, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream  = new DataInputStream(socket.getInputStream());

            Boolean usernameInUse = true;
            while (usernameInUse){
                display("Insert username:");
                username = scan.nextLine();
                //Write join here with username
                outputStream.writeBytes("");
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                //change if to check if username is available
                //If available
                if(in.ready()){
                    usernameInUse = false;
                }
                //If taken
                else if (in.ready()){
                    display("Username " + username + " already in use");
                }
                //If none of above apply then
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
                //Check message if data(text) then print
                System.out.println("");
            }
        }
        );
        serverListener.start();

        Thread heartBeat = new Thread(()-> {
            while(connected) {
                try {
                    Thread.sleep(60000);
                    //Insert heart beat message here
                    outputStream.writeBytes("");
                }
                catch(IOException e) {
                    display("Server timed out1: " + e);
                    disconnect();
                    break;
                } catch (InterruptedException e) {
                    display("Server timed out2: " + e);
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

                    if (scan.nextLine() == "#LIST"){

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
