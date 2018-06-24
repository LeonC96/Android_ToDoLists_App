package com.leonchai.todolists;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TaskDetailActivity extends AppCompatActivity {

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        toolBar = findViewById(R.id.taskDetailToolbar);
        setSupportActionBar(toolBar);

        Bundle data = getIntent().getExtras();
        TaskModel task = data.getParcelable("task");

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Toast.makeText(this, "Editing", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
