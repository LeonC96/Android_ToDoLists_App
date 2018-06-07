package com.leonchai.todolists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class DoFragment extends Fragment {

    private ListView doList;
    private TaskAdapter adapter;

    public DoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_do, container, false);
        System.out.println("DOFRAG");
        doList = (ListView) view.findViewById(R.id.doListView);

        ArrayList<DataModel> tasks = new ArrayList<>();
        tasks.add(new DataModel("Do this", "01/20/19"));
        tasks.add(new DataModel("Do that", "06/20/19"));
        tasks.add(new DataModel("Do this", "01/20/29"));
        tasks.add(new DataModel("Do and this", "01/24/19"));
        tasks.add(new DataModel("Do this", "01/20/18"));
        tasks.add(new DataModel("Do this", "01/06/19"));

        adapter = new TaskAdapter(getActivity(),tasks);
        doList.setAdapter(adapter);

        return view;
    }


}
