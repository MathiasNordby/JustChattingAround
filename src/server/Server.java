package server;

import java.io.IOException;
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
    private ServerSocket serverSocket;

    public Server() {

    }

    /**
     * Starter server
     */
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

            //ServerSocket oprettes med tidligere indtastet portnummer
            serverSocket = new ServerSocket(serverPort);
            display("Server up and running");

            //Den laver en tråd til at lytte på consol inputtet. Bruges til at genstart serveren på consol
            Thread scannerListner = new Thread(() -> {
                while (running) {
                    String input = scan.nextLine();
                    if (input.equals("EXIT")) {
                        display("EXITING..... RESTARTING");
                        restart();
                    }
                }
            });
            scannerListner.start();


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

            restart();
        } catch (Exception e) {
            display("Error when closing the server and clients: " + e);
        }

    }

    /**
     * stopper serveren, gør klar så man kan oprette en ny / anden server
     */
    protected void restart() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    /**
     * Metoden er lavet til at man ik skal bruge Sout konstant
     * Ideen er at klargører så man kan smide beskederen afsted til en Gui, så den eneste metode der skal ændres er denne
     * @param textMessage
     */
    public void display(String textMessage) {
        System.out.println(textMessage);
    }


    /**
     * Sender beskeder ud til alle clients, hvis ikke den kan finde den client, fjerner den activeClients fra sin client list
     * @param message
     */
    public void broadcast(ServerMessage message) {
        for (int i = clientList.size(); --i >= 0; ) {
            ActiveClient activeClient = clientList.get(i);
            if (!activeClient.writeToThisClient(message)) {
                clientList.remove(i);
                display("Disconnected Client " + activeClient.getUsername() + " removed from list.");
            }
        }
        //Hvis message typen er DATA så til sidst viser den Usernavnet og den messagde de sender, bagefter, så servern også kan se hvad de skriver.
        if (message.getType() == ServerMessage.DATA) {
            display(message.getUser_name() + ": " + message.getText());
        }
    }

    /**
     * Fjerner activeClients fra client listen ud fra deres id.
     * @param id
     */
    public void removeClient(int id) {
        for (ActiveClient activeClient : clientList) {
            if (activeClient.getId() == id) {
                clientList.remove(activeClient.getId());
                updateActiveClientList();
                return;
            }
        }
    }

    /**
     * Laver en String med ActiveClients, og broadcaster dem
     */
    public void updateActiveClientList() {
        String list = "LIST ";
        for (ActiveClient client : clientList) {
            list = list + client.getUsername() + " ";
            display(client.toString());
        }
        list = list + "\n";
        //Broadcaster List, til LIST i ServerMessage
        broadcast(new ServerMessage(ServerMessage.LIST, list));
    }

    /**
     * Tjekker at port nummer er gyldigt
     * @param port tjekker portnummer
     * @return retunerer om portnummeret er gyldigt
     */
    public boolean portVerify(int port) {
        if (!(port <= 65535 && port >= 0)) {
            display("Insert valid port number. Note only numbers allowed, between 0-65535.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * get clientlist
     * @return retunerer clientlist (ArrayList)
     */
    public ArrayList<ActiveClient> getClientList() {
        return clientList;
    }

    /**
     * Det er denne metode der gør det muligt at starte javefilen
     * @param args
     */
    public static void main(String[] args) {
        new Server().start();
    }
}

