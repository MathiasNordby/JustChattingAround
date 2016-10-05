package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mathias + Mikkel on 21-09-2016.
 */
public class MessageClient {
    public static final int J_OK = 1, J_ERR = 2, DATA = 3, LIST = 4, FAIL = 5;
    private int type;
    private String text, user_name, inputString;
    private String[] userlist;

    public MessageClient(String msg) {
        inputString = msg;
        if (msg != null) {
            type = FAIL;

            if (msg.equals("J_OK")) {
                type = J_OK;
            } else if (msg.equals("J_ERR")) {
                type = J_ERR;
            } else if (msg.startsWith("DATA")) {
                type = DATA;
                String[] messageSplit = msg.split("DATA\\s|:\\s");
                if(messageSplit.length >= 3){
                    user_name = messageSplit[1];
                    text = messageSplit[2];
                } else{
                    type = FAIL;
                }
            } else if (msg.startsWith("LIST")) {
                type = LIST;

                String[] messageSplit = msg.split("LIST\\s");
                if(messageSplit.length >= 2){
                    //Made double split because else there would be 1 empty string at [0]
                    userlist = messageSplit[1].split("\\s");
                } else {
                    type = FAIL;
                }

            } else {
                type = FAIL;
            }
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

    public String getInputString() {
        return inputString;
    }
}
