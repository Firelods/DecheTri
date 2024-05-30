package etu.seinksansdoozebank.dechetri.model.waste;

import android.util.Base64;

public class WasteDTO {
    private final String name;
    private final WasteType type;
    private final String description;
    private final String imageData;
    private final String address;
    private final double latitude;
    private final double longitude;
    private final String userReporterId;

    public WasteDTO(String name, WasteType wasteType, String description, byte[] imageData, String address, double latitude, double longitude, String userReporterID) {
        this.name = name;
        this.type = wasteType;
        this.description = description;
        this.imageData = Base64.encodeToString(imageData, Base64.DEFAULT);
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userReporterId = userReporterID;
    }

    public String getName() {
        return name;
    }

    public WasteType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImageData() {
        return Base64.decode(imageData, Base64.DEFAULT);
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUserReporterId() {
        return userReporterId;
    }
}
