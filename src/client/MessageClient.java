package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mathias on 21-09-2016.
 */
public class MessageClient {
    public static final int J_OK = 1, J_ERR = 2, DATA = 3, LIST = 4, FAIL = 5;
    private int type;
    private String text, user_name;
    private String[] userlist;

    public MessageClient(String msg) {
        if(msg != null) {
            type = FAIL;

            if (msg.equals("J_OK")) {
                type = J_OK;
            } else if (msg.equals("J_ERR")) {
                type = J_ERR;
            } else if (msg.startsWith("DATA")) {
                type = DATA;
                Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
                Matcher matcher = pattern.matcher(msg);
                matcher.find();
                user_name = matcher.group(1);
                matcher.find();
                text = matcher.group(1);
            } else if (msg.startsWith("LIST")) {
                type = LIST;
                Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
                Matcher matcher = pattern.matcher(msg);
                matcher.find();
                userlist = matcher.group(1).split("\\s+");
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
}
