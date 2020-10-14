package at.htlbraunau.notenmanagement;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Konrad on 03.10.2017.
 */

public class NotenmanagementSingleton {

    private static User user;
    private static NotenmanagementSingleton instance = null;
    private static Context context;
    private static Activity currentAcitivity;
    private static String InstanceID;
    private static int widthOfDevice;
    private static int heightOfDevice;

    private NotenmanagementSingleton(){}

    public static synchronized NotenmanagementSingleton getInstance(){
        if(instance == null){
            instance = new NotenmanagementSingleton();
        }
        return instance;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        NotenmanagementSingleton.user = user;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        NotenmanagementSingleton.context = context;
    }

    public static String getInstanceID() {
        return InstanceID;
    }

    public static void setInstanceID(String intanceID) {
        InstanceID = intanceID;
    }

    public static Activity getCurrentAcitivity() {
        return currentAcitivity;
    }

    public static void setCurrentAcitivity(Activity currentAcitivity) {
        NotenmanagementSingleton.currentAcitivity = currentAcitivity;
    }

    public  static int getWidthOfDevice() {
        return widthOfDevice;
    }

    public static void setWidthOfDevice(int widthOfDevice) {
        NotenmanagementSingleton.widthOfDevice = widthOfDevice;
    }

    public  static int getHeightOfDevice() {
        return heightOfDevice;
    }

    public static void setHeightOfDevice(int heightOfDevice) {
        NotenmanagementSingleton.heightOfDevice = heightOfDevice;
    }
}
