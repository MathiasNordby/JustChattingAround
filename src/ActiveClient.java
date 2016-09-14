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
            username = (String) inputStream.readObject();
            connectedServer.display(username + " connected.");
            connectedDate = new Date();

        }
        catch (IOException e) {

            connectedServer.display("Error. Could not create IO streams: " + e);
            return;
        } catch (ClassNotFoundException e) {
            connectedServer.display("Error. Could not create IO streams: " + e);
            return;
        }
        aliveTimer = new Timer();
        System.out.println("timer start" + new Date());
        aliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer exe" + new Date());
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
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    connected = false;
                    connectedServer.broadcast(new Message(username, Message.DATA ," disconnected by own will"));
                    break;
                case Message.ALVE:
                    alive();
                    connectedServer.display(message.getUsername() + ": Is alive");
                    break;
                case Message.JOIN:
                    for(ActiveClient activeClient: connectedServer.getClientList()){
                        //Run them for each name.... but then it is already kinda accepted
                    }
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
            System.out.println("OMG!" + message.getType());
            outputStream.writeObject(message);
        }
        catch(IOException e) {
            connectedServer.display("Error sending message to " + username);
            connectedServer.display(e.toString());
        }
        return true;
    }

    private void alive(){
        System.out.println("alive run" + new Date());
        if(aliveTimer != null){
            aliveTimer.cancel();
            aliveTimer = new Timer();
            System.out.println("it did!" + new Date());
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
        return  "Id: " + id + " Username: " + username + "\nConnected since: " + connectedDate + "\n";
    }
}