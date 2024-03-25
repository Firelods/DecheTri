package etu.seinksansdoozebank.dechetri.ui.taskslist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TasksListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TasksListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragment des tâches");
    }

    public LiveData<String> getText() {
        return mText;
    }
}