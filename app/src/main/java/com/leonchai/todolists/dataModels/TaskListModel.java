package com.leonchai.todolists.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TaskListModel implements Parcelable{
    private String id;
    private String name;
    private List<String> userIDs;

    public static final Parcelable.Creator<TaskListModel> CREATOR = new Parcelable.Creator<TaskListModel>(){
        @Override
        public TaskListModel createFromParcel(Parcel parcel) {
            return new TaskListModel(parcel);
        }

        @Override
        public TaskListModel[] newArray(int i) {
            return new TaskListModel[i];
        }
    };

    public TaskListModel(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        userIDs = new ArrayList<>();
        in.readStringList(this.userIDs);
    }

    public TaskListModel(String id, String name, List<String> userIDs){
        this.id = id;
        this.name = name;
        this.userIDs = userIDs;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeStringList(this.userIDs);
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
