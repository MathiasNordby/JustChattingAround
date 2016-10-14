package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Mikkel on 12-09-2016.
 */
public class Client {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean connected;
    private String username;
    private Scanner scan;

    public Client() {

    }
    /**
     * Starter Client
     */
    public void start() {
        try {
            //Tjekker for et gyldigt IP input
            Boolean validInput = false;
            scan = new Scanner(System.in);
            String serverAddress = "";
            while (!validInput) {
                display("Insert ip/server address:");
                serverAddress = scan.next();
                if (ipVerify(serverAddress)) {
                    validInput = true;
                }
            }

            //Tjekker for en gyldig ServerPort
            validInput = false;
            int serverPort = 0;
            while (!validInput) {
                display("Insert server port:");
                serverPort = scan.nextInt();
                if (portVerify(serverPort)) {
                    validInput = true;
                }
            }

            //Socket bliver oprettet med ServerAdresse og Port. Out og inptStream sockets bliver oprettet.
            socket = new Socket(serverAddress, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            //Tjekker om username er gyldig og om det er i brug, og den skanner det valgte username, og tjekker om det kan godkendes
            Boolean usernameInUse = true;
            while (usernameInUse) {
                validInput = false;
                // --> Tjekker om brugernavn er gyldigt
                while (!validInput) {

                    display("Insert username (max 12 chars long, only chars, digits, ‘-‘ and ‘_’ allowed): ");
                    username = scan.next();
                    if (usernameVerify(username)) {
                        validInput = true;
                    }
                }
                //Viser username, og sender et JOIN request mod output socket med det valgte brugernavn
                display("Try joining with Username: " + username);
                outputStream.writeBytes("JOIN " + username + ", " + serverAddress + ":" + serverPort + "\n");
                outputStream.flush();

                //Læser fra inputstream for at få den næste besked vi får tilbage fra serveren, og laver det om til et ClientMessage objekt
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                ClientMessage message = new ClientMessage(in.readLine().toString());

                //Kigger messagen igennem for type, kigger om brugernavnet allerede er i brug eller ej
                if (message.getType() == ClientMessage.J_OK) {
                    usernameInUse = false;
                    display("Login successful, welcome to the chat " + username);
                } else if (message.getType() == ClientMessage.J_ERR) {
                    display("Username " + username + " already in use, try again");
                } else {
                    display("Unknown username error");
                }
            }

            //Nu sætter vi connected til true, fordi cienten er forbundet til serveren.
            connected = true;

            //Laver en ny tråd til at lytte til serveren
            Thread serverListener = new Thread(() -> {
                while (connected) {
                    //Her læser den fra serveren
                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    try {

                        //Næste modtaget linje læses og laves om til et ClientMessage objekt.
                        ClientMessage message = new ClientMessage(in.readLine());
                        //Kigger på om det er data type, så smiden den ud som en bruger besked
                        if (message.getType() == ClientMessage.DATA) {
                            display(message.getUser_name() + ": " + message.getText());
                            //Hvis typen er LIST så smiden den en liste ud med ActiveUsers
                        } else if (message.getType() == ClientMessage.LIST) {
                            display("\nActive users:");
                            for (String user : message.getUserlist()) {
                                display("- " + user);
                            }
                            display("");
                        }
                    } catch (IOException e) {
                        if (connected) {
                            display("Lost server connection: " + e);
                            disconnect();
                        }
                    }
                }
            }
            );
            //Tården serverListener startes
            serverListener.start();

            //Tråden sender et heartbeat ud hvert 60'ne sekund, for at vise at den stadig er aktiv. (Tråden er lavet med lambda expression)
            Thread heartBeat = new Thread(() -> {
                while (connected) {
                    try {
                        Thread.sleep(60000);
                        outputStream.writeBytes("ALVE\n");
                        outputStream.flush();
                    } catch (IOException e) {
                        if (connected) {
                            display("Server timed out: " + e);
                            disconnect();
                            break;
                        }
                    } catch (InterruptedException e) {
                        if (connected) {
                            display("Server timed out: " + e);
                            disconnect();
                            break;
                        }
                    }
                }
            });

            //Tråden heartbeat startes
            heartBeat.start();

            //Den laver en tråd til at lytte på consol inputtet.
            Thread scannerListner = new Thread(() -> {
                while (connected) {

                        //Bruges til at modtage beskeder fra brugeren, og tjekker om det er gyldigt input eller ej.
                        Boolean validInputSL = false;
                        String inputText = "";
                        while (!validInputSL) {
                            inputText = scan.nextLine();
                            if (!inputText.equals("")) {
                                if (dataVerify(inputText)) {
                                    validInputSL = true;
                                } else {
                                    display("Insert valid data text. Rules: 1-250 chars long, only chars, digits, ‘-‘ and ‘_’ allowed");
                                }

                            }

                        }
                        //Hvis inputtet er = EXIT (ALL CAPS) Så bliver man disconeccetede fra serveren
                        if (inputText.equals("EXIT")) {
                            try {
                                outputStream.writeBytes("QUIT\n");
                                outputStream.flush();
                                disconnect();
                            } catch (IOException e) {
                                display("Error when quiting: " + e);
                            }
                        } else {
                            try {
                                //Ellers sender den teksten afsted med output Stream, hvor den sender Data, bruger navn og beskeden.
                                outputStream.writeBytes("DATA " + username + ": " + inputText + "\n");
                                outputStream.flush();
                            } catch (IOException e) {
                                display("Error when sending message: " + e);
                            }
                        }
                }

                start();
            });

            scannerListner.start();
        } catch (Exception e) {
            display("Unsuccessful login: " + e);
            disconnect();
        }
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
     * Metoden er lavet til at disconnecte forbindelsen (inputstreams, outputstreams, og sockets)
     */
    public void disconnect() {
        connected = false;
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            display("Successfully disconnected. Restarting.");

        } catch (Exception ex) {
            display("Exception when disconnecting: " + ex);
        }
    }

    /**
     * Metoden kigger på om den har et gyldigt username
     * @param username username skal tjekkes
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
         *
         */
        //Det her mømster ser på om den indeholder store og små bogstaver A-Z,0-9,_-. Samt at det må være mellem 1-12 karakterer
        String pattern = "^[a-zA-Z0-9_-]{1,12}$"; // Vi ønsker at man min skal have 1 char som brugernavn.
        if (username.matches(pattern)) {
            return true;
        } else {
            display("Insert valid username. Max 12 chars long, only chars, digits, ‘-‘ and ‘_’ allowed");
            return false;
        }
    }

    /**
     *
     * Metoden kigger på om den får en gyldig port
     * @param port Portnummer der skal tjekkes
     * @return retunerer om den er gyldig eller ej
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
     * Metode kigger på om den får et gyldigt IP indput
     * @param ip IP der skal tjekkkes
     * @return retunerer om den er gyldig eller ej
     */
    public boolean ipVerify(String ip) {
        //Her laver vi et mønsker for at tjekke om den indtastede stemmer over ens med den gyldige ip, ex. 1920
        String pattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        //Her ser den på om det matcher mønseret eller localhost som String, som også er en gyldig IP
        if (ip.matches(pattern) || ip.equals("localhost")) {
            return true;
        } else {
            display("Insert valid ip. Like 192.168.46.34 or localhost");
            return false;
        }
    }

    /**
     * Den kigger bare på om data teksten er gyldig
     * @param data Data teksten der tjekkes
     * @return retunerer om den er gyldig eller ej
     */
    public boolean dataVerify(String data) {
        //See @usernameVerify for regex explain
        String pattern = "^[a-zA-Z0-9_ -.,:=+/()!?@]{1,250}$";
        if (data.matches(pattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
