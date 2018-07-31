package com.leonchai.todolists;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leonchai.todolists.dataModels.TaskListModel;
import com.leonchai.todolists.dataModels.TaskModel;
import com.leonchai.todolists.dataModels.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDB {

    private static final String FIREBASE_USERS = "users";
    private static final String FIREBASE_USER_PROJECTS = "projects";
    private static final String FIREBASE_TASK_LIST_NAME = "taskListName";

    private static final DatabaseReference DB = FirebaseDatabase.getInstance().getReference();


    public static void createUserTable(final String userID, final String name, final String email){

        DB.child(FIREBASE_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userID)){
                    HashMap<String, String> userDetail = new HashMap<>();
                    userDetail.put("email", email);
                    userDetail.put("name", name);

                    // add user's detail to users table in firebase
                    DB.child(FIREBASE_USERS).child(userID).setValue(userDetail);

                    // add to-do list name and id to user's project list in user table
                    DB.child(FIREBASE_USERS).child(userID).child(FIREBASE_USER_PROJECTS).child(userID).setValue("Personal");

                    // add to-do list name to to-do list table
                    DB.child(userID).child(FIREBASE_TASK_LIST_NAME).setValue("Personal");

                    // add user to to-do list table
                    DB.child(userID).child(FIREBASE_USERS).child(userID).setValue(userDetail);

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

    //TODO: maybe add admin power
    public static String createList(FirebaseUser user, String listName){
        // Create new list in Firebase
        String listID = DB.push().getKey();
        DB.child(listID).child(FIREBASE_TASK_LIST_NAME).setValue(listName);
        DB.child(listID).child(FIREBASE_USERS).child(user.getUid()).child("email").setValue(user.getEmail());
        DB.child(listID).child(FIREBASE_USERS).child(user.getUid()).child("name").setValue(user.getDisplayName());

        // Add project list to user information in Firebase
        DB.child(FIREBASE_USERS).child(user.getUid()).child(FIREBASE_USER_PROJECTS).child(listID).setValue(listName);

        return listID;
    }

    // get all the to do lists the user has
    public static void getUserLists(String userID, final FirebaseCallback callback){
        DB.child(FIREBASE_USERS).child(userID).child(FIREBASE_USER_PROJECTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<TaskListModel> listsNames = new ArrayList<>();

                for(DataSnapshot list : dataSnapshot.getChildren()){
                    String id = list.getKey();
                    String name = list.getValue().toString();
                    //List<String> userIDs = Arrays.asList(list.child("users").getValue().toString().split(","));
                    TaskListModel taskList = new TaskListModel(id, name);
                    listsNames.add(taskList);
                }

                callback.onCallback(listsNames);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void removeTask(String listID, String tableName, TaskModel task){
        DB.child(listID).child(tableName).child(task.getId()).removeValue();
    }

    public static void deleteTaskList(String listID, String userID){
        DB.child(listID).removeValue();
        DB.child(FIREBASE_USERS).child(userID).child(FIREBASE_USER_PROJECTS).child(listID).removeValue();
    }

    // get all the users in current to-do list
    public static void getListUsers(TaskListModel currentList, final FirebaseCallback callback){
        DB.child(currentList.getId()).child(FIREBASE_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {
                List<UserModel> userModels = new ArrayList<>();
                for(DataSnapshot user : users.getChildren()){
                    String email = (String) user.child("email").getValue();
                    String name = (String) user.child("name").getValue();
                    userModels.add(new UserModel(email, name));
                }

                callback.onCallback(userModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //TODO
    public static void addUserToList(String currentUserId, String addedUserId, final String listId, final String email){
        DB.child(FIREBASE_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userInfo : dataSnapshot.getChildren()){
                    if(((String) userInfo.child("email").getValue()).equalsIgnoreCase(email) ){
                       // userInfo.child(FIREBASE_USER_PROJECTS).child(listId).
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface FirebaseCallback{
        void onCallback(Object tasks);
    }
}

