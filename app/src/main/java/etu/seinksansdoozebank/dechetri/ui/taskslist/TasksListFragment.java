package etu.seinksansdoozebank.dechetri.ui.taskslist;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;

public class TasksListFragment extends Fragment {
    private FragmentTasksListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TasksListViewModel tasksListViewModel =
                new ViewModelProvider(this).get(TasksListViewModel.class);

        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTasksList;
        tasksListViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}