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


    public static void createUserTable(final String userID, final String name, String email, final FirebaseCallback callback){
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

                    HashMap<String, String> projectDetail = new HashMap<>();
                    projectDetail.put("name", "Personal");
                    projectDetail.put("users", name);
                    DB.child("users").child(userID).child("projects").setValue(projectDetail);

                    DB.child(userID).child("taskListName").setValue("Personal");

                    callback.onCallbackListName("Personal");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getList(String listID, String tableName, final FirebaseCallback callback){

        DB.child(listID).child(tableName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TaskModel newTask;
                List<TaskModel> tasklist = new ArrayList<>();

                for(DataSnapshot task : dataSnapshot.getChildren()){
                    String name = (String) task.child("name").getValue();
                    String dueDate = (String) task.child("dueDate").getValue();
                    String user = (String) task.child("user").getValue();
                    String description = (String) task.child("description").getValue();

                    newTask = new TaskModel(task.getKey(), name, dueDate, user, description);
                    tasklist.add(newTask);
                }

                callback.onCallback(tasklist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addTask(String listID, String tableName, TaskModel task){
        String taskKey;

        if(task.getId().equals("") || task.getId() == null) {
            taskKey = DB.child(listID).child(tableName).push().getKey();
        } else {
            taskKey = task.getId();
        }

        Map<String, Object> firebaseTask = new HashMap<>();
        firebaseTask.put("name", task.getName());
        firebaseTask.put("dueDate", task.getDueDate());
        firebaseTask.put("user", task.getUser());
        firebaseTask.put("description", task.getDescription());

        DB.child(listID).child(tableName).child(taskKey).updateChildren(firebaseTask);
    }

    public static void getTaskListName(String taskListID, final FirebaseCallback callback){
        DB.child(taskListID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("taskListName").getValue().toString();

                callback.onCallbackListName(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void removeTask(String listID, String tableName, TaskModel task){
        DB.child(listID).child(tableName).child(task.getId()).removeValue();
    }


    public interface FirebaseCallback{
        void onCallback(List<TaskModel> tasks);
        void onCallbackListName(String listName);
    }
}

