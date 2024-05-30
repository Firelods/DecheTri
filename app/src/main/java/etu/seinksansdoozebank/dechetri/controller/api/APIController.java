package etu.seinksansdoozebank.dechetri.controller.api;

import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import etu.seinksansdoozebank.dechetri.model.user.Role;
import etu.seinksansdoozebank.dechetri.model.user.User;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class APIController {
    private static final String TAG = "512Bank";
    private static final String BASE_URL = "http://138.197.176.101:8080/";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Call put(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .put(body)
                .build();
        Log.d(TAG + "APIController", "put: " + request);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();
        Log.d(TAG + "APIController", "post: " + request);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .get()
                .build();
        Log.d(TAG + "APIController", "get: " + request);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static Call delete(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .delete()
                .build();
        Log.d(TAG + "APIController", "delete: " + request);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private static Call patch(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .patch(body)
                .build();
        Log.d(TAG + "APIController", "patch: " + request);
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }


    /* task */

    /**
     * Assign a task to an employee (PUT method)
     *
     * @param wasteId    String
     * @param employeeId String
     * @param callback   Callback
     * @return Call
     * {
     * "idAssignee": "string",
     * "idWasteToCollect": "string"
     * }
     * @route /task/assign
     */
    public static Call assignTask(String wasteId, String employeeId, Callback callback) {
        if (employeeId != null) {
            employeeId = "\"" + employeeId + "\"";
        }
        String json = "{\n" +
                "  \"idAssignee\": " + employeeId + ",\n" +
                "  \"idWasteToCollect\": \"" + wasteId + "\"\n" +
                "}";
        Log.d(TAG + "APIController", "assignTask: " + json);
        return put("task/assign", json, callback);
    }

    /**
     * Mark a task as completed (PUT method)
     *
     * @param idWaste  String
     * @param callback Callback
     * @return Call
     * {
     * "id-task": "string"
     * }
     * @route /task/complete/{id-task}
     */
    public static Call completeTask(String idWaste, Callback callback) {
        String json = "{\n" +
                "  \"id-task\": \"" + idWaste + "\"\n" +
                "}";
        return put("task/complete/" + idWaste, json, callback);
    }

    /**
     * Get all tasks assigned to an employee (GET method)
     *
     * @param idEmploye String
     * @param callback  Callback
     * @return Call
     * @route /task/list/{id-employee}
     */
    public static Call getEmployeAssignee(String idEmploye, Callback callback) {
        return get("task/list/" + idEmploye, callback);
    }

    public static Call getEmployeeTasks(String path, String idEmployee, Callback callback) {
        Log.d(TAG + "APIController", "getEmployeeTasks: " + path + idEmployee);
        return get("waste/list" + path + idEmployee, callback);
    }

    /* Waste */

    /**
     * Report a waste (POST method)
     *
     * @param waste    Waste
     * @param callback Callback
     * @return Call
     * {
     * "name": "string",
     * "type": "WasteType",
     * "description": "string",
     * "userReporterId": "string",
     * "address": "string",
     * "latitude": 0,
     * "longitude": 0,
     * "image": "string"
     * }
     * @route /waste/report
     */
    public static Call reportWaste(WasteDTO waste, Callback callback) {
        String json = "{\n" +
                "  \"name\": \"" + waste.getName() + "\",\n" +
                "  \"type\": \"" + waste.getType() + "\",\n" +
                "  \"description\": \"" + waste.getDescription() + "\",\n" +
                "  \"userReporterId\": \"" + waste.getUserReporterId() + "\",\n" +
                "  \"address\": \"" + waste.getAddress() + "\",\n" +
                "  \"latitude\": " + waste.getLatitude() + ",\n" +
                "  \"longitude\": " + waste.getLongitude() + "\n" +
                "}";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", waste.getName(),
                        RequestBody.create(MediaType.parse("image/*"), waste.getImageData()))
                .addFormDataPart("waste", "json",
                        RequestBody.create(MediaType.parse("application/json"), json))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "waste/report")
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    /**
     * Get a waste by its id (GET method)
     *
     * @param idWaste  String
     * @param callback Callback
     * @return Call
     * @route /waste/{id}
     */
    public static Call getWaste(String idWaste, Callback callback) {
        return get("waste/" + idWaste, callback);
    }

    /**
     * Delete a waste by its id (DELETE method)
     *
     * @param idWaste  String
     * @param callback Callback
     * @return Call
     * @route /waste/{id}
     */
    public static Call deleteWaste(String idWaste, Callback callback) {
        return delete("waste/" + idWaste, callback);
    }

    /**
     * Get all waste type (GET method)
     *
     * @param callback Callback
     * @return Call
     * @route /waste/type/all
     */
    public static Call getWasteType(Callback callback) {
        return get("waste/type/all", callback);
    }

    /**
     * Get all waste reported (GET method)
     *
     * @param callback Callback
     * @return Call
     * @route /waste/all
     */
    public static Call getWasteList(Callback callback) {
        return get("waste/all", callback);
    }

    /* Announcement */

    /**
     * Create an announcement of type NEWS (POST method)
     *
     * @param callback Callback
     * @return Call
     * @route /announcement/news
     */
    public static Call createAnnouncementNews(String title, String description, Callback callback) {
        String json = "{\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"description\": \"" + description + "\"\n" +
                "}";
        Log.d(TAG + "APIController", json);
        return post("announcement/news", json, callback);
    }

    /**
     * Create an announcement of type EVENT (POST method)
     *
     * @param callback Callback
     * @return Call
     * @route /announcement/events
     */
    public static Call createAnnouncementEvent(String title, String description, String eventDate, Callback callback) {
        String json = "{\n" +
                "  \"title\": \"" + title + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "  \"eventDate\": \"" + eventDate + "\"\n" +
                "}";
        Log.d(TAG + "APIController", json);
        return post("announcement/event", json, callback);
    }

    /**
     * Get all announcement (GET method)
     *
     * @param callback Callback
     * @return Call
     * @route /announcement/type/all
     */
    public static Call getAnnouncementType(Callback callback) {
        return get("announcement/type/all", callback);
    }

    /**
     * Get all announcement (GET method)
     *
     * @param callback Callback
     * @return Call
     * @route /announcement/all
     */
    public static Call getAllAnnouncement(Callback callback) {
        return get("announcement/all", callback);
    }

    /**
     * Delete an announcement by its id (DELETE method)
     *
     * @param idAnnouncement String
     * @param callback       Callback
     * @return Call
     * @route /announcement/{id}
     */
    public static Call deleteAnnouncement(String idAnnouncement, Callback callback) {
        return delete("announcement/" + idAnnouncement, callback);
    }

    /* User */

    /**
     * Get all user by their role (GET method)
     *
     * @param roles    The Role list users need
     * @param callback Callback
     * @return Call
     * @route /user/{role}/all
     */
    public static Call getUserByRoles(List<Role> roles, Callback callback) {
        StringBuilder sb = new StringBuilder("user/filterByRole?");
        for (int index = 0; index < roles.size(); index++) {
            sb.append("roles=");
            sb.append(roles.get(index));
            if (index < roles.size() - 1) {
                sb.append("&");
            }
        }
        return get(sb.toString(), callback);
    }

    /**
     * Get a user by its id (GET method)
     *
     * @param idUser   String
     * @param callback Callback
     * @return Call
     * @route /user/{id}
     */
    public static Call getUser(String idUser, Callback callback) {
        return get("user/" + idUser, callback);
    }

    /**
     * Get all roles (GET method)
     *
     * @param callback Callback
     * @return Call
     * @route /user/role/all
     */
    public static Call getAllRole(Callback callback) {
        return get("user/role/all", callback);
    }

    public static List<User> parseUsers(String body) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        return gson.fromJson(body, type);
    }

    public static List<Waste> parseWastes(String body) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Waste>>() {
        }.getType();
        List<Waste> wasteList = gson.fromJson(body, type);
        return wasteList;
    }
}
