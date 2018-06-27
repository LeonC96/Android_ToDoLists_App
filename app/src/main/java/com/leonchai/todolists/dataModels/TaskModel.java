package com.leonchai.todolists.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskModel implements Parcelable{
    private String name;
    private String dueDate;
    private String user;
    private String id;
    private String description;

    public TaskModel(String name, String dueDate, String description){
        this.id = "";
        this.name = name;
        this.dueDate = dueDate;
        user = "";
        this.description = description;
    }

    // Use when moving to doing or done fragment
    public TaskModel(String id, String name, String dueDate, String user, String description){
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        setUser(user);
        this.description = description;
    }

    public static final Parcelable.Creator<TaskModel> CREATOR = new Parcelable.Creator<TaskModel>(){
        @Override
        public TaskModel createFromParcel(Parcel parcel) {
            return new TaskModel(parcel);
        }

        @Override
        public TaskModel[] newArray(int i) {
            return new TaskModel[i];
        }
    };

    public TaskModel(Parcel in){
         name = in.readString();
         dueDate = in.readString();
         setUser(in.readString());
         id = in.readString();
         description = in.readString();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if(name != null || !name.equals("")){
            this.name = name;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(dueDate);
        parcel.writeString(user);
        parcel.writeString(id);
        parcel.writeString(description);

    }

    public String getDueDate(){
        return dueDate;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        if(user.equals("") || user == null){
            this.user = "N/A";
        } else {
            this.user = user;
        }
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
