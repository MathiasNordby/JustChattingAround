package server;

import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mikkel on 12-09-2016.
 */
public class Server {

    private static int clientId;
    private int serverPort;
    private boolean running;
    private String serverUsername;

    public Server(){
    }

    public void start() {
        running = true;
        try
        {
            Scanner scan = new Scanner(System.in);
            display("Insert ip/server address:");
            serverAddress = scan.next();

            display("Insert server port:");
            serverPort = scan.nextInt();

            ServerSocket serverSocket = new ServerSocket(serverPort);
            display("Server up and running");
            while(running)
            {
                Socket socket = serverSocket.accept();

                if(!running)
                    break;

                ActiveClient activeClientThread = new ActiveClient(socket, ++clientId, this);
                clientList.add(activeClientThread);
                activeClientThread.start();
                updateActiveClientList();
            }
            serverSocket.close();
            for(ActiveClient activeClient : clientList){
                activeClient.close();
            }
        }
        catch(Exception e) {
            display("Error when closing the server and clients: " + e);
        }

    }

    protected void stop() {
        running = false;
        try {
            new Socket("localhost", serverPort);
        }
        catch(Exception e) {
        }
    }

    public void display(String textMessage) {
        System.out.println(textMessage);
    }

    public void broadcast(Message message) {
        for (int i = clientList.size(); --i >= 0; ) {
            ActiveClient activeClient = clientList.get(i);
            if (!activeClient.writeToThisClient(message)) {
                clientList.remove(i);
                display("Disconnected Client " + activeClient.getUsername() + " removed from list.");
            }
        }
        if(message.getType() == Message.DATA){
            display(message.getUsername() + " :" + message.getMessage());
        } else if(message.getType() == Message.LIST){
            serverGUI.listClients(message.getclientList());
        }

    }

    public void removeClient(int id) {
        for(ActiveClient activeClient : clientList){
            if(activeClient.getId() == id) {
                clientList.remove(activeClient.getId());
                updateActiveClientList();
                return;
            }
        }
    }

    public void updateActiveClientList(){
        broadcast(new Message(Message.LIST, clientList));
    }

    public ArrayList<ActiveClient> getClientList() {
        return clientList;
    }

    public String getServerUsername() {
        return serverUsername;
    }
}

