package etu.seinksansdoozebank.dechetri.model.flux;

public class Flux {

    private String title;
    private String date;
    private String description;
    private static final String APP_NAME = "DecheTri";
    public Flux(String title, String date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppName() {
        return APP_NAME;
    }
}
