package com.leonchai.todolists;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDB {
    private static final DatabaseReference DB = FirebaseDatabase.getInstance().getReference();


    public static void createUserTable(final String userID, String name, String email){
        // CHECK FOR EXIST USER
        final String userEmail = email;
        final String userName = name;

        DB.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userID)){
                    HashMap<String, String> userDetail = new HashMap<>();
                    userDetail.put("email", userEmail);
                    userDetail.put("name", userName);

                    DB.child("users").child(userID).setValue(userDetail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getList(String userID, String tableName, final FirebaseCallback callback){

        DB.child(userID).child(tableName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TaskModel newTask;
                List<TaskModel> tasklist = new ArrayList<>();

                for(DataSnapshot task : dataSnapshot.getChildren()){
                    String name = (String) task.child("name").getValue();
                    String dueDate = (String) task.child("dueDate").getValue();
                    newTask = new TaskModel(task.getKey(), name, dueDate);
                    tasklist.add(newTask);
                }

                callback.onCallback(tasklist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //TODO: TEST
    public static void addTask(String userID, String tableName, TaskModel task){
        String taskKey = DB.child(userID).child(tableName).push().getKey();

        Map<String, Object> firebaseTask = new HashMap<>();
        firebaseTask.put("name", task.getName());
        firebaseTask.put("dueDate", task.getDueDate());
        firebaseTask.put("user", task.getUser());

        DB.child(userID).child(tableName).child(taskKey).updateChildren(firebaseTask);
    }


    public interface FirebaseCallback{
        void onCallback(List<TaskModel> tasks);
    }
}

