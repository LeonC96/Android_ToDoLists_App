package com.leonchai.todolists.dataModels;

public class UserModel {
    private String email;
    private String name;

    public UserModel(String email, String name){
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
