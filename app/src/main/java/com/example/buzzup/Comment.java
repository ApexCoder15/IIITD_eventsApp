package com.example.buzzup;

public class Comment {
    String cId, comment, timestamp, uid, uName, uEmail;

    public Comment(){

    }

    public Comment(String cId, String comment, String timestamp, String uName, String uEmail) {
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uName = uName;
        this.uEmail = uEmail;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }
}
