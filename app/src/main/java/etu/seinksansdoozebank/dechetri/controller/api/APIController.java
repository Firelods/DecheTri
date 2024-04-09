package etu.seinksansdoozebank.dechetri.controller.api;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementType;
import etu.seinksansdoozebank.dechetri.model.task.Task;
import etu.seinksansdoozebank.dechetri.model.user.Role;
import etu.seinksansdoozebank.dechetri.model.user.User;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteType;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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


    /* task */

    /**
     * Get all tasks (PUT method)
     *
     * @param task Task
     * @route /task/assign
     */
    public void assignTask(Task task) {
        // Convert the task object to a JSON string
        Gson gson = new Gson();
        String taskJson = gson.toJson(task);

        // Create a RequestBody with the JSON string
        RequestBody requestBody = RequestBody.create(taskJson, JSON);

        Log.d("APIController", "Request body: " + taskJson);

        // Build the Request object
        Request request = new Request.Builder()
                .url(baseUrl + "/task/assign")
                .put(requestBody)
                .build();

        // Execute the request and handle the response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // Handle the successful response here
            Log.d("APIController", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mark a task as completed (PATCH method)
     *
     * @param task Task
     * @route /task/complete/{id-task}
     */
    public void completeTask(Task task) {

    }

    /**
     * Get all tasks assigned to an employee (GET method)
     *
     * @param idEmploye String
     * @return List<Task>
     * @route /task/list/{id-employee}
     */
    public List<User> getEmployeAssignee(String idEmploye) {
        return null;
    }

    /* Waste */

    /**
     * Report a waste (POST method)
     *
     * @param waste Waste
     * @route /waste/report
     */
    public void reportWaste(Waste waste) {

    }

    /**
     * Get a waste by its id (GET method)
     *
     * @param idWaste String
     * @route /waste/{id}
     */
    public void getWaste(String idWaste) {

    }

    /**
     * Delete a waste by its id (DELETE method)
     *
     * @param idWaste String
     * @route /waste/{id}
     */
    public void deleteWaste(String idWaste) {

    }

    /**
     * Delete a waste by its id (DELETE method)
     *
     * @route /waste/type/all
     */
    public void getWasteType() {

    }

    /**
     * Get all waste reported (GET method)
     *
     * @return List<Waste>
     * @route /waste/all
     */
    public List<WasteType> getWasteList() {
        return null;
    }

    /* Announcement */

    /**
     * Get all announcement news (GET method)
     * @route /announcement/news
     * @return List<Announcement>
     */
    public List<Announcement> getAnnouncementNews() {
        return null;
    }

    /**
     * Get all announcement events (GET method)
     * @route /announcement/events
     * @return List<Announcement>
     */
    public List<Announcement> getAnnouncementEvents() {
        return null;
    }

    /**
     * Get all announcement news (GET method)
     * @route /announcement/type/all
     * @return List<Announcement>
     */
    public List<AnnouncementType> getAnnouncementType() {
        return null;
    }

    /**
     * Create an announcement (POST method)
     * @param idAnnouncement
     * @route /announcement/{id}
     */
    public void deleteAnnouncement(String idAnnouncement) {

    }

    /* User */

    /**
     * Get all users (GET method)
     * @param role Role
     * @route /user/{role}/all
     * @return List<User>
     */
    public List<User> getUserByRole(Role role) {
        return null;
    }

    /**
     * Get a user by its id (GET method)
     * @param idUser String
     * @route /user/{id}
     * @return User
     */
    public User getUser(String idUser) {
        return null;
    }

    /**
     * Create a user (POST method)
     * @param role Role
     * @route /user/role/all
     */
    public List<Role> getAllRole(Role role) {
        return null;
    }
}
