package server;

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
    private ArrayList<ActiveClient> clientList;

    public Server(){

    }

    public void start() {
        running = true;
        clientList = new ArrayList<>();
        try
        {
            Scanner scan = new Scanner(System.in);

            display("Insert server port:");
            serverPort = scan.nextInt();

            ServerSocket serverSocket = new ServerSocket(serverPort);
            display("Server up and running");
            while(running)
            {
                Socket socket = serverSocket.accept();

                ActiveClient activeClientThread = new ActiveClient(socket, ++clientId, this);
                clientList.add(activeClientThread);
                activeClientThread.start();
                Thread.sleep(100);
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

    public void broadcast(MessageServer message) {
        for (int i = clientList.size(); --i >= 0; ) {
            ActiveClient activeClient = clientList.get(i);
            System.out.println("Broadcasting to: " + activeClient.getUsername());
            if (!activeClient.writeToThisClient(message)) {
                clientList.remove(i);
                display("Disconnected Client " + activeClient.getUsername() + " removed from list.");
            }
        }
        if(message.getType() == MessageServer.DATA){
            display(message.getUser_name() + ": " + message.getText());
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
        String list = "LIST {";
        for (ActiveClient client: clientList){
            list = list + client.getUsername() + " ";
        }
        list = list + "}\n";
        broadcast(new MessageServer(MessageServer.LIST, list));
    }

    public ArrayList<ActiveClient> getClientList() {
        return clientList;
    }

    public static void main(String[] args) {
        new Server().start();
    }
}

