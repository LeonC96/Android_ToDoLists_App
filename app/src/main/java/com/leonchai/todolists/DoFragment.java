package com.leonchai.todolists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.List;

public class DoFragment extends Fragment {

    private ListView doListView;

    private TaskAdapter taskAdapter;
    private SwipeActionAdapter swipeAdapter;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ArrayList<TaskModel> tasksList = new ArrayList<>();
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

        System.out.println("Do onCreateView");

        doListView = (ListView) view.findViewById(R.id.doListView);

        taskAdapter = new TaskAdapter(getActivity(),tasksList);

        swipeAdapter = new SwipeActionAdapter(taskAdapter);
        swipeAdapter.setListView(doListView);

        doListView.setAdapter(swipeAdapter);

        // Add Left swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.delete_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.delete_bg);

        //Add Right swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.in_progress_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.in_progress_bg);

        // Disable short swipes incase of accidents
        swipeAdapter.setNormalSwipeFraction(1);

        // What each swipe action does
        // Swipe Right: in_progress_bg
        // Swipe Left: Delete
        swipeAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
            @Override
            public boolean hasActions(int position, SwipeDirection direction) {
                if(direction.isLeft()) return true; // Change this to false to disable left swipes
                if(direction.isRight()) return true;

                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction) {
                return false;
            }

            @Override
            public void onSwipe(int[] position, SwipeDirection[] direction) {
                for(int i = 0; i < position.length; i++) {
                    SwipeDirection currentDirection = direction[i];
                    int currentPosition = position[i];

                    switch (currentDirection) {
                        case DIRECTION_FAR_LEFT:
                            // Delete
                            tasksList.remove(currentPosition);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                        case DIRECTION_FAR_RIGHT:
                            // Move to Doing
                            Toast.makeText(getContext(), "Swipe Right", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        /*
        * First time app opens, setUserVisibleHint() runs first so getting data must be called
        * one time here.
        */
        if(!isViewShown){
            System.out.println("Called fetchData()");
            fetchData();
            System.out.println("finished fetchData()");
        }

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
        FirebaseDB.getList(user.getUid(), "doTasks", new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(List<TaskModel> tasks) {
                tasksList.addAll(tasks);
                System.out.println("Fetched data");
                swipeAdapter.notifyDataSetChanged();

            }
        });
    }
}
