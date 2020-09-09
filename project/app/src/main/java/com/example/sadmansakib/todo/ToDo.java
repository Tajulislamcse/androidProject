package com.example.sadmansakib.todo;

public class ToDo {
    private String title, details, date, time;
    private int id;
    private int notification_state;

    //Constructor for Insert
    public ToDo(int id, String title, String details, String date, String time,int notification_state) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.time = time;
        this.id = id;
        this.notification_state=notification_state;

    }

    public ToDo() {
    }

    //Constructor for Update
    public ToDo(String title, String details, String date, String time, int notification_state) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.time = time;
        this.notification_state=notification_state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotification_state() {
        return notification_state;
    }

    public void setNotification_state(int notification_state) {
        this.notification_state = notification_state;
    }

}
