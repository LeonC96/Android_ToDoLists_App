package com.leonchai.todolists.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leonchai.todolists.dataModels.TaskListModel;

import java.util.List;

public class TaskListAdapter extends ArrayAdapter<TaskListModel> {
    private Context context;
    private List<TaskListModel> taskList;

    public TaskListAdapter(Context context, List<TaskListModel> list){
        super(context, android.R.layout.simple_list_item_1, list);
        this.context = context;
        this.taskList = list;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);
        }

        TaskListModel currentTaskList = taskList.get(position);

        TextView nameTxt = view.findViewById(android.R.id.text1);

        nameTxt.setText(currentTaskList.getName());

        return view;
    }
}
