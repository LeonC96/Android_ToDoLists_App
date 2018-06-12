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

public class DoneFragment extends Fragment {

    private ListView doneList;
    private TaskAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private boolean isViewShown;

    public DoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do, container, false);

        System.out.println("Done onCreateView");

        doneList = (ListView) view.findViewById(R.id.doListView);

        ArrayList<TaskModel> tasks = new ArrayList<>();
        tasks.add(new TaskModel("Do this", "01/20/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do that", "06/20/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/20/29", user.getDisplayName()));
        tasks.add(new TaskModel("Do and this", "01/24/19", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/20/18", user.getDisplayName()));
        tasks.add(new TaskModel("Do this", "01/06/19", user.getDisplayName()));

        adapter = new TaskAdapter(getActivity(),tasks);
        doneList.setAdapter(adapter);

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
        System.out.println("Done RESUME");
    }

    // Used to update list every time user goes back to fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(getView() != null){
                isViewShown = true;
            }
        } else {

        }
    }

    // fetches Firebse DB data
    private void fetchData(){
        //STILL NEEDS TO WAIT .... maybe dont matter
        FirebaseDB.getList(user.getUid(), "doTasks", new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(List<TaskModel> tasks) {
                //System.out.println(tasks.get(0).getName());
            }
        });
    }

}
