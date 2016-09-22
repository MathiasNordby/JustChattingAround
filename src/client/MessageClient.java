package client;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Mathias on 21-09-2016.
 */
public class MessageClient {
    public static final int J_OK = 1, J_ERR = 2, DATA = 3, LIST = 4, FAIL = 5;
    private int type;
    private String text, user_name;
    private String[] userlist;

    public MessageClient(String msg) {
        if(msg.startsWith("J_OK")){
            type = J_OK;
        } else if (msg.startsWith("J_ERR")){
            type = J_ERR;
        } else if (msg.startsWith("DATA")){
            type = DATA;
            String[] results = msg.split("\\}\\{");
            user_name = results[0];
            text = results[1];
        } else if (msg.startsWith("LIST")){
            type = LIST;
            String[] results = msg.split("\\}\\{");
            userlist = results[0].split("\\\\s+");
        } else {
            type = FAIL;
        }
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

    public String[] getUserlist() {
        return userlist;
    }
}
