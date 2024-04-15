package etu.seinksansdoozebank.dechetri.controller.api;

import android.util.Log;

import java.util.List;

import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementType;
import etu.seinksansdoozebank.dechetri.model.user.Role;
import etu.seinksansdoozebank.dechetri.model.user.User;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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
        Log.d(TAG + "APIController", "put: " + call);
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
        Log.d(TAG + "APIController", "post: " + call);
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
        Log.d(TAG + "APIController", "get: " + call);
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
        Log.d(TAG + "APIController", "patch: " + call);
        return call;
    }


    /* task */

    /**
     * Get all tasks (PUT method)
     *
     * @return List<Task>
     * @route /task/assign
     * {
     * "idAssignee": "string",
     * "idWasteToCollect": "string"
     * }
     */
    public static Call assignTask(String wasteId, String employeeId, Callback callback) {
        String json = "{\n" +
                "  \"idAssignee\": \"" + employeeId + "\",\n" +
                "  \"idWasteToCollect\": \"" + wasteId + "\"\n" +
                "}";
        Log.d(TAG + "APIController", "assignTask: " + json);
        return put("task/assign", json, callback);
    }

    /**
     * Mark a task as completed (PATCH method)
     *
     * @param idTask String
     * @route /task/complete/{id-task}
     * {
     * "id-task": "string"
     * }
     */
    public static Call completeTask(String idTask, Callback callback) {
        String json = "{\n" +
                "  \"id-task\": \"" + idTask + "\"\n" +
                "}";
        return post("task/complete/" + idTask, json, callback);
    }

    /**
     * Get all tasks assigned to an employee (GET method)
     *
     * @param idEmploye String
     * @return List<Task>
     * @route /task/list/{id-employee}
     */
    public static Call getEmployeAssignee(String idEmploye, Callback callback) {
        return get("task/list/" + idEmploye, callback);
    }

    /* Waste */

    /**
     * Report a waste (POST method)
     *
     * @param waste Waste
     * @route /waste/report
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
     */
    public static Call reportWaste(Waste waste, Callback callback) {
        String imageBase64 = android.util.Base64.encodeToString(waste.getImageData(), android.util.Base64.DEFAULT);
        Log.d(TAG + "APIController", "reportWaste: " + imageBase64);
        String json = "{\n" +
                "  \"name\": \"" + waste.getName() + "\",\n" +
                "  \"type\": \"" + waste.getType() + "\",\n" +
                "  \"description\": \"" + waste.getDescription() + "\",\n" +
                "  \"userReporterId\": \"" + waste.getUserReporterId() + "\",\n" +
                "  \"address\": \"" + waste.getAddress() + "\",\n" +
                "  \"latitude\": " + waste.getLatitude() + ",\n" +
                "  \"longitude\": " + waste.getLongitude() + ",\n" +
                "  \"image\": \"" + imageBase64 + "\"\n" +
                "}";
        return post("waste/report", json, callback);
    }

    /**
     * Get a waste by its id (GET method)
     *
     * @param idWaste String
     * @route /waste/{id}
     */
    public static Call getWaste(String idWaste, Callback callback) {
        return get("waste/" + idWaste, callback);
    }

    /**
     * Delete a waste by its id (DELETE method)
     *
     * @param idWaste String
     * @param callback Callback
     * @route /waste/{id}
     */
    public static Call deleteWaste(String idWaste, Callback callback) {
        return delete("waste/" + idWaste, callback);
    }

    /**
     * Delete a waste by its id (DELETE method)
     *
     * @param callback Callback
     * @route /waste/type/all
     */
    public static Call getWasteType(Callback callback) {
        return get("waste/type/all", callback);
    }

    /**
     * Get all waste reported (GET method)
     *
     * @param callback Callback
     * @return List<Waste>
     * @route /waste/all
     */
    public static Call getWasteList(Callback callback) {
        return get("waste/all", callback);
    }

    /* Announcement */

    /**
     * Get all announcement news (GET method)
     *
     * @param callback Callback
     * @return List<Announcement>
     * @route /announcement/news
     */
    public static Call getAnnouncementNews(Callback callback) {
        return get("announcement/news", callback);
    }

    /**
     * Get all announcement events (GET method)
     *
     * @param callback Callback
     * @return List<Announcement>
     * @route /announcement/events
     */
    public static Call getAnnouncementEvents(Callback callback) {
        return get("announcement/events", callback);
    }

    /**
     * Get all announcement news (GET method)
     *
     * @param callback Callback
     * @route /announcement/type/all
     * @return List<Announcement>
     */
    public static Call getAnnouncementType(Callback callback) {
        return get("announcement/type/all", callback);
    }

    /**
     * Get all announcement (GET method)
     *
     * @param callback Callback
     * @route /announcement/all
     * @return
     */
    public static Call getAnnouncement(Callback callback) {
        return get("announcement/all", callback);
    }

    /**
     * Create an announcement (POST method)
     *
     * @param idAnnouncement
     * @route /announcement/{id}
     */
    public static Call deleteAnnouncement(String idAnnouncement, Callback callback) {
        return delete("announcement/" + idAnnouncement, callback);
    }

    /* User */

    /**
     * Get all users (GET method)
     *
     * @param role Role
     * @return List<User>
     * @route /user/{role}/all
     */
    public List<User> getUserByRole(Role role) {
        return null;
    }

    /**
     * Get a user by its id (GET method)
     *
     * @param idUser String
     * @return User
     * @route /user/{id}
     */
    public User getUser(String idUser) {
        return null;
    }

    /**
     * Create a user (POST method)
     *
     * @param role Role
     * @route /user/role/all
     */
    public List<Role> getAllRole(Role role) {
        return null;
    }
}
