package server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mikkel on 12-09-2016.
 */
public class ActiveClient extends Thread {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private int id;
    private String user_name;
    private Date connectedDate;
    private Server connectedServer;
    private Timer aliveTimer;

    public ActiveClient(Socket socket, int id, Server connectedServer) {
        this.id = id;
        this.socket = socket;
        this.connectedServer = connectedServer;
        System.out.println("Test");

        try
        {
            System.out.println("Before");
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream  = new DataInputStream(socket.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            System.out.println("Before1");
            MessageServer message = new MessageServer(in.readLine().toString());
            System.out.println("OMGFFFF");
            if (message.getType() == MessageServer.JOIN){
                for (ActiveClient client: connectedServer.getClientList()){
                    if(client.getUsername().equalsIgnoreCase(message.getUser_name())) {
                        outputStream.writeBytes("J_ERR\n");
                        outputStream.flush();
                        close();
                        connectedServer.removeClient(id);
                        break;
                    }
                }
                outputStream.writeBytes("J_OK\n");
                outputStream.flush();
                user_name = message.getUser_name();

                //Maybe broadcast this??
                connectedServer.display(user_name + " connected.");
                connectedDate = new Date();
            }
        }
        catch (IOException e) {

            connectedServer.display("Error. Could not create IO streams: " + e);
            e.getStackTrace();
            connectedServer.removeClient(id);
            return;
        }

        aliveTimer = new Timer();
        aliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
                connectedServer.removeClient(id);
                connectedServer.broadcast(new MessageServer(user_name, " disconnected by dropout\n"));
                connectedServer.updateActiveClientList();
            }
        }, 70000);
    }

    public synchronized void run() {
        boolean connected = true;
        MessageServer message;
        while(connected) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                message = new MessageServer(in.readLine());
            }
            catch (IOException ex) {
                connectedServer.display(user_name + " Error reading Streams: " + ex);
                break;
            }

            switch(message.getType()) {

                case MessageServer.DATA:
                    connectedServer.broadcast(message);
                    break;
                case MessageServer.QUIT:
                    close();
                    connected = false;
                    connectedServer.broadcast(new MessageServer(user_name, " disconnected by own will"));
                    connectedServer.updateActiveClientList();
                    break;
                case MessageServer.ALVE:
                    alive();
                    connectedServer.display(message.getUser_name() + ": Is alive\n");
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

    public boolean writeToThisClient(MessageServer message) {
        if(socket.isClosed()) {
            close();
            return false;
        }
        try {
            if(message.getType() == MessageServer.DATA){
                outputStream.writeBytes("DATA {" + message.getUser_name() +"}: {" + message.getText() + "}\n");
                outputStream.flush();
            }
            else if (message.getType() == MessageServer.LIST){
                outputStream.writeBytes(message.getText());
                outputStream.flush();
            }

        }
        catch(IOException e) {
            connectedServer.display("Error sending message to " + user_name);
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
                    connectedServer.broadcast(new MessageServer(user_name, " disconnected by dropout\n"));
                }
            }, 70000);
        }
    }

    public String getUsername() {
        return user_name;
    }

    @Override
    public String toString() {
        return  "Id: " + id + " Username: " + user_name + "\nConnected since: " + connectedDate + "\n" + socket.getInetAddress() + "\n";
    }
}