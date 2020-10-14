package at.htlbraunau.notenmanagement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Konrad on 25.09.2017.
 */

public class User implements Parcelable {

    private String role;
    public String className;
    public String matrikelNr;
    public String userName;
    public String access_token;

    public User(String role, String className, String matrikelNr, String userName, String access_token){
        this.role = role;
        this.className = className;
        this.matrikelNr = matrikelNr;
        this.userName = userName;
        this.access_token = access_token;
    }

    public User(Parcel parcel){
        this.role = parcel.readString();
        this.className = parcel.readString();
        this.matrikelNr = parcel.readString();
        this.userName = parcel.readString();
        this.access_token = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.role);
        parcel.writeString(this.className);
        parcel.writeString(this.matrikelNr);
        parcel.writeString(this.userName);
        parcel.writeString(this.access_token);
    }

    public static final Creator<User> CREATOR =
            new Creator<User>(){

                @Override
                public User createFromParcel(Parcel source) {
                    return new User(source);
                }

                @Override
                public User[] newArray(int size) {
                    return new User[size];
                }
            };

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
