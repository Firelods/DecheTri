package etu.seinksansdoozebank.dechetri.ui.taskslist;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;
import etu.seinksansdoozebank.dechetri.model.task.Task;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TasksListFragment extends Fragment implements TasksListAdapterListener {
    private static final String TAG = "512Bank" + TasksListFragment.class.getSimpleName();
    private FragmentTasksListBinding binding;
    private ListView listViewTasks;
    private TasksListAdapter taskListAdapter;
    private final List<Task> taskList = new ArrayList<>();
    private final List<Waste> wasteList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewTasks = binding.listViewTasks;
        swipeRefreshLayout = binding.swipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(this::getEmployeAssignee);

//        getEmployeAssignee();

        // Create an adapter
        taskListAdapter = new TasksListAdapter(requireActivity(), wasteList);
        listViewTasks.setAdapter(taskListAdapter);
        return root;
    }

    private void getEmployeAssignee() {
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String id = sharedPreferences.getString(requireContext().getString(R.string.shared_preferences_key_user_id), requireContext().getResources().getString(R.string.role_user_id));
        APIController.getEmployeAssignee(id, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show());
                    return;
                }
                String json = response.body().string();
                Gson gson = new Gson();
                Type taskListType = new TypeToken<List<Task>>() {
                }.getType();
                List<Task> tasks = gson.fromJson(json, taskListType);
                if (tasks != null) {
                    taskList.clear();
                    wasteList.clear();
                    taskList.addAll(tasks);
                    if (taskList.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            taskListAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        });
                        return;
                    }
                    getWasteList();
                } else {
                    // The employee has no tasks
                    requireActivity().runOnUiThread(() -> {
                        taskList.clear();
                        taskListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }

    private void getWasteList() {
        List<Task> tempTaskList = new ArrayList<>(taskList);
        for (Task task : tempTaskList) {
            APIController.getWaste(task.getIdWasteToCollect(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String message = e.getMessage();
                    Log.e("APIController", "Error while getting waste : " + message);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Erreur lors de la récupération des déchets", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Erreur lors de la récupération des déchets", Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        });
                        return;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Waste waste = gson.fromJson(json, Waste.class);
                    if (waste == null) {
                        return;
                    }
                    wasteList.add(waste);
                    // notify the listview that the data has changed
                    requireActivity().runOnUiThread(() -> {
                        taskListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        getEmployeAssignee();
    }
}

