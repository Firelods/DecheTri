package etu.seinksansdoozebank.dechetri.model.task;

public class Task {
    private String id;
    private String idAssignee;
    private String idWasteToCollect;
    private boolean completed;
    public Task(String id, String idAssignee, String idWasteToCollect) {
        this.id = id;
        this.idAssignee = idAssignee;
        this.idWasteToCollect = idWasteToCollect;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAssignee() {
        return idAssignee;
    }

    public void setIdAssignee(String idAssignee) {
        this.idAssignee = idAssignee;
    }

    public String getIdWasteToCollect() {
        return idWasteToCollect;
    }

    public void setIdWasteToCollect(String idWasteToCollect) {
        this.idWasteToCollect = idWasteToCollect;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
