package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etu.seinksansdoozebank.dechetri.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UnassignedTaskList extends Fragment {
    public UnassignedTaskList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unassigned_task_list, container, false);
        TasksListFragment fragment = TasksListFragment.newInstance("/unassigned", "");
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_unassigned_task_list, fragment).commit();
        return view;
    }
}