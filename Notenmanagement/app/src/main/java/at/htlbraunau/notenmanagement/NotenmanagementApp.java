package at.htlbraunau.notenmanagement;

import android.app.Application;

/**
 * Created by Konrad on 26.09.2017.
 */

public class NotenmanagementApp extends Application{
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
