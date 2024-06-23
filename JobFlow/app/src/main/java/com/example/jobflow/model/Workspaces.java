package com.example.jobflow.model;

import java.util.List;

public class Workspaces {
    String id,name,des,auth,img;
    List<String> uid;

    public Workspaces() {

    }

    public Workspaces(String id, String name, String des, String auth,String img,List<String> uid) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.auth = auth;
        this.uid = uid;
    }
    public Workspaces(String id, String name, String des, String auth,String img) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.auth = auth;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public List<String> getUid() {
        return uid;
    }

    public void setUid(List<String> uid) {
        this.uid = uid;
    }
}
