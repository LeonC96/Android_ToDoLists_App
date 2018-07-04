package com.leonchai.todolists;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leonchai.todolists.adapters.TaskListAdapter;
import com.leonchai.todolists.dataModels.TaskListModel;

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
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private List<TaskListModel> taskLists;
    private TaskListModel currentTaskList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        setupDrawer();

        // setup actionBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //check if just logged in or switch to different list
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey(EXTRA_TASKLIST)){
                currentTaskList = extras.getParcelable(EXTRA_TASKLIST);
                System.out.println(currentTaskList.getId());
            }
        }

        if(currentTaskList == null){
            List<String> userIDInList = new ArrayList<>();
            userIDInList.add(user.getUid());
            currentTaskList = new TaskListModel(user.getUid(), "Personal", userIDInList);
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
        setupDrawerItems();

        setupTabs();
    }

    private void setupDrawerItems() {
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
                        FirebaseDB.deleteTaskList(selectedTaskList.getId(), user.getUid());
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, options[i], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            fetchTaskLists();
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

                //TODO: Create new task list and redirect to new list
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String taskListName = input.getText().toString();
                        String id = FirebaseDB.createList(user.getUid(), taskListName);
                        List<String> users = new ArrayList<>();
                        users.add(user.getUid());
                        currentTaskList = new TaskListModel(id, taskListName, users);

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

    private void fetchTaskLists(){
        List<String> defaultUser = new ArrayList<>();
        defaultUser.add(user.getUid());
        final TaskListModel userPersonal = new TaskListModel(user.getUid(), "Personal", defaultUser);
        FirebaseDB.getUserLists(user.getUid(), new FirebaseDB.FirebaseCallback() {
            @Override
            public void onCallback(Object taskList) {
                taskLists.clear();
                taskLists.add(userPersonal);
                taskLists.addAll((List<TaskListModel>) taskList);
                mTaskListAdapter.notifyDataSetChanged();
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


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("To-Do Lists");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

