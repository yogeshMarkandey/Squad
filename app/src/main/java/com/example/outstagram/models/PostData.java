package com.example.outstagram.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostData {

    private String username;
    private String description;
    private String postId;
    private int totalLikes;
    private int totalComments;
    private String imageUrl;
    private String postImageUrl;
    private @ServerTimestamp
    Date timestamp;

    public PostData() {
    }



    public PostData(String username, String description, String postId, String imageUrl, String postImageUrl) {
        this.username = username;
        this.description = description;
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.postImageUrl = postImageUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }
    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
