package etu.seinksansdoozebank.dechetri.model.waste;

import etu.seinksansdoozebank.dechetri.R;

public enum WasteType {
    HOUSEHOLD("Ménager", R.drawable.baseline_cabin_24),
    INDUSTRIAL("Industriel", R.drawable.baseline_factory_24),
    HAZARDOUS("Dangereux", R.drawable.baseline_warning_amber_24),
    GREEN("Vert", R.drawable.baseline_grass_24),
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
