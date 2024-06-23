package com.example.jobflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String id;
    private String name;
    private String timeS;
    private String timeE;
    private String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    String member;
    String img;

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }



    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public String getTimeS() {
        return timeS;
    }

    public void setTimeS(String timeS) {
        this.timeS = timeS;
    }

    public String getTimeE() {
        return timeE;
    }

    public void setTimeE(String timeE) {
        this.timeE = timeE;
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

    public Project() {
    }

    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
