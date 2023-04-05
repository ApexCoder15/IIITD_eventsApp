package com.example.buzzup;

public class Post {
    String id, title, description, imageUrl, time, email, uName;

    public Post() {
    }

    public Post(String id, String title, String description,String imageUrl, String time, String email, String uName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.time = time;
        this.email = email;
        this.uName = uName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public String getEmail() {
        return email;
    }

    public String getuName() {
        return uName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

}
