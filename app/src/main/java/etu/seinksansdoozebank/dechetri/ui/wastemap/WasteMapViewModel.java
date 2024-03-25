package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WasteMapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WasteMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragment des cartes de déchets");
    }

    public LiveData<String> getText() {
        return mText;
    }}