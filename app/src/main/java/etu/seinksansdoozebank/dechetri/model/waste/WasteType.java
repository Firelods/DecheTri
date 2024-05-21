package etu.seinksansdoozebank.dechetri.model.waste;

import etu.seinksansdoozebank.dechetri.R;

public enum WasteType {
    HOUSEHOLD("Ménager", R.drawable.waste),
    INDUSTRIAL("Industriel", R.drawable.waste),
    HAZARDOUS("Dangereux", R.drawable.waste),
    GREEN("Vert", R.drawable.waste),
    OTHER("Autre", R.drawable.waste);

    private final String name;
    private final int icon;

    WasteType(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }
    public int getIcon() {
        return icon;
    }
}
