package at.htlbraunau.notenmanagement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Konrad on 18.11.2017.
 */

public class EarlyWarning implements Parcelable {
    private String subject;
    private String dateOfinsert;

    public EarlyWarning(String subject, String dateOfinsert){
        this.subject = subject;
        this.dateOfinsert = dateOfinsert;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDateOfinsert() {
        return dateOfinsert;
    }

    public void setDateOfinsert(String dateOfinsert) {
        this.dateOfinsert = dateOfinsert;
    }

    public EarlyWarning(Parcel in){
        String[] data = new String[2];

        in.readStringArray(data);

        this.subject = data[0];
        this.dateOfinsert = data[1];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.subject,
                this.dateOfinsert});
    }
    public static final Creator CREATOR = new Creator() {
        public EarlyWarning createFromParcel(Parcel in) {
            return new EarlyWarning(in);
        }

        public EarlyWarning[] newArray(int size) {
            return new EarlyWarning[size];
        }
    };
}
