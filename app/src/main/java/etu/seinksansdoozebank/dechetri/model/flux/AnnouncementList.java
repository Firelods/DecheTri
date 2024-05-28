package etu.seinksansdoozebank.dechetri.model.flux;

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
import java.util.List;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.model.observable.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnnouncementList extends ArrayList<Announcement> implements Observable<AnnouncementListObserver> {
    private static final String TAG = "512Bank";
    private final List<AnnouncementListObserver> observers = new ArrayList<>();
    private final FragmentActivity activity;
    private final Context context;

    public AnnouncementList(FragmentActivity activity) {
        super();
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    private void init() {
        APIController.getAllAnnouncement(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = e.getMessage();
                Log.e("APIController", "Error while getting announcement : " + message);
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "Erreur lors de la récupération des annonces : " + message, Toast.LENGTH_SHORT).show();
                    notifyObservers();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG + "AnnouncementList", "onResponse: " + response.body().string());
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, "Erreur lors de la récupération des annonces", Toast.LENGTH_SHORT).show();
                        notifyObservers();
                    });
                    return;
                }
                String json = response.body().string();
                Log.d(TAG + "AnnouncementList", "onResponse: " + json);
                Gson gson = new Gson();
                Announcement[] announcements = gson.fromJson(json, Announcement[].class);
                if (announcements != null) {
                    AnnouncementList.this.addAll(Arrays.asList(announcements));
                }
                notifyObservers();
            }
        });
    }

    public void updateList() {
        this.clear();
        this.init();
    }

    public void addObserver(AnnouncementListObserver observer) {
        Log.d(TAG + "addObserver", observer.toString());
        observers.add(observer);
    }

    public void removeObserver(AnnouncementListObserver observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObservers() {
        for (AnnouncementListObserver observer : observers) {
            Log.d(TAG + "notifyObservers", observer.toString());
            observer.onAnnouncementListChanged();
        }
    }

    @Override
    public boolean add(Announcement announcement) {
        boolean result = super.add(announcement);
        if (result) {
            notifyObservers();
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Announcement> c) {
        boolean result = super.addAll(c);
        Log.d(TAG + "addAll", "change" + result);
        if (result) {
            notifyObservers();
        }
        return result;
    }
}
