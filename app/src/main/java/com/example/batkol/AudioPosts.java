package com.example.batkol;

import java.util.Date;

public class AudioPosts {
    final String creator, description, url;
    final Date date1;

    public AudioPosts(String creator, Date date, String description, String url) {
        this.creator = creator;
        this.date1 = date;
        this.description = description;
        this.url = url;
    }
}
