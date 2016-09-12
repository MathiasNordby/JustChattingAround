import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mikkel on 12-09-2016.
 */
public class Message implements Serializable {

    public static final int JOIN = 0, J_OK = 1, J_ERR = 2, DATA = 3, ALVE = 4, QUIT = 5, LIST = 6;
    private int type;
    private String message, username;
    private ArrayList<ActiveClient> clientList;

    public Message(int type) {
        this.type = type;
    }

    public Message(String username , int type) {
        this.username = username;
        this.type = type;
    }

    public Message(String username , int type, String message) {
        this.username = username;
        this.type = type;
        this.message = message;
    }

    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(int type, ArrayList<ActiveClient> clientList){
        this.clientList = clientList;
    }

    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList getclientList(){
        return clientList;
    }

}

