package server;


/**
 * Created by Tanja + Mikkel on 21/09/2016.
 */
public class MessageServer {
    public static final int JOIN = 1, ALVE = 2, DATA = 3, QUIT = 4, LIST = 5, FAIL = 6;
    private int type, port;
    private String text, user_name, ip, inputString;

    public MessageServer(String msg) {
        inputString = msg;
        if (msg != null) {
            if (msg.startsWith("JOIN")) {
                type = JOIN;
                String[] messageSplit = msg.split("JOIN\\s|,\\s|:");
                if(messageSplit.length <= 4) {
                    user_name = messageSplit[1];
                    ip = messageSplit[2];
                    try {
                        port = Integer.parseInt(messageSplit[3]);
                    } catch (NumberFormatException e) {
                        type = FAIL;
                    }
                } else {
                    type = FAIL;
                }
            } else if (msg.equals("ALVE")) {
                type = ALVE;
            } else if (msg.startsWith("DATA")) {
                type = DATA;
                String[] messageSplit = msg.split("DATA\\s|:\\s");
                if(messageSplit.length >= 3){
                    user_name = messageSplit[1];
                    text = messageSplit[2];
                } else {
                    type = FAIL;
                }
            } else if (msg.equals("QUIT")) {
                type = QUIT;
            } else {
                type = FAIL;
            }
        } else {
            type = FAIL;
        }

    }

    public MessageServer(String user_name, String text) {
        type = DATA;
        this.user_name = user_name;
        this.text = text;
    }

    public MessageServer(int type, String inputString) {
        this.type = type;
        this.inputString = inputString;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getInputString() {
        return inputString;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
