package com.leonchai.todolists.dataModels;

import java.util.List;

public class TaskListModel {
    private String id;
    private String name;
    private List<String> userIDs;

    public TaskListModel(String id, String name, List<String> userIDs){
        this.id = id;
        this.name = name;
        this.userIDs = userIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }
}
