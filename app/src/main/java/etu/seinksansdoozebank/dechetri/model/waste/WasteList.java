package etu.seinksansdoozebank.dechetri.model.waste;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

    public WasteList(Activity activity) {
        Context context = activity.getApplicationContext();
        APIController.getWasteList(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = e.getMessage();
                Log.e("APIController", "Error while getting waste : " + message);
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "Erreur lors de la récupération des déchêts : " + message, Toast.LENGTH_SHORT).show();
                    notifyObservers();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, "Erreur lors de la récupération des déchêts", Toast.LENGTH_SHORT).show();
                        notifyObservers();
                    });
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

    public void addObserver(WasteListObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WasteListObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (WasteListObserver observer : observers) {
            observer.onWasteListChanged();
        }
    }

    @Override
    public boolean add(Waste announcement) {
        boolean result = super.add(announcement);
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
