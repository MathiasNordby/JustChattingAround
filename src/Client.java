import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by mikkel on 12-09-2016.
 */
public class Client {

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String serverAddress, username;
    private int serverPort;
    private ClientGUI clientGUI;
    private boolean connected;

    public Client (String connectAddress, String username, int connectPort, ClientGUI clientGUI){
        this.serverAddress = connectAddress;
        this.username = username;
        this.serverPort = connectPort;
        this.clientGUI = clientGUI;

    }

    public boolean start(){
        try {
            socket = new Socket(serverAddress, serverPort);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream  = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject(new Message(username, Message.JOIN));

        } catch (Exception e){
            display("Unsuccessful login: " + e);
            return false;
        }

        connected = true;
        Thread serverListener = new Thread(() -> {
            while(true) {
                try {
                    Message message = (Message) inputStream.readObject();
                    switch(message.getType()) {

                        case Message.DATA:
                            display(message.getUsername()+ ": " + message.getMessage());
                            break;
                        case Message.J_ERR:
                            display("Error Username " + username + " already in use. Try something else.");
                            disconnect();
                            break;
                        case Message.J_OK:
                            display("Successful login");
                            break;
                        case Message.LIST:
                            clientGUI.listClients(message.getclientList());
                            break;
                        default:
                            break;
                    }
                }
                catch(IOException e) {
                    if(connected){
                        display("Server timed out3: " + e);
                    }
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
            }
        }
        );
        serverListener.start();

        Thread heartBeat = new Thread(()-> {
            while(true) {
                try {
                    Thread.sleep(60000);
                    outputStream.writeObject(new Message(username, Message.ALVE));
                }
                catch(IOException e) {
                    display("Server timed out1: " + e);
                    break;
                } catch (InterruptedException e) {
                    display("Server timed out2: " + e);
                    break;
                }
            }
        });

        heartBeat.start();
    return true;
    }

    public void display(String text){
        clientGUI.writeTextToGUI(text);
    }

    public void sendMessage(Message message){
        try {
            outputStream.writeObject(message);
        }
        catch(IOException ex) {
            display("Exception writing to server: " + ex);
        }
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
        }
        catch(Exception ex){
            display("Exception when disconnecting: " + ex);
        }
    }

    public String getUsername() {
        return username;
    }
}
