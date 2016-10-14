package server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mikkel on 12-09-2016.
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

    /**
     *
     * @param socket
     * @param id hvert active har en unikt id
     * @param connectedServer
     */
    public ActiveClient(Socket socket, int id, Server connectedServer) {
        this.id = id;
        this.socket = socket;
        this.connectedServer = connectedServer;

        //Laver input/Putputstreams
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            Boolean usernameInUse = true;
            while (usernameInUse) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                Boolean found = false;

                //Modtager et JOIN request, tjekker om username er gyldigt, hvis ikke, sender den en J_ERROR
                ServerMessage message = new ServerMessage(in.readLine().toString());
                if (message.getType() == ServerMessage.JOIN) {
                    if (!usernameVerify(message.getUser_name())) {
                        outputStream.writeBytes("J_ERR\n");
                        outputStream.flush();
                    } else {
                        //Kigger igennem alle de aktive clients, og tjekker om der er nogle med samme navn, hvis der, sender den også en J_ERROR
                        for (ActiveClient client : connectedServer.getClientList()) {
                            if (client.getUsername().equalsIgnoreCase(message.getUser_name())) {
                                outputStream.writeBytes("J_ERR\n");
                                outputStream.flush();
                                found = true;
                            }
                        }
                        // Hvis ikke, og der er ingen med det samme bruger navn, bliver brugeren accepteret og den får et J_OK tilbage
                        // Nu sender første gang alive
                        if (!found) {
                            user_name = message.getUser_name();
                            outputStream.writeBytes("J_OK\n");
                            outputStream.flush();
                            connectedServer.display(user_name + " connected.");
                            connectedDate = new Date();
                            alive();
                            usernameInUse = false;
                        }
                    }
                //Hvis det ik er et JOIN
                } else {
                    connectedServer.display("Received invalid JOIN message");
                    close();
                }

            }

        } catch (IOException e) {

            //
            connectedServer.display("Error. Could not create IO streams: " + e);
            e.getStackTrace();
            close();
            return;
        }

    }

    /**
     * Run metode til at starte tråden
     */
    //synchroniced (synkroniserer threads, så de ikke står og laver problemer for hinanden.)
    public synchronized void run() {
        boolean connected = true;
        ServerMessage message;
        while (connected) {
            try {
                //Laver en ny tråd der lytter til clientforbindelsen, på den næste modtagede besked
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                message = new ServerMessage(in.readLine());
            } catch (IOException ex) {
                connectedServer.display(user_name + " Error reading Streams: " + ex);
                break;
            }

            switch (message.getType()) {

                // Det er når vi får en bruger besked
                case ServerMessage.DATA:
                    if (dataVerify(message.getText())) {
                        connectedServer.broadcast(message);
                    } else {
                        writeToThisClient(new ServerMessage("Admin", "Insert valid data text. Rules: 1-250 chars long, only chars, digits, ‘-‘ and ‘_’ allowed"));
                    }
                    break;
                //Når brugeren vælger selv at exit
                case ServerMessage.QUIT:
                    close();
                    connected = false;
                    connectedServer.broadcast(new ServerMessage(user_name, " disconnected by own will"));
                    connectedServer.updateActiveClientList();
                    if(aliveTimer != null){
                        aliveTimer.cancel();
                    }
                    break;
                //Det heartbeat man får ved 60 sekund
                case ServerMessage.ALVE:
                    alive();
                    connectedServer.display("<" + user_name + "> Is alive");
                    break;
                default:
                    break;
            }
        }

        close();
    }


    /**
     * Lukker outpusStream, InputStrem og socket. Til sidst fjerner den clienten fra serveren
     */
    public void close() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
            connectedServer.removeClient(id);
        } catch (Exception e) {
        }
    }

    /**
     * Den får en message ind, og skriver tilbage til clienten
     * @param message
     * @return
     */
    public boolean writeToThisClient(ServerMessage message) {
        //Hvis socket er lukket, retunere den false
        if (socket.isClosed()) {
            close();
            return false;

        }
        try {
            //kigger på hvilken message type.
            if (message.getType() == ServerMessage.DATA) {
                //kigger på om brugeren hedder det samme som den client den sender til, hvis ikke, sender den brugernavnet + beskedens indhold
                if (!message.getUser_name().equals(user_name)) {
                    String temp = "DATA " + message.getUser_name() + ": " + message.getText() + "\n";
                    outputStream.writeBytes(temp);
                    outputStream.flush();
                } else {
                    //Hvis det er det samme navn, ændre den navnet til Me, og sender Me + beskedens indhold
                    String temp = "DATA " + "Me: " + message.getText() + "\n";
                    outputStream.writeBytes(temp);
                    outputStream.flush();
                }
                //Hvis typen er LIST, sender den listen afsted mod output
            } else if (message.getType() == ServerMessage.LIST) {
                outputStream.writeBytes(message.getInputString());
                outputStream.flush();
            }


        } catch (IOException e) {
            connectedServer.display("Error sending message to " + user_name);
            connectedServer.display(e.toString());
        }
        return true;
    }

    //Denne timer står for at smide folk ud. Efter 70 sekunder smider den folk ud
    private void alive() {
        if(aliveTimer != null){
            aliveTimer.cancel();
        }
        //Hvis den er alive, laver den bare en ny timer
        aliveTimer = new Timer();
        aliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                close();
                connectedServer.broadcast(new ServerMessage(user_name, " disconnected by dropout\n"));
            }
        }, 70000);
    }

    /**
     * Kigger på en den har et gyldigt username
     * @param username Username bliver tjekket
     * @return retunerer om den er gyldig eller ej
     */
    public boolean usernameVerify(String username) {
        /**
         * ^ : start of string
         * [ : beginning of character group
         * a-z : any lowercase letter
         * A-Z : any uppercase letter
         * 0-9 : any digit
         * _ : underscore
         * ] : end of character group
         * {x,y] : x min length, y max length
         * $ : end of string
         */
        //Det her mømster ser på om den indeholder store og små bogstaver A-Z,0-9,_-. Samt at det må være mellem 1-12 karakterer
        String pattern = "^[a-zA-Z0-9_-]{1,12}$"; // Vi ønsker at man min skal have 1 char som brugernavn.
        return username.matches(pattern);
    }

    /**
     * Metoden kigger på om det få et gyldigt input i data
     * @param data data der skal tjekkes
     * @return retunerer om den er gyldig elelr ej
     */
    public boolean dataVerify(String data) {
        //See @usernameVerify for regex explain
        String pattern = "^[a-zA-Z0-9_ -.,:=+/()!?@]{1,250}$";
        return data.matches(pattern);
    }

    /**
     * get username
     * @return retunerer user_name
     */
    public String getUsername() {
        return user_name;
    }

    @Override
    /**
     * toString metode med ID Username og tid man er connected
     */
    public String toString() {
        return "Id: " + id + " Username: " + user_name + " Connected since: " + connectedDate;
    }
}