package etu.seinksansdoozebank.dechetri.model.waste;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.model.observable.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WasteList extends ArrayList<Waste> implements Observable<WasteListObserver> {
    private static final String TAG = "512Bank";
    private final ArrayList<WasteListObserver> observers = new ArrayList<>();

    private Activity activity;

    public WasteList(Activity activity) {
        this.activity = activity;
    }
    public void init() {
        APIController.getWasteList(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG + "TasksListFragment", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d(TAG + "TasksListFragment", "onResponse: " + response.body().string());
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Failed to get waste list", Toast.LENGTH_SHORT).show());
                    return;
                }
                String json = response.body().string();
                Gson gson = new Gson();
                Waste[] wastes = gson.fromJson(json, Waste[].class);
                if (wastes != null) {
                    WasteList.this.addAll(Arrays.asList(wastes));
                }
            }
        });
    }

    public void updateList() {
        this.clear();
        this.init();
    }

    @Override
    public void addObserver(WasteListObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(WasteListObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (WasteListObserver observer : observers) {
            observer.onWasteListChanged();
        }
    }

    @Override
    public boolean add(Waste waste) {
        boolean result = super.add(waste);
        if (result) {
            notifyObservers();
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Waste> c) {
        boolean result = super.addAll(c);
        if (result) {
            notifyObservers();
        }
        return result;
    }
}
