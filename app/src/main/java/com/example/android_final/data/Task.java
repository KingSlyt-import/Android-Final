package com.example.android_final.data;

import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    String importance;
    private String day;
    private String body;
    private String document;
    private boolean checked;

    public Task(String name, String importance, String day, String body, String document, boolean checked) {
        this.name = name;
        this.importance = importance;
        this.day = day;
        this.body = body;
        this.document = document;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getBody() {
        return body;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
