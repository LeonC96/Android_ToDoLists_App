package com.leonchai.todolists.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskListModel implements Parcelable{
    private String id;
    private String name;

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
    }

    public TaskListModel(String id, String name){
        this.id = id;
        this.name = name;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
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

}
