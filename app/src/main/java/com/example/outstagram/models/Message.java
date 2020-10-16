package com.example.outstagram.models;

import com.example.outstagram.adapters.SearchRVAdapter;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message  {

    private String userName;
    private String message;
    private String message_id;
    private @ServerTimestamp Date timestamp;

    public Message(){

    }



    public Message(String userName, String message, Date timestamp, String message_id){
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.message_id = message_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
