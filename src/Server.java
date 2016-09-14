import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by mikkel on 12-09-2016.
 */
public class Server {

    private static int clientId;
    private ArrayList<ActiveClient> clientList;
    private int serverPort;
    private boolean running;
    private String serverUsername;
    private ServerGUI serverGUI;

    public Server(int serverPort, ServerGUI serverGUI, String serverUsername){
        this.serverPort = serverPort;
        clientList = new ArrayList<>();
        this.serverUsername = serverUsername;
        this.serverGUI = serverGUI;
        this.serverGUI = serverGUI;
    }

    public void start() {
        running = true;
        try
        {
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
                System.out.println("Now we are here");
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
        serverGUI.writeTextToGUI(textMessage);
    }

    public void broadcast(Message message) {
        for (int i = clientList.size(); --i >= 0; ) {
            ActiveClient activeClient = clientList.get(i);
            System.out.println("Send to client: " + i);
            if (!activeClient.writeToThisClient(message)) {
                clientList.remove(i);
                display("Disconnected Client " + activeClient.getUsername() + " removed from list.");
            }
        }
        display(message.getUsername() + " :" + message.getMessage());
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
        System.out.println("This is called");
        broadcast(new Message(Message.LIST, clientList));
        System.out.println(Message.LIST);
    }

    public ArrayList<ActiveClient> getClientList() {
        return clientList;
    }

    public String getServerUsername() {
        return serverUsername;
    }
}
