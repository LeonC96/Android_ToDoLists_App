package com.leonchai.todolists;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {
    private static final DatabaseReference DB = FirebaseDatabase.getInstance().getReference();

    public static void createUserTable(String userID){
        //if(DB.c)
        DB.child(userID).setValue("TESTclass");
    }

}
