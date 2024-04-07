package etu.seinksansdoozebank.dechetri.ui.wastemap;

import android.graphics.Bitmap;
import android.net.Uri;

public class Waste {
    private final String name;
    private final double latitude;
    private final double longitude;

    private final String address;

    private final Uri photo;

    public Waste(String name, double latitude, double longitude, String address, Uri photo) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.photo=photo;
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
