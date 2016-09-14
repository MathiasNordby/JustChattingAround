import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mikkel on 12-09-2016.
 */
public class ActiveClient extends Thread implements Serializable {
    private static Socket socket;
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private int id;
    private String username;
    private Date connectedDate;
    private static Server connectedServer;
    private static Timer aliveTimer;

    public ActiveClient(Socket socket, int id, Server connectedServer) {
        this.id = id;
        this.socket = socket;
        this.connectedServer = connectedServer;
                try
        {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream  = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) inputStream.readObject();
            if (message.getType() == Message.JOIN){
                for (ActiveClient client: connectedServer.getClientList()){
                    if(client.getUsername().equalsIgnoreCase(message.getUsername())) {
                        outputStream.writeObject(new Message(Message.J_ERR));
                        close();
                        connectedServer.removeClient(id);
                        break;
                    }
                }
                outputStream.writeObject(new Message(Message.J_OK));
                username = message.getUsername();
                connectedServer.display(username + " connected.");
                connectedDate = new Date();
            }
        }
        catch (IOException e) {

            connectedServer.display("Error. Could not create IO streams: " + e);
            return;
        } catch (ClassNotFoundException e) {
            connectedServer.display("Error. Could not create IO streams: " + e);
            return;
        }

        aliveTimer = new Timer();
        aliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
                connectedServer.broadcast(new Message(username, Message.DATA ," disconnected by dropout"));
            }
        }, 70000);
    }

    public synchronized void run() {
        boolean connected = true;
        Message message;
        while(connected) {
            try {
                message = (Message) inputStream.readObject();
            }
            catch (IOException ex) {
                connectedServer.display(username + " Error reading Streams: " + ex);
                break;
            }
            catch(ClassNotFoundException ex2) {
                connectedServer.display(username + " Error reading Streams: " + ex2);
                break;
            }

            switch(message.getType()) {

                case Message.DATA:
                    connectedServer.broadcast(message);
                    break;
                case Message.QUIT:
                    close();
                    connected = false;
                    connectedServer.broadcast(new Message(username, Message.DATA ," disconnected by own will"));
                    connectedServer.updateActiveClientList();
                    break;
                case Message.ALVE:
                    alive();
                    connectedServer.display(message.getUsername() + ": Is alive");
                    break;
                default:
                    break;
            }
        }
        connectedServer.removeClient(id);
        close();
    }

    public void close() {
        try {
            if(outputStream != null) outputStream.close();
            if(inputStream != null) inputStream.close();
            if(socket != null) socket.close();
        }
        catch(Exception e) {
        }
    }

    public boolean writeToThisClient(Message message) {
        if(socket.isClosed()) {
            close();
            return false;
        }
        try {
            outputStream.writeObject(message);
        }
        catch(IOException e) {
            connectedServer.display("Error sending message to " + username);
            connectedServer.display(e.toString());
        }
        return true;
    }

    private void alive(){
        if(aliveTimer != null){
            aliveTimer.cancel();
            aliveTimer = new Timer();
            aliveTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    close();
                    connectedServer.broadcast(new Message(username, Message.DATA ," disconnected by dropout"));
                }
            }, 70000);
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return  "Id: " + id + " Username: " + username + "\nConnected since: " + connectedDate + "\n" + socket.getInetAddress() + "\n";
    }
}