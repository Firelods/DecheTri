package etu.seinksansdoozebank.dechetri.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String id;
    private String name;
    private Role role;
    private static int idCounter = 1;
    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        String roleString = in.readString();
        if (roleString != null) {
            role = Role.fromString(roleString);
        } else {
            role = null;
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

    @Override
    public int describeContents() {
        return id.hashCode();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(role.name());
    }
}
