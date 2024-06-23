package com.example.jobflow.model;

public class ChatList {
    private String id,msg,type,user,date,time;

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

    public ChatList(String id, String msg, String type, String user, String date, String time) {
        this.id = id;
        this.msg = msg;
        this.type = type;
        this.user = user;
        this.date = date;
        this.time = time;
    }
    public ChatList(String msg, String type, String user, String date, String time) {
        this.msg = msg;
        this.type = type;
        this.user = user;
        this.date = date;
        this.time = time;
    }

    ;

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
