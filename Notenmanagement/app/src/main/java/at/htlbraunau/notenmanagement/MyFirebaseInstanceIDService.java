package at.htlbraunau.notenmanagement;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Konrad on 14.11.2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        /*
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Notenmanagement", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        // Wenn Benachrichtigungen gewünscht sind -> Token an den Server senden
        SharedPreferences defaultpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(defaultpreferences.getBoolean("pushnotification", false)) {
            NotenmanagementServerAPI.sendRegistrationToServer(refreshedToken);
        }
        */
    }

    private void sendRegistrationToServer(String refreshedToken) {

        // GET baseURL + "/api/notificationdevices/{guid}

        // POST baseURL + "/api/notificationdevices
        // PUT baseURL + "/api/notificationdevices/{guid}
        /*
        {
            Device_ID (String) <- guid
            Push_ID (String) <- refreshedToken
            Matrikelnummer <- falls Schüler
            Lehrer_ID <- falls Lehrer
            NotificationPlatform <- "FCM"
        }

        */
        NotenmanagementServerAPI.sendRegistrationToServerFCM(refreshedToken);
    }
}
