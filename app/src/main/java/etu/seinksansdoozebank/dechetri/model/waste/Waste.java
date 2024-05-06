package etu.seinksansdoozebank.dechetri.model.waste;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Date;

public class Waste implements Parcelable {
    //Waste
    private String id;
    private String name;
    private WasteType type;
    private String description;
    private String imageData;
    //report
    private Date reportDate;
    private String address;
    private double latitude;
    private double longitude;
    private String userReporterId;

    public Waste(String id, String name, WasteType type, String description, String imageData, Date reportDate, String address, double latitude, double longitude, String userReporterId) {
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

    public Waste(String id, String name, WasteType type, String description, byte[] imageData, Date reportDate, String address, double latitude, double longitude, String userReporterId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imageData = Base64.encodeToString(imageData, Base64.DEFAULT);
        this.reportDate = reportDate;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userReporterId = userReporterId;
    }

    protected Waste(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imageData = Base64.encodeToString(in.readBlob(), Base64.DEFAULT);
        }
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        userReporterId = in.readString();
    }

    public static final Creator<Waste> CREATOR = new Creator<Waste>() {
        @Override
        public Waste createFromParcel(Parcel in) {
            return new Waste(in);
        }

        @Override
        public Waste[] newArray(int size) {
            return new Waste[size];
        }
    };

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

    @Override
    public int describeContents() {
        return Integer.parseInt(id);
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.writeBlob(this.getImageData());
        }
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(userReporterId);
    }

    @Override
    public String toString() {
        return "Waste{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", imageData=" + this.imageData +
                ", reportDate=" + reportDate +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userReporterId='" + userReporterId + '\'' +
                '}';
    }
}
