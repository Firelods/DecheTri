package etu.seinksansdoozebank.dechetri.model.taskslist;

public class Task {
    private String title;
    private String wasteType;
    private String address;

    public Task(String title, String wasteType, String address) {
        this.title = title;
        this.wasteType = wasteType;
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String title) {
        this.title = title;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
