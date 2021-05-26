package com.example.batkol;

import java.util.Date;

public class AudioPosts {
    String name, fileName, description, url,postID,userID,stt;
    float record_pitch, record_speed;
    Date date;
    public AudioPosts(String name,String fileName, String url, String description,String postID,String userID,Date date,String stt, float r_pitch, float r_speed) {
        this.name = name;
        this.fileName = fileName;
        this.date = date;
        this.description = description;
        this.url = url;
        this.userID = userID;
        this.postID = postID;
        this.stt=stt;
        this.record_pitch = r_pitch;
        this.record_speed = r_speed;
    }
    public AudioPosts(String name,String fileName, String url, String description,String postID,String userID,Date date, float r_pitch, float r_speed) {
        this.name = name;
        this.fileName = fileName;
        this.date = date;
        this.description = description;
        this.url = url;
        this.userID = userID;
        this.postID = postID;
        this.record_pitch = r_pitch;
        this.record_speed = r_speed;
    }
    public AudioPosts(){

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

    public float getRecord_pitch() { return record_pitch; }

    public float getRecord_speed() { return record_speed; }
}
