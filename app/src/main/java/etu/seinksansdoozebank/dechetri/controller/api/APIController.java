package etu.seinksansdoozebank.dechetri.controller.api;

import okhttp3.*;


public class APIController {
    private static final String baseUrl = "http://138.197.176.101:8080/";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Call put(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call assignTaskToEmployee(String wasteId, String employeeId, Callback callback) {
        String url = baseUrl + "task/assign";
        return put(url, "{\"idAssignee\":\"" + employeeId + "\",\"idWasteToCollect\":\"" + wasteId + "\"}", callback);
    }
}
