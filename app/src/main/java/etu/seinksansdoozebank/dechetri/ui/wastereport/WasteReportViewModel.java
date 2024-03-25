package etu.seinksansdoozebank.dechetri.ui.wastereport;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WasteReportViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WasteReportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragment des signalements des déchets");
    }

    public LiveData<String> getText() {
        return mText;
    }}