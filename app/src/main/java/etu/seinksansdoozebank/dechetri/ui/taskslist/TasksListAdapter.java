package etu.seinksansdoozebank.dechetri.ui.taskslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.model.task.Task;

public class TasksListAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;  //Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private final FragmentActivity activity;
    private final List<Task> taskList;

    public TasksListAdapter(FragmentActivity activity, List<Task> taskList) {
        this.activity = activity;
        this.taskList = taskList;
        this.mInflater = LayoutInflater.from(activity.getBaseContext());
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int i) {
        return taskList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem;

        // (1) : Réutilisation des layouts
        listItem = convertView == null ? mInflater.inflate(R.layout.item_taskslist, parent, false) : convertView;
        listItem.setElevation(5);
        if (!taskList.isEmpty()) {
            // (2) : Récupération des TextView de notre layout
            TextView name = listItem.findViewById(R.id.taskList_title);

            // (3) : Renseignement des valeurs
            // name.setText(taskList.get(position).getTitle());
        }

        return listItem;
    }
}
