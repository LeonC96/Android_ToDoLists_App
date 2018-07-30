package com.leonchai.todolists.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.leonchai.todolists.R;
import com.leonchai.todolists.dataModels.TaskListModel;
import com.leonchai.todolists.dataModels.TaskModel;

public class TaskDetailActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private TaskModel task;
    private TaskListModel currentTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        toolBar = findViewById(R.id.taskDetailToolbar);
        toolBar.setTitle("Task");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        Bundle data = getIntent().getExtras();
        task = data.getParcelable("task");
        currentTaskList = data.getParcelable(MainActivity.EXTRA_TASKLIST);

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView dueDateView = findViewById(R.id.dueDateTextView);
        TextView description = findViewById(R.id.descriptionTextView);
        TextView userTextView = findViewById(R.id.userTextView);

        nameTextView.setText(task.getName());
        dueDateView.setText(task.getDueDate());
        description.setText(task.getDescription());
        userTextView.setText(task.getUser());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.task_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, addTaskActivity.class);
            intent.putExtra("task", task);
            intent.putExtra(MainActivity.EXTRA_TASKLIST, currentTaskList);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
