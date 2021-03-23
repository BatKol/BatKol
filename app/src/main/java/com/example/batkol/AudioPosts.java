package com.example.batkol;

import java.util.Date;

public class AudioPosts {
    final String name, fileName, description, url,postID,userID;
    final Date date;
    public AudioPosts(String name,String fileName, String url, String description,String postID,String userID,Date date) {
        this.name = name;
        this.fileName = fileName;
        this.date = date;
        this.description = description;
        this.url = url;
        this.userID = userID;
        this.postID = postID;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getPostID() {
        return postID;
    }

    public String getUrl() {
        return url;
    }

    public String getUserID() {
        return userID;
    }
}
