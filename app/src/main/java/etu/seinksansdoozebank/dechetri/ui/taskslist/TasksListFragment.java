package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;
import etu.seinksansdoozebank.dechetri.model.task.Task;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TasksListFragment extends Fragment implements TasksListAdapterListener {
    private static final String TAG = "512Bank";
    private FragmentTasksListBinding binding;
    private ListView listViewTasks;
    private TasksListAdapter taskListAdapter;
    private final List<Task> taskList = new ArrayList<>();
    private final List<Waste> wasteList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewTasks = binding.listViewTasks;


        APIController.getEmployeAssignee("2", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG + "TasksListFragment", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                Type taskListType = new TypeToken<List<Task>>() {
                }.getType();
                List<Task> tasks = gson.fromJson(json, taskListType);
                if (tasks != null) {
                    taskList.addAll(tasks);
                }
                getWasteList();
            }
        });

        // Create an adapter
        taskListAdapter = new TasksListAdapter(requireActivity(), taskList, wasteList);
        listViewTasks.setAdapter(taskListAdapter);
        return root;
    }

    private void getWasteList() {
        for (Task task : taskList) {
            APIController.getWaste(task.getIdWasteToCollect(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String message = e.getMessage();
                    Log.e("APIController", "Error while getting waste : " + message);
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la récupération des déchets : " + message, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Waste waste = gson.fromJson(json, Waste.class);
                    if (waste == null) {
                        return;
                    }
                    wasteList.add(waste);
                    // notify the listview that the data has changed
                    requireActivity().runOnUiThread(() -> taskListAdapter.notifyDataSetChanged());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
