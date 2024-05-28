package etu.seinksansdoozebank.dechetri.ui.taskslist;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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
public class PersonnalTaskList extends Fragment {

    public PersonnalTaskList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personnal_task_list, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String id = sharedPreferences.getString(requireContext().getString(R.string.shared_preferences_key_user_id), requireContext().getResources().getString(R.string.role_user_id));
        TasksListFragment fragment = TasksListFragment.newInstance("/assigned/", id);
        getChildFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();

        return view;
    }
}