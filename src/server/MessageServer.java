package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tanja on 21/09/2016.
 */
public class MessageServer {
    public static final int JOIN = 1, ALVE = 2, DATA = 3, QUIT = 4, LIST = 5, FAIL = 6;
    private int type;
    private String text, user_name;

    public MessageServer(String msg) {
        System.out.println("In message server");
        if(msg.startsWith("JOIN")){
            System.out.println("JOIN");
            type = JOIN;
            Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
            Matcher matcher = pattern.matcher(msg);
            matcher.find();
            user_name = matcher.group(1);
        } else if (msg.startsWith("ALVE")){
            System.out.println("ALVE");
            type = ALVE;
        } else if (msg.startsWith("DATA")){
            System.out.println("DATA");
            type = DATA;
            Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
            Matcher matcher = pattern.matcher(msg);
            matcher.find();
            user_name = matcher.group(1);
            matcher.find();
            text = matcher.group(1);
        } else if (msg.startsWith("QUIT")){
            System.out.println("QUIT");
            type = QUIT;
        } else {
            System.out.println("FAIL");
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
