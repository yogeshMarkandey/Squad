package com.example.outstagram.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListAdapter;

import java.util.List;
import java.util.Map;

public class UserDetails implements Parcelable {

    private String email;
    private String userId;
    private String profileImageUrl;
    private String name;
    private String userName;
    private List<Map<String, String>> friends_list;
    private String chatId;

    protected UserDetails(Parcel in) {
        email = in.readString();
        userId = in.readString();
        profileImageUrl = in.readString();
        name = in.readString();
        userName = in.readString();
        chatId = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    public List<Map<String, String>> getFriends_list() {
        return friends_list;
    }

    public void setFriends_list(List<Map<String, String>> friends_list) {
        this.friends_list = friends_list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserDetails(){

    }

    public UserDetails(String email, String userId) {
        this.email = email;
        this.userId = userId;
    }





    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(userId);
        parcel.writeString(profileImageUrl);
        parcel.writeString(name);
        parcel.writeString(userName);
        parcel.writeString(chatId);
    }
}
