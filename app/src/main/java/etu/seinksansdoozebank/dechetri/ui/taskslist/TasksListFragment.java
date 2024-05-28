package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentTasksListBinding;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TasksListFragment extends Fragment implements TasksListAdapterListener {
    private static final String URL_KEY = "query_url";
    private static final String ID_KEY = "id";

    private FragmentTasksListBinding binding;
    private TasksFromWasteListAdapter taskListAdapter;
    private final List<Waste> wasteList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noTasksAssignedTextView;
    private View root;

    private String requestURL;
    private String connectedID;

    public static TasksListFragment newInstance(String url, String id) {
        TasksListFragment fragment = new TasksListFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        args.putString(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    public TasksListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestURL = getArguments().getString(URL_KEY);
            connectedID = getArguments().getString(ID_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksListBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        ListView listViewTasks = binding.listViewTasks;
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::getEmployeAssignee);

        noTasksAssignedTextView=root.findViewById(R.id.noTaskAssigned);
        noTasksAssignedTextView.setVisibility(root.GONE);
        taskListAdapter = new TasksListAdapter(requireActivity(), wasteList);
        listViewTasks.setAdapter(taskListAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        getEmployeAssignee();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getEmployeAssignee() {
        swipeRefreshLayout.setRefreshing(true);
        APIController.getEmployeeTasks(requestURL, connectedID, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la récupération des tâches", Toast.LENGTH_SHORT).show());
                    return;
                }
                wasteList.clear();
                wasteList.addAll(APIController.parseWastes(response.body().string()));
                requireActivity().runOnUiThread(() -> {
                    noTasksAssignedTextView.setVisibility(root.VISIBLE);
                    taskListAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        });
    }
}

