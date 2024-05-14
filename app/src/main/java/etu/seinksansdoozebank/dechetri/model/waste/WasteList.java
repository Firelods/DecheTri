package etu.seinksansdoozebank.dechetri.model.waste;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementListObserver;
import etu.seinksansdoozebank.dechetri.model.observable.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WasteList extends ArrayList<Waste> implements Observable<WasteListObservers>{
    private static final String TAG = "512Bank";
    private final List<WasteListObservers   > observers = new ArrayList<>();



    public WasteList() {
        super();

        this.init();
    }
    public void init() {
        APIController.getWasteList(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG + "TasksListFragment", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public void addObserver(WasteListObservers observer) {
        observers.add(observer);
    }

    public void removeObserver(WasteListObservers observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers() {
        for (WasteListObservers observer : observers) {
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
