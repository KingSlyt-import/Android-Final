package com.example.android_final.data;

public class Category {
    private String title;
    private String description;
    private int color;
    private int picture;

    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Category(String title, String description, int color, int picture) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
