package com.example.android_final.data;

import java.io.Serializable;

public class Note implements Serializable {
    private String name;
    private String body;
    private String document;

    public Note(String name, String body, String document) {
        this.name = name;
        this.body = body;
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String time) {
        this.body = body;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
