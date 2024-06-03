package etu.seinksansdoozebank.dechetri.model.waste;

import java.util.Arrays;

import etu.seinksansdoozebank.dechetri.R;

public enum WasteType {
    OTHER("Autre", R.drawable.waste),
    HOUSEHOLD("Ménager", R.drawable.waste),
    INDUSTRIAL("Industriel", R.drawable.waste),
    HAZARDOUS("Dangereux", R.drawable.waste),
    GREEN("Vert", R.drawable.waste);

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

    public static String[] getNames() {
        return Arrays.stream(WasteType.values()).map(WasteType::getName).toArray(String[]::new);
    }
}
