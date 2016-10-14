package server;


/**
 * Created by Tanja + Mikkel on 21/09/2016.
 */
public class ServerMessage {
    //Skal være statisk og final fordi de kun skal ha én værdi og der må kun findes én enstans af dem, og de bliver brugt til at difinerer typen af beskeden.
    public static final int JOIN = 1, ALVE = 2, DATA = 3, QUIT = 4, LIST = 5, FAIL = 6;
    private int type, port;
    private String text, user_name, ip, inputString;


    /**
     * Parameteren på den er String, og den msg er den modtagede besked. Den nedbryder den modtagede besked
     * fra serven og laver det om til et message objekt. Gør det nemmere at håndterer
     * @param msg
     */
    public ServerMessage(String msg) {
        inputString = msg;
        if (msg != null) {

            //Alt efter hvilket msg.equals eller msg.startWith starter med, bliver den tilsvarende type sat.
            if (msg.startsWith("JOIN")) {
                type = JOIN;

                //Splitter message op ved JOIN-mellemtum, og ved komma-mellemrum, og ved kolon.
                String[] messageSplit = msg.split("JOIN\\s|,\\s|:");
                if(messageSplit.length <= 4) { //Der er 4 i alt, det første er typen JOIN altså [0]
                    user_name = messageSplit[1]; // det andet er username [1]
                    ip = messageSplit[2]; //Det tredje er IP [2]
                    try {
                        port = Integer.parseInt(messageSplit[3]); //Og det fjerde er port [3]
                    } catch (NumberFormatException e) {
                        type = FAIL;
                    }
                } else {
                    type = FAIL;
                }
                //Sætter typen til ALIVE
            } else if (msg.equals("ALVE")) {
                type = ALVE;

                //Sætter typen til DATA
            } else if (msg.startsWith("DATA")) {
                type = DATA;
                String[] messageSplit = msg.split("DATA\\s|:\\s");
                if(messageSplit.length >= 3){
                    user_name = messageSplit[1];
                    text = messageSplit[2];
                } else {
                    type = FAIL;
                }
                //Sætter typen QUIT
            } else if (msg.equals("QUIT")) {
                type = QUIT;
            } else {
                type = FAIL;
            }
        } else {
            type = FAIL;
        }

    }

    /**
     * Tager typen DATA, og tager en gælden besked fra en gælden user. Bliver brugt til at broadcaste DATA beskeder
     * @param user_name
     * @param text
     */
    public ServerMessage(String user_name, String text) {
        type = DATA;
        this.user_name = user_name;
        this.text = text;
    }

    /**
     * Bruges til at sende en type afsted og inputstring, bliver brugt til at broadcaste LIST beskeder
     * @param type
     * @param inputString
     */
    public ServerMessage(int type, String inputString) {
        this.type = type;
        this.inputString = inputString;
    }

    /**
     * get Type
     * @return retunerer type
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
     * get inputString
     * @return retunerer inputString
     */
    public String getInputString() {
        return inputString;
    }

}
