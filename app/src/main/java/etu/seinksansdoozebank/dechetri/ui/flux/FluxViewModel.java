package etu.seinksansdoozebank.dechetri.ui.flux;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FluxViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FluxViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragment des activités municipales");
    }

    public LiveData<String> getText() {
        return mText;
    }
}