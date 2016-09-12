package com.company.test;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mikkel on 10-09-2016.
 */
public class Message implements Serializable {

    public static final int JOIN = 0, J_OK = 1, J_ERR = 2, DATA = 3, ALVE = 4, QUIT = 5, LIST = 6;
    private int type;
    private String message;
    private ArrayList<User> userList;

    // constructor
    public Message(int type) {
        this.type = type;
    }

    public Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public Message(int type, ArrayList<User> userList){
        this.userList = userList;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList getUserList(){
        return userList;
    }

}

