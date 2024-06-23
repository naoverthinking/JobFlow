package com.example.jobflow.model;

public class Message {
    private String senderID,content,type,timestamp;
    int unseenMessenger,pos;

    public Message(String senderID, String content, String type, String timestamp) {
        this.senderID = senderID;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

    public int getUnseenMessenger() {
        return unseenMessenger;
    }

    public void setUnseenMessenger(int unseenMessenger) {
        this.unseenMessenger = unseenMessenger;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Message() {
    }



    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
