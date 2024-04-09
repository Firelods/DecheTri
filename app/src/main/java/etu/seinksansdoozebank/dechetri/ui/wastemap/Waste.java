package etu.seinksansdoozebank.dechetri.ui.wastemap;

public class Waste {
    private final String name;
    private final double latitude;
    private final double longitude;

    private final String address;

    public Waste(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }
}
