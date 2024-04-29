package etu.seinksansdoozebank.dechetri.model.waste;

import java.util.Date;

public class Waste {
    //Waste
    private String id;
    private String name;
    private WasteType type;
    private String description;
    private byte[] imageData;
    //report
    private Date reportDate;
    private String address;
    private double latitude;
    private double longitude;
    private String userReporterId;

    public Waste(String id, String name, WasteType type, String description, byte[] imageData, Date reportDate, String address, double latitude, double longitude, String userReporterId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imageData = imageData;
        this.reportDate = reportDate;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userReporterId = userReporterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WasteType getType() {
        return type;
    }

    public void setType(WasteType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserReporterId() {
        return userReporterId;
    }

    public void setUserReporterId(String userReporterId) {
        this.userReporterId = userReporterId;
    }
}
