package com.leonchai.todolists;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leonchai.todolists.dataModels.TaskModel;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.List;

public class DoneFragment extends Fragment {

    public static final String TABLE_NAME = "doneTasks";

    private ListView doneListView;

    private String taskListID;

    private TaskAdapter taskAdapter;
    private SwipeActionAdapter swipeAdapter;

    private FirebaseUser user;

    private ArrayList<TaskModel> tasksList = new ArrayList<>();

    public DoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do, container, false);

        taskListID = getArguments().getString("taskListID");

        doneListView = (ListView) view.findViewById(R.id.doListView);

        taskAdapter = new TaskAdapter(getActivity(),tasksList);

        swipeAdapter = new SwipeActionAdapter(taskAdapter);
        swipeAdapter.setListView(doneListView);

        doneListView.setAdapter(swipeAdapter);

        // Add Left swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.incomplete_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.incomplete_bg);


        //Add Right swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.delete_done_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.delete_done_bg);

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
                    TaskModel currentTask = tasksList.get(currentPosition);

                    switch (currentDirection) {
                        case DIRECTION_FAR_LEFT:
                            // Move to Done Fragment
                            // Add task to doing fragment list
                            FirebaseDB.addTask(user.getUid(), DoingFragment.TABLE_NAME,currentTask);

                            // Removes task from list and from firebase
                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(user.getUid(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                        case DIRECTION_FAR_RIGHT:
                            // Delete
                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(user.getUid(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

        doneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra("task", tasksList.get(i));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    // Used to update list every time user goes back to fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(getView() != null){
                fetchData();
            }
        }
    }

    // fetches Firebse DB data
    private void fetchData(){
        FirebaseDB.getList(taskListID, TABLE_NAME, new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(Object tasks) {
                List<TaskModel> theTasks = (List<TaskModel>) tasks;
                // Check if there is any data to fetch
                if(!theTasks.isEmpty()) {
                    tasksList.clear();
                    tasksList.addAll(theTasks);
                    swipeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
