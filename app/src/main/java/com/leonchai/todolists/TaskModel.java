package com.leonchai.todolists;

public class TaskModel {
    private String name;
    private String dueDate;
    private String user;
    private String id;
    private String description;

    public TaskModel(String name, String dueDate, String description){
        this.id = "";
        this.name = name;
        this.dueDate = dueDate;
        this.description = description;
        user = "";


    }

    /*
    public TaskModel(String id, String name, String dueDate){
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        user = "";

    }
    */

    // Use when moving to doing or done fragment
    public TaskModel(String id, String name, String dueDate, String user){
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.user = user;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if(name != null || !name.equals("")){
            this.name = name;
        }
    }

    public String getDueDate(){
        return dueDate;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getId(){
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
