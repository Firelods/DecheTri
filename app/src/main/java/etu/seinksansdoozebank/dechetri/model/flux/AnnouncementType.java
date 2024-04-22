package etu.seinksansdoozebank.dechetri.model.flux;

public enum AnnouncementType {
    NEWS("actualité"),
    EVENT("événement");

    private final String name;

    AnnouncementType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
