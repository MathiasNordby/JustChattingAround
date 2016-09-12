package com.company.test;

import java.security.Timestamp;

/**
 * Created by MiMo on 07-09-2016.
 */
public class User {

    private String name;
    private String address;
    private Timestamp connected;

    public User (String name, String address, Timestamp connected){
        this.name = name;
        this.address = address;
        this.connected = connected;
    }

}
