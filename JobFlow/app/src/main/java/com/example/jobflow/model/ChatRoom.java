package com.example.jobflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatRoom implements Serializable {
    public String id,chatName,type,avt,lastmsg,lastuser;
    public long lasttime;

    public long getLasttime() {
        return lasttime;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public String getLastuser() {
        return lastuser;
    }

    public void setLastuser(String lastuser) {
        this.lastuser = lastuser;
    }


    List<String> idUser = new ArrayList<>();

    public List<String> getIdUser() {
        return idUser;
    }

    public void setIdUser(List<String> idUser) {
        this.idUser = idUser;
    }

    public ChatRoom() {
        this.id = null;
        this.chatName = null;
        this.type = null;
        this.avt = null;
        this.lastmsg = null;
    }

    public ChatRoom(String id, String chatName, String type, String avt, String lastmsg) {
        this.id = id;
        this.chatName = chatName;
        this.type = type;
        this.avt = avt;
        this.lastmsg = lastmsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getLastmsg() {
        return lastmsg;
    }
    public static Comparator<ChatRoom> lastTimeComparator = new Comparator<ChatRoom>() {
        @Override
        public int compare(ChatRoom room1, ChatRoom room2) {
            return Long.compare(room1.getLasttime(), room2.getLasttime());
        }
    };
    public static Comparator<ChatRoom> lastTimeComparatorDec = new Comparator<ChatRoom>() {
        @Override
        public int compare(ChatRoom room1, ChatRoom room2) {
            return Long.compare(room2.getLasttime(), room1.getLasttime());
        }
    };

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }
}
