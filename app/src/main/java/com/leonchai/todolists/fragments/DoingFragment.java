package com.leonchai.todolists.fragments;

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
import com.leonchai.todolists.FirebaseDB;
import com.leonchai.todolists.R;
import com.leonchai.todolists.TaskModelComparator;
import com.leonchai.todolists.activities.MainActivity;
import com.leonchai.todolists.activities.TaskDetailActivity;
import com.leonchai.todolists.adapters.TaskAdapter;
import com.leonchai.todolists.dataModels.TaskListModel;
import com.leonchai.todolists.dataModels.TaskModel;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DoingFragment extends Fragment {

    public static final String TABLE_NAME = "doingTasks";

    private ListView doingListView;
    private TaskListModel currentTaskList;

    private TaskAdapter taskAdapter;
    private SwipeActionAdapter swipeAdapter;

    private FirebaseUser user;

    private ArrayList<TaskModel> tasksList = new ArrayList<>();

    public DoingFragment() {
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

        currentTaskList = getArguments().getParcelable(MainActivity.EXTRA_TASKLIST);

        doingListView = (ListView) view.findViewById(R.id.doListView);

        taskAdapter = new TaskAdapter(getActivity(),tasksList);

        swipeAdapter = new SwipeActionAdapter(taskAdapter);
        swipeAdapter.setListView(doingListView);

        doingListView.setAdapter(swipeAdapter);

        // Add Left swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.unassigned_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.unassigned_bg);

        //Add Right swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.done_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.done_bg);

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
                        // Move back to do fragment
                        case DIRECTION_FAR_LEFT:

                            // Add task to do fragment list
                            currentTask.setUser("");
                            FirebaseDB.addTask(currentTaskList.getId(), DoFragment.TABLE_NAME,currentTask);

                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(currentTaskList.getId(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;

                        // Move to Done Fragment
                        case DIRECTION_FAR_RIGHT:
                            // Add task to done fragment list
                            FirebaseDB.addTask(currentTaskList.getId(), DoneFragment.TABLE_NAME,currentTask);

                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(currentTaskList.getId(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

        doingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra("task", tasksList.get(i));
                intent.putExtra(MainActivity.EXTRA_TASKLIST, currentTaskList);
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
        FirebaseDB.getList(currentTaskList.getId(), TABLE_NAME, new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(Object tasks) {
                List<TaskModel> theTasks = (List<TaskModel>) tasks;
                // Check if there is any data to fetch
                if(!theTasks.isEmpty()) {
                    tasksList.clear();
                    tasksList.addAll(theTasks);
                    Collections.sort(tasksList, new TaskModelComparator());
                    swipeAdapter.notifyDataSetChanged();
                }
            }

        });
    }

}
