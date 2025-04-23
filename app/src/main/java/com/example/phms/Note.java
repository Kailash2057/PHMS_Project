package com.example.phms;

public class Note {
    private int id;
    private String title;
    private String description;
    private String username;
    private String type;
    private String createdAt;
    private String updatedAt;
    private boolean selected;

    public Note(int id, String title, String description, String username, String type, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }
}