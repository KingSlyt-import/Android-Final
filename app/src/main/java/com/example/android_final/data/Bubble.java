package com.example.android_final.data;

import java.io.Serializable;

public class Bubble implements Serializable {
    private int icon;
    private String name;
    private String time;
    private String document;

    public Bubble(int icon, String name, String time, String document) {
        this.icon = icon;
        this.name = name;
        this.time = time;
        this.document = document;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
