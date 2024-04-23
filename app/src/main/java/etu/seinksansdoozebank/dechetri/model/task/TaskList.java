package etu.seinksansdoozebank.dechetri.model.task;

import java.util.ArrayList;

public class TaskList extends ArrayList<Task> {
    public TaskList() {
        super();
        this.add(new Task("Task 1", "User 1", "Address 1"));
        this.add(new Task("Task 2", "User 2", "Address 2"));
        this.add(new Task("Task 3", "User 3", "Address 3"));
        this.add(new Task("Task 4", "User 4", "Address 4"));
        this.add(new Task("Task 5", "User 5", "Address 5"));
    }
}
