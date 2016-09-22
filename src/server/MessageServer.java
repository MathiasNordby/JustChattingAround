package server;

/**
 * Created by Tanja on 21/09/2016.
 */
public class MessageServer {
    public static final int JOIN = 1, ALVE = 2, DATA = 3, QUIT = 4, LIST = 5, FAIL = 6;
    private int type;
    private String text, user_name;

    public MessageServer(String msg) {
        if(msg.startsWith("JOIN")){
            type = JOIN;
            String[] results = msg.split("\\}\\{");
            user_name = results[0];
        } else if (msg.startsWith("ALVE")){
            type = ALVE;
        } else if (msg.startsWith("DATA")){
            type = DATA;
            String[] results = msg.split("\\}\\{");
            user_name = results[0];
            text = results[1];
        } else if (msg.startsWith("QUIT")){
            type = QUIT;
        } else {
            type = FAIL;
        }
    }

    public MessageServer(String user_name, String text){
        type = DATA;
        this.user_name = user_name;
        this.text = text;
    }

    public MessageServer(int type, String text){
        this.type = type;
        this.text = text;
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
}
