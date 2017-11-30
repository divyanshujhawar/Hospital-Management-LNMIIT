package com.example.android.hospitalmanagementsystem;

/**
 * Created by Divyanshu on 04-10-2017.
 */

public class Notification {
    private String name;
    private String description;
    private String rollNo;
    private String fromTime;
    private String toTime;
    private String id;
    private String comment;
    private boolean status;
    private String email;

    public Notification(){

    }

    public Notification(String id, String name, String rollNo, String description,  String fromTime, String toTime, String comment, boolean status,String email) {
        this.name = name;
        this.description = description;
        this.rollNo = rollNo;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.id = id;
        this.comment = comment;
        this.status = status;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public String getId() {
        return id;
    }

    public String getComment(){ return comment; }

    public boolean getStatus(){ return status;}
}
