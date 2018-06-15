package com.leonchai.todolists;

public class TaskModel {
    private String name;
    private String dueDate;
    private String user;
    private String id;

    public TaskModel(String id, String name, String dueDate){
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        user = "";

    }

    public TaskModel(String id, String name, String dueDate, String user){
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.user = user;
    }

    public String getName(){
        return name;
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
}
