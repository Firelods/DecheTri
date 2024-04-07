package etu.seinksansdoozebank.dechetri.model.taskslist;

import java.util.ArrayList;

public class TaskList extends ArrayList<Task> {
    public TaskList() {
        super();
        this.add(new Task("Task 1", "Waste type 1", "Address 1"));
        this.add(new Task("Task 2", "Waste type 2", "Address 2"));
        this.add(new Task("Task 3", "Waste type 3", "Address 3"));
        this.add(new Task("Task 4", "Waste type 4", "Address 4"));
        this.add(new Task("Task 5", "Waste type 5", "Address 5"));
    }
}
