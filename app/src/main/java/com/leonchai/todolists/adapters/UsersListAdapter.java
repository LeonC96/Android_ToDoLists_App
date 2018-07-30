package com.leonchai.todolists.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leonchai.todolists.dataModels.UserModel;

import java.util.List;

public class UsersListAdapter extends ArrayAdapter<UserModel> {

    private Context context;
    private List<UserModel> users;

    public UsersListAdapter(Context context, List<UserModel> users) {
        super(context, android.R.layout.simple_list_item_2, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, viewGroup,false);
        }

        UserModel currentUser = users.get(i);

        TextView nameTxt = view.findViewById(android.R.id.text1);
        TextView emailTxt = view.findViewById(android.R.id.text2);

        nameTxt.setText(currentUser.getName());
        emailTxt.setText(currentUser.getEmail());

        return view;
    }
}
