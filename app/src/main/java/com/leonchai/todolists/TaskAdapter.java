package com.leonchai.todolists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<TaskModel> {
    private Context context;
    private List<TaskModel> taskList;

    public TaskAdapter(Context context, ArrayList<TaskModel> list){
        super(context, 0, list);
        this.context = context;
        this.taskList = list;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.row_item,parent,false);

        TaskModel currentTask = taskList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textViewName);
        name.setText(currentTask.getName());

        TextView dueDate = (TextView) listItem.findViewById(R.id.textViewDueDate);
        dueDate.setText(currentTask.getDueDate());

        TextView user = (TextView) listItem.findViewById(R.id.textViewUser);
        user.setText(currentTask.getUser());

        return listItem;
    }
}
