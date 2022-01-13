package com.example.android_final.data;

import java.io.Serializable;

public class Alarm implements Serializable {
    private String time, randomID, document, rung, checked;

    public Alarm(String time, String randomID, String document, String rung, String checked) {
        this.time = time;
        this.randomID = randomID;
        this.document = document;
        this.rung = rung;
        this.checked = checked;
    }

    public String getRung() {
        return rung;
    }

    public void setRung(String rung) {
        this.rung = rung;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRandomID() {
        return randomID;
    }

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
