package etu.seinksansdoozebank.dechetri.model.waste;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.util.Date;

import etu.seinksansdoozebank.dechetri.model.user.User;

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
    private User assignee;

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
        assignee = in.readParcelable(User.class.getClassLoader());
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

    public String getImageDataBase64() {
        return imageData;
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

    public Date getReportDate() {
        return reportDate;
    }

    @Override
    public int describeContents() {
        return Integer.parseInt(id);
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User user) {
        this.assignee = user;
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
        parcel.writeParcelable(assignee, PARCELABLE_WRITE_RETURN_VALUE);
    }

    @Override
    public String toString() {
        return "Waste{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", imageData=" + this.imageData.substring(0, 50) +
                ", reportDate=" + reportDate +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userReporterId='" + userReporterId + '\'' +
                ", assignee='" + assignee + '\'' +
                '}';
    }
}
