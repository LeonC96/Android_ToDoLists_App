package com.leonchai.todolists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DoFragment extends Fragment {

    private ListView doList;
    private TaskAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private boolean isViewShown = false;

    public DoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("onCreate");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_do, container, false);

        System.out.println("onCreateView");

        doList = (ListView) view.findViewById(R.id.doListView);

        ArrayList<TaskModel> tasks = new ArrayList<>();
        tasks.add(new TaskModel("Do this", "01/20/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do that", "06/20/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/20/29", user.getDisplayName()));
        tasks.add(new TaskModel("Do and this", "01/24/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/20/18", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/06/19", user.getDisplayName()));

        adapter = new TaskAdapter(getActivity(),tasks);
        doList.setAdapter(adapter);

        /*
        * First time app opens, setUserVisibleHint() runs first so getting data must be called
        * one time here.
        */
        if(!isViewShown){

        }

        fetchData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME");
    }

    // Used to update list every time user goes back to fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            System.out.println("VISIBLE");
            if(getView() != null){
                System.out.println("THERES A VIEW");
                isViewShown = true;
            }
        } else {
            System.out.println("NOT VISIBLE");
        }
    }

    // fetches Firebse DB data
    private void fetchData(){
        //STILL NEEDS TO WAIT .... maybe dont matter
        FirebaseDB.getList(user.getUid(), "doTasks", new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(List<TaskModel> tasks) {
                System.out.println(tasks.get(0).getName());
                System.out.println("DONE");
            }
        });
    }
}
