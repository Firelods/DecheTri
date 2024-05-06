package etu.seinksansdoozebank.dechetri.model.flux;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AnnouncementList extends ArrayList<Announcement> {
    private static final String TAG = "512Bank";
    private final List<AnnouncementListObserver> observers = new ArrayList<>();

    public AnnouncementList() {
        super();
        this.init();
    }

    private void init() {
        APIController.getAllAnnouncement(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG + "AnnouncementList", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                Log.d(TAG + "AnnouncementList", "onResponse: " + json);
                Gson gson = new Gson();
                Announcement[] announcements = gson.fromJson(json, Announcement[].class);
                if (announcements != null) {
                    AnnouncementList.this.addAll(Arrays.asList(announcements));
                }
            }
        });
    }

    public void updateList() {
        this.clear();
        this.init();
    }

    public void addObserver(AnnouncementListObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AnnouncementListObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (AnnouncementListObserver observer : observers) {
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
    public void add(int index, Announcement element) {
        super.add(index, element);
        notifyObservers();
    }

    @Override
    public boolean addAll(Collection<? extends Announcement> c) {
        boolean result = super.addAll(c);
        if (result) {
            notifyObservers();
        }
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Announcement> c) {
        boolean result = super.addAll(index, c);
        if (result) {
            notifyObservers();
        }
        return result;
    }
}
