package client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mathias + Mikkel on 21-09-2016.
 */
public class ClientMessage {
    //Skal være statisk og final fordi de kun skal ha én værdi og der må kun findes én enstans af dem, og de bliver brugt til at difinerer typen af beskeden.
    public static final int J_OK = 1, J_ERR = 2, DATA = 3, LIST = 4, FAIL = 5;
    private int type;
    private String text, user_name;
    private String[] userlist;

    /**
     * Parameteren på den er String, og den msg er den modtagede besked. Den nedbryder den modtagede besked
     * fra client og laver det om til et message objekt. Gør det nemmere at håndterer
     * @param msg
     */
    public ClientMessage(String msg) {
        if (msg != null) {
            type = FAIL;

            //Alt efter hvilket msg.equals starter med, bliver den tilsvarende type sat.
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

                //Først splittes den modtagede message op i en Liste, for at få en String af alle Users
                String[] messageSplit = msg.split("LIST\\s",2);
                if(messageSplit.length >= 2){
                    //Den splitter den modtaget string (Den der indeholder alle brugernavne) op.
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

    /**
     * Get Type
     * @return den retunerer type
     */
    public int getType() {
        return type;
    }

    /**
     * get text
     * @return retunerer text
     */
    public String getText() {
        return text;
    }

    /**
     * get username
     * @return retunerer user_name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * get userlist
     * @return retunerer userlist
     */
    public String[] getUserlist() {
        return userlist;
    }

}
