package com.leonchai.todolists;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class addTaskActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth auth;

    private String taskName;
    private String dueDate;
    private String description;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView dueDateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("New Task");
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        dueDateTextView = (TextView) findViewById(R.id.dueDateTextView);
        showDatePickerDialog();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_saved was selected
            case R.id.action_save:
                if(nameEditText.getText().toString().equals("") || dueDateTextView.getText().toString().equals("")){
                    Toast.makeText(this, "Must have a name & due date!", Toast.LENGTH_SHORT).show();
                    break;
                }
                taskName = nameEditText.getText().toString();
                description = descriptionEditText.getText().toString();
                dueDate = dueDateTextView.getText().toString();

                TaskModel newTask = new TaskModel(taskName, dueDate, description);
                FirebaseDB.addTask(user.getUid(), DoFragment.TABLE_NAME, newTask);

                Toast.makeText(this, "Saved Task", Toast.LENGTH_SHORT)
                        .show();
                finish();
                break;

            default:
                break;
        }

        return true;
    }

    private void showDatePickerDialog(){
        Button dueDatebtn = (Button) findViewById(R.id.dueDateButton);

        dueDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        StringBuffer strBuff = new StringBuffer();

                        strBuff.append(month + 1);
                        strBuff.append("/");
                        strBuff.append(day);
                        strBuff.append("/");
                        strBuff.append(year);

                        dueDateTextView.setText(strBuff);
                    }
                };

                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(addTaskActivity.this,  android.R.style.Theme_Holo_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Select Due Date");

                datePickerDialog.show();
            }
        });
    }
}
