package at.htlbraunau.notenmanagement;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import at.htlbraunau.notenmanagement.R;

/**
 * Created by Konrad on 14.11.2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NEW_ASSESSMENT_NOTIFICATION = 100;
    private static final int DURATION_OF_VIBRATION = 400;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Notenmanagement", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Notenmanagement", "Message data payload: " + remoteMessage.getData());

            String message = remoteMessage.getData().get("message");

            SharedPreferences defaultpreferences = PreferenceManager.getDefaultSharedPreferences(this);


            Intent intent = new Intent(this, LoginActivity.class);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            0
                    );

            NotificationCompat.Builder bob  = new NotificationCompat.Builder(this);
                                       bob
                                       .setSmallIcon(R.mipmap.notenmanagement_logo_small_icon)
                                       .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notenmanagament_logo_new))
                                       .setContentTitle(getString(R.string.newAssessmnet))
                                       .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                       .setContentText(message)
                                       .setContentIntent(pendingIntent)
                                       .setAutoCancel(true);

            if(defaultpreferences.getBoolean(getResources().getString(R.string.swSound_key),false)){

                 Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                bob.setSound(defaultSoundUri);
            }

            if(defaultpreferences.getBoolean(getResources().getString(R.string.swVibration_key), false)){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                v.vibrate(DURATION_OF_VIBRATION);
            }

            if(defaultpreferences.getBoolean(getResources().getString(R.string.swWakeup_key), false)){

                PowerManager.WakeLock screenOn = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");

                screenOn.acquire();
                screenOn.release();
            }


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NEW_ASSESSMENT_NOTIFICATION, bob.build());



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("Notenmanagement", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
