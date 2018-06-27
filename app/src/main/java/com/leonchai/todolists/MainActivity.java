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
import com.leonchai.todolists.dataModels.TaskListModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth auth;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseUser user;

    private ListView mNavigationList;
    private TaskListAdapter mTaskListAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String taskListID;

    private List<TaskListModel> taskLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        setupDrawer();

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Left side navigation view setup
        mNavigationList = (ListView) findViewById(R.id.nav);

        setupCreateListBtn();

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        addDrawerItems();
        fetchTaskLists();

        //check if just logged in or switch to different list
        taskListID = getIntent().getStringExtra("taskListID");
        if(taskListID == null){
            taskListID = user.getUid();

        }

        // check if new user of existing
        String username = getIntent().getStringExtra("username");
        if(username != null) {
            FirebaseDB.createUserTable(user.getUid(), username, user.getEmail(), new FirebaseDB.FirebaseCallback() {
                @Override
                public void onCallback(Object listName) {
                    mActivityTitle = (String) listName;
                    getSupportActionBar().setTitle(mActivityTitle);
                }
            });
        } else {
            FirebaseDB.getTaskListName(taskListID, new FirebaseDB.FirebaseCallback() {
                @Override
                public void onCallback(Object listName) {
                    mActivityTitle = (String) listName;
                    getSupportActionBar().setTitle(mActivityTitle);
                }
            });
        }

        setupTabs();
    }

    private void addDrawerItems() {
        taskLists = new ArrayList<>();
        mTaskListAdapter = new TaskListAdapter(this, taskLists);
        mNavigationList.setAdapter(mTaskListAdapter);
        mNavigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, taskLists.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.signOut();
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
            startActivity(intent);
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
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

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = input.getText().toString();
                        String id = FirebaseDB.createList(user.getUid(), text);
                        //mNavigationView.getMenu().add(text).setTooltipText(id);

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
        bundle.putString("taskListID", taskListID);
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

