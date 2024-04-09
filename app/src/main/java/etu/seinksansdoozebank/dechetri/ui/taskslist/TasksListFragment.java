package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;
import etu.seinksansdoozebank.dechetri.model.task.TaskList;

public class TasksListFragment extends Fragment implements TasksListAdapterListener {
    private FragmentTasksListBinding binding;
    private ListView listViewTasks;
    private TasksListAdapter taskListAdapter;
    private final TaskList taskList = new TaskList();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewTasks = binding.listViewTasks;

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
