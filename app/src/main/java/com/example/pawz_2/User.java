package com.example.pawz_2;

import com.google.firebase.database.ServerValue;

public class User {
    public String displayname;
    public String email;
    public Object timestamp;
    public User(String displayname, String email){
        this.displayname = displayname;
        this.email = email;
        this.timestamp = ServerValue.TIMESTAMP;
    }
    public Object getTimestamp(){ return timestamp; }
    public User(){

    }
}
