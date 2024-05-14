package etu.seinksansdoozebank.dechetri.model.waste;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WasteList extends ArrayList<Waste> {
    private static final String TAG = "512Bank";
    public WasteList() {
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

}
