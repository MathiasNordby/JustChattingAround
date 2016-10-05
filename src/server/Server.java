package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mikkel on 12-09-2016.
 */
public class Server {

    private static int clientId;
    private int serverPort;
    private boolean running;
    private ArrayList<ActiveClient> clientList;

    public Server() {

    }
    //Starter server
    public void start() {
        running = true;
        clientList = new ArrayList<>();
        try {
            Scanner scan = new Scanner(System.in);

            //Tjekker at indtastet ServerPort er gyldig, og godkender hvis den er.
            Boolean verfiyInput = false;
            while (!verfiyInput) {
                display("Insert server port:");
                serverPort = scan.nextInt();
                if (portVerify(serverPort)) {
                    verfiyInput = true;
                }
            }

            //Imens serveren kører, ServerSocket accepteres, Portnummer fastlagt for denne server
            ServerSocket serverSocket = new ServerSocket(serverPort);
            display("Server up and running");
            while (running) {
                Socket socket = serverSocket.accept();

                //ActiveClientThread bliver tilføjet til clientList. ActiveClientThread startes, og listen opdateres
                ActiveClient activeClientThread = new ActiveClient(socket, ++clientId, this);
                clientList.add(activeClientThread);
                activeClientThread.start();
                Thread.sleep(100);
                updateActiveClientList();
            }
            serverSocket.close();
            //Hvis der opstår en fejl, kører Activeclient igennem og lukker listen
            for (ActiveClient activeClient : clientList) {
                activeClient.close();
            }
        } catch (Exception e) {
            display("Error when closing the server and clients: " + e);
        }

    }
    //Stopper serveren, gør klar så man kan oprette en ny / anden server
    protected void stop() {
        running = false;
        try {
            new Socket("localhost", serverPort);
        } catch (Exception e) {
        }
    }
    //Printer textMessage
    public void display(String textMessage) {
        System.out.println(textMessage);
    }

    //Tjekker om der er en client der ikke er aktiv, og fjerner den fra ActiveClients listen.
    //Hvis client ikke er active: Besked om at den gældende client er disconnectet
    public void broadcast(MessageServer message) {
        for (int i = clientList.size(); --i >= 0; ) {
            ActiveClient activeClient = clientList.get(i);
            if (!activeClient.writeToThisClient(message)) {
                clientList.remove(i);
                display("Disconnected Client " + activeClient.getUsername() + " removed from list.");
            }
        }
        //Hvis de to nederstående er lig hinanden, viser den usernavnet og deres message i consolen
        if (message.getType() == MessageServer.DATA) {
            display(message.getUser_name() + ": " + message.getText());
        }
    }
    //Fjerner ikke aktive clients ud fra deres id.
    public void removeClient(int id) {
        for (ActiveClient activeClient : clientList) {
            if (activeClient.getId() == id) {
                clientList.remove(activeClient.getId());
                updateActiveClientList();
                return;
            }
        }
    }
    //Laver en String med ActiveClients, med deres username
    public void updateActiveClientList() {
        String list = "LIST ";
        for (ActiveClient client : clientList) {
            list = list + client.getUsername() + " ";
        }
        list = list + "\n";
        //Broadcaster List, til LIST i MessageServer
        broadcast(new MessageServer(MessageServer.LIST, list));
    }
    //Tjekker at port nummer er gyldigt
    public boolean portVerify(int port) {
        if (!(port <= 65535 && port >= 0)) {
            display("Insert valid port number. Note only numbers allowed, between 0-65535.");
            return false;
        } else {
            return true;
        }
    }
    // Retunerer clientList
    public ArrayList<ActiveClient> getClientList() {
        return clientList;
    }

    public static void main(String[] args) {
        new Server().start();
    }
}

