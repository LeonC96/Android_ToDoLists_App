package com.leonchai.todolists.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.leonchai.todolists.FirebaseDB;
import com.leonchai.todolists.R;
import com.leonchai.todolists.adapters.TaskListAdapter;
import com.leonchai.todolists.adapters.UsersListAdapter;
import com.leonchai.todolists.dataModels.TaskListModel;
import com.leonchai.todolists.dataModels.UserModel;
import com.leonchai.todolists.fragments.DoFragment;
import com.leonchai.todolists.fragments.DoingFragment;
import com.leonchai.todolists.fragments.DoneFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_TASKLIST = "taskList";

    private FirebaseAuth auth;
    private FirebaseUser user;

    private ListView mNavigationList;
    private TaskListAdapter mTaskListAdapter;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private List<TaskListModel> taskLists;
    private TaskListModel currentTaskList = null;

    private ListView usersListView;
    private List<UserModel> usersInList;
    private UsersListAdapter mUsersListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // setup actionBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_lists);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //check if just logged in or switch to different list
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey(EXTRA_TASKLIST)){
                currentTaskList = extras.getParcelable(EXTRA_TASKLIST);
            }
        }

        // if user just logged in go to first list in firebase table
        //TODO: if personal was deleted. For now "personal" list can't be delete but may change later
        if(currentTaskList == null){
            currentTaskList = new TaskListModel(user.getUid(), "Personal");
        }

        // check if new user or existing
        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if(username != null) {
            FirebaseDB.createUserTable(user.getUid(), username, user.getEmail());

        }

        mActivityTitle = currentTaskList.getName();
        getSupportActionBar().setTitle(mActivityTitle);


        // Left side navigation view setup
        mNavigationList = (ListView) findViewById(R.id.nav);
        setupCreateListBtn();
        setupTaskListNav();

        // Right Side User navigation
        usersListView = (ListView) findViewById(R.id.usersList);
        setupUsersListView();
        setupAddUserBtn();

        setupTabs();

    }

    private void setupTaskListNav() {
        taskLists = new ArrayList<>();
        mTaskListAdapter = new TaskListAdapter(this, taskLists);
        mNavigationList.setAdapter(mTaskListAdapter);
        mNavigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskListModel selectedTaskList = taskLists.get(position);

                if(selectedTaskList.getId().equals(currentTaskList.getId())){
                    Toast.makeText(MainActivity.this, "Already Selected", Toast.LENGTH_SHORT).show();

                } else {
                    currentTaskList = selectedTaskList;
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_TASKLIST, currentTaskList);
                    finish();
                    startActivity(intent);
                }

            }
        });

        //Setup long press for deleting task list
        mNavigationList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TaskListModel selectedTaskList = taskLists.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Options");
                final String[] options = new String[]{"Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(options[i].equals("Delete")) {
                            if(selectedTaskList.getId().equals(user.getUid())){
                                Toast.makeText(MainActivity.this, "Cannot Delete Personal List!", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseDB.deleteTaskList(selectedTaskList.getId(), user.getUid());
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(MainActivity.this, "Deleted " + selectedTaskList.getName(), Toast.LENGTH_SHORT).show();

                                //taskLists.remove(i);
                                if(selectedTaskList.getId().equalsIgnoreCase(currentTaskList.getId())) {
                                    Intent intent = getIntent();
                                    intent.putExtra(EXTRA_TASKLIST, taskLists.get(0));
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    //show users list
    private void setupUsersListView(){
        usersInList = new ArrayList<>();
        mUsersListAdapter = new UsersListAdapter(this, usersInList);
        usersListView.setAdapter(mUsersListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // For tool button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {

            Intent intent = new Intent(MainActivity.this, addTaskActivity.class);
            intent.putExtra(EXTRA_TASKLIST, currentTaskList);
            startActivity(intent);
            return true;
        }

        //TODO: refresh users
        if(id == R.id.action_users){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.END)){
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.closeDrawers();
                fetchListUsers();
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }

        if(id == android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawers();
                fetchTaskLists();
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupCreateListBtn(){
        Button createListBtn;

        createListBtn = findViewById(R.id.addListBtn);
        createListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create New List");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP);
                builder.setView(input);

                //Create new task list and redirect to new list
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String taskListName = input.getText().toString();
                        String id = FirebaseDB.createList(user , taskListName);
                        currentTaskList = new TaskListModel(id, taskListName);

                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_TASKLIST, currentTaskList);
                        startActivity(intent);
                        finish();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });
    }

    private void setupAddUserBtn(){
        Button addUserBtn;

        addUserBtn = findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add New User");

                final EditText userEmailInput = new EditText(MainActivity.this);
                userEmailInput.setInputType(InputType.TYPE_CLASS_TEXT);
                userEmailInput.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP);
                builder.setView(userEmailInput);

                builder.setPositiveButton("Add User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String addedUserEmail = userEmailInput.getText().toString();

                        //Check if there's a user with inputted email in firebase
                        auth.fetchProvidersForEmail(addedUserEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                if(task.isSuccessful()) {
                                    if (task.getResult().getProviders().size() > 0) {
                                        //user exist
                                        FirebaseDB.addUserToList(user.getUid(), addedUserEmail, currentTaskList.getId(), currentTaskList.getName());
                                        mDrawerLayout.closeDrawers();
                                    } else {
                                        //user doesn't exist
                                        Toast.makeText(MainActivity.this, "User Does Not Exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //invalid email format
                                    Toast.makeText(MainActivity.this, "User Does Not Exist", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
    }

    private void fetchTaskLists(){
        //List<String> defaultUser = new ArrayList<>();
        //defaultUser.add(user.getUid());
        //final TaskListModel userPersonal = new TaskListModel(user.getUid(), "Personal");
        FirebaseDB.getUserLists(user.getUid(), new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(Object taskList) {
                taskLists.clear();
                taskLists.addAll((List<TaskListModel>) taskList);
                mTaskListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchListUsers(){
        FirebaseDB.getListUsers(currentTaskList, new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(Object tasks) {
                usersInList.clear();
                usersInList.addAll((List<UserModel>) tasks);
                mUsersListAdapter.notifyDataSetChanged();

            }
        });
    }

    private void setupTabs(){
        TabLayout tabLayout;
        ViewPager viewPager;

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //adding fragments
        DoFragment doFragment = new DoFragment();
        DoingFragment doingFragment = new DoingFragment();
        DoneFragment doneFragment = new DoneFragment();

        //send task list ID to each fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("taskList", currentTaskList);
        doFragment.setArguments(bundle);
        doingFragment.setArguments(bundle);
        doneFragment.setArguments(bundle);

        adapter.AddFragment(doFragment, "DO");
        adapter.AddFragment(doingFragment, "DOING");
        adapter.AddFragment(doneFragment, "DONE");

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentListTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentListTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return fragmentListTitles.get(position);
        }

        public void AddFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            fragmentListTitles.add(title);
        }

    }
}

