package etu.seinksansdoozebank.dechetri.controller.api;

import com.google.gson.Gson;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;
import etu.seinksansdoozebank.dechetri.model.flux.Waste;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class APIController {
    private String baseUrl;
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public APIController(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
    }

    public String getUrl() {
        return baseUrl;
    }

    public void createNewsAnnouncement(String title, String description, String type) throws IOException {
        Announcement announcement = new Announcement(title, description, type);
        RequestBody body = RequestBody.create(new Gson().toJson(announcement), JSON);
        Request request = new Request.Builder()
                .url(baseUrl + "/announcement/news")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
        }
    }

    public void createEventAnnouncement(String title, String description, String type, String eventDate) throws IOException {
        Announcement announcement = new Announcement(title, description, type, eventDate);
        RequestBody body = RequestBody.create(new Gson().toJson(announcement), JSON);
        Request request = new Request.Builder()
                .url(baseUrl + "/announcement/event")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
        }
    }

    public List<String> getAnnouncementTypes() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/announcement/types")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), List.class);
        }
    }

    public AnnouncementList getAllAnnouncement() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/announcement/all")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), AnnouncementList.class);
        }
    }

    public void deleteAnnouncement(String id) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/announcement/" + id)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
        }
    }

    public Waste getWasteById(int id) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/waste/" + id)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), Waste.class);
        }
    }

    public List<String> getWasteTypes() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/waste/types")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), List.class);
        }
    }

    public byte[] getWasteImage(int id) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/waste/image/" + id)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            return response.body().bytes();
        }
    }

    public List<Waste> getAllWastes() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/waste/all")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), List.class);
        }
    }
}
