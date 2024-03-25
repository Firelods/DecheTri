package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;
import etu.seinksansdoozebank.dechetri.model.taskslist.Task;

public class TasksListFragment extends Fragment implements TasksListAdapterListener {
    private FragmentTasksListBinding binding;
    private ListView listViewTasks;
    private List<Task> taskList;
    private TasksListAdapter taskListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewTasks = binding.listViewTasks;

        // Create a list of flux
        taskList = new ArrayList<>();
        taskList.add(new Task("Task 1", "Waste type 1", "Address 1"));
        taskList.add(new Task("Task 2", "Waste type 2", "Address 2"));
        taskList.add(new Task("Task 3", "Waste type 3", "Address 3"));
        taskList.add(new Task("Task 4", "Waste type 4", "Address 4"));
        taskList.add(new Task("Task 5", "Waste type 5", "Address 5"));

        // Create an adapter
        taskListAdapter = new TasksListAdapter(getActivity(), taskList);
        listViewTasks.setAdapter(taskListAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
