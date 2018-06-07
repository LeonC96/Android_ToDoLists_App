package com.leonchai.todolists;

public class DataModel {
    private String name;
    private String dueDate;
    private String user;

    public DataModel(String name, String dueDate){
        this.name = name;
        this.dueDate = dueDate;
        user = "";

    }

    public DataModel(String name, String dueDate, String user){
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
}
