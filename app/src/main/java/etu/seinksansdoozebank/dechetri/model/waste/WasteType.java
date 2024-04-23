package etu.seinksansdoozebank.dechetri.model.waste;

public enum WasteType {
    HOUSEHOLD("Ménager"),
    INDUSTRIAL("Industriel"),
    HAZARDOUS("Dangereux"),
    GREEN("Vert"),
    OTHER("Autre");

    private final String name;

    WasteType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
