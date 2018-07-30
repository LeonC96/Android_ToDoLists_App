package com.leonchai.todolists.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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

public class DoFragment extends Fragment {

    public static final String TABLE_NAME = "doTasks";

    private TaskListModel currentTaskList;

    private ListView doListView;
    private View view;
    private TaskAdapter taskAdapter;
    private SwipeActionAdapter swipeAdapter;

    private FirebaseUser user;

    private ArrayList<TaskModel> tasksList = new ArrayList<>();

    public DoFragment() {
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_do, container, false);

        currentTaskList = getArguments().getParcelable(MainActivity.EXTRA_TASKLIST);

        doListView = (ListView) view.findViewById(R.id.doListView);

        taskAdapter = new TaskAdapter(getActivity(),tasksList);

        swipeAdapter = new SwipeActionAdapter(taskAdapter);
        swipeAdapter.setListView(doListView);

        doListView.setAdapter(swipeAdapter);

        // Add Left swipe
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT, R.layout.delete_do_bg);
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.delete_do_bg);

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
                    TaskModel currentTask = tasksList.get(currentPosition);

                    switch (currentDirection) {
                        case DIRECTION_FAR_LEFT:
                            // Delete
                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(user.getUid(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                        case DIRECTION_FAR_RIGHT:

                            // Add task to doing fragment list
                            currentTask.setUser(user.getDisplayName());
                            FirebaseDB.addTask(user.getUid(), DoingFragment.TABLE_NAME,currentTask);

                            // Removes task from list and from firebase
                            tasksList.remove(currentPosition);
                            FirebaseDB.removeTask(user.getUid(), TABLE_NAME, currentTask);
                            swipeAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

        doListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                List<TaskModel> theTasks = (List<TaskModel>) tasks;
                // Check if there is any data to fetch
                if(!theTasks.isEmpty()) {
                    tasksList.clear();
                    tasksList.addAll(theTasks);
                    Collections.sort(tasksList, new TaskModelComparator());
                    swipeAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);

            }

        });
    }
}
