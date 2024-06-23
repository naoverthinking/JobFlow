package com.example.jobflow.model;

public class MessageUser {
    private String senderName,lastMessage,status,countUnSeen;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountUnSeen() {
        return countUnSeen;
    }

    public void setCountUnSeen(String countUnSeen) {
        this.countUnSeen = countUnSeen;
    }

    public MessageUser(String senderName, String lastMessage, String status, String countUnSeen) {
        this.senderName = senderName;
        this.lastMessage = lastMessage;
        this.status = status;
        this.countUnSeen = countUnSeen;
    }
}
