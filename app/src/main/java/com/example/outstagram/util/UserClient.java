package com.example.outstagram.util;

import com.example.outstagram.models.UserDetails;

public class UserClient {



    public static UserClient instance = null;

    private UserDetails currentUser;
    public static synchronized UserClient getInstance(){

        if(instance == null){
            instance = new UserClient();
        }
        return instance;

    }

    private UserClient(){

    }

    public UserDetails getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDetails currentUser) {
        this.currentUser = currentUser;
    }
}
