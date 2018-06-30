package com.leonchai.todolists;

import com.leonchai.todolists.dataModels.TaskModel;

import java.util.Comparator;

public class TaskModelComparator implements Comparator<TaskModel> {
    @Override
    public int compare(TaskModel taskModel, TaskModel t1) {
        return taskModel.getDueDateAsDate().compareTo(t1.getDueDateAsDate());
    }
}
