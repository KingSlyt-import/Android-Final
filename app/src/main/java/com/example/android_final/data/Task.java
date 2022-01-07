package com.example.android_final.data;

import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    private String importance;
    private String day;
    private String body;

    public Task(String name, String importance, String day, String body) {
        this.name = name;
        this.importance = importance;
        this.day = day;
        this.body = body;
    }

    public String getBody() {
        return body;
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
