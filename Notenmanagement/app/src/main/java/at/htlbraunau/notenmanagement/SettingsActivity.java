package at.htlbraunau.notenmanagement;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Base64;

import com.google.firebase.messaging.FirebaseMessaging;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.htlbraunau.notenmanagement.R;

public class SettingsActivity extends MenuOptionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Fragment fragment  =  new SettingsFragment();
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();

        if(savedInstanceState == null){
            fragmentTransaction.add(R.id.ll_settings, fragment, getResources().getString(R.string.settings_fragment_tag));
            fragmentTransaction.commit();
        }
        else{
            fragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.settings_fragment_tag));
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {

        CheckBoxPreference prefPush;
        SwitchPreference prefVibration;
        SwitchPreference prefSound;
        SwitchPreference prefWakeup;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.addPreferencesFromResource(R.xml.settings);

            prefPush  =(CheckBoxPreference)findPreference(getResources().getString(R.string.checkPush_key));
            prefVibration = (SwitchPreference) findPreference(getResources().getString(R.string.swVibration_key));
            prefSound = (SwitchPreference) findPreference(getResources().getString(R.string.swSound_key));
            prefWakeup = (SwitchPreference) findPreference(getResources().getString(R.string.swWakeup_key));


            if(prefPush.isChecked()){
                prefPush.setIcon(R.mipmap.ic_notification_on);
                prefVibration.setEnabled(true);
                prefSound.setEnabled(true);
                prefWakeup.setEnabled(true);
            }
            else{
                prefPush.setIcon(R.mipmap.ic_notification_off);
                prefVibration.setEnabled(false);
                prefSound.setEnabled(false);
                prefWakeup.setEnabled(false);
            }

            NotenmanagementSingleton.setContext(this.getActivity());
            NotenmanagementSingleton.setCurrentAcitivity(this.getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if(key.equals(getString(R.string.checkPush_key))) {

                this.prefPush.setIcon(getResources().getDrawable(R.mipmap.ic_notification_on));

                User user = NotenmanagementSingleton.getUser();

                String userID = user.getRole() + "_" + user.className + "_" + user.matrikelNr;

                //BASE URL
                String url = getResources().getString(R.string.NOTENMANAGEMENT_SERVER_BASE_URL);

                //Abfrage ob Live-Datenbank oder Work-Datenbank verwendet wird (für Topicnamen des FCM)
                if(url.contains("work")){
                    StringBuffer sb = new StringBuffer(userID).append("_Work");

                    userID = sb.toString();
                }

                String topic = null;

                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA256");

                    byte[] hash = digest.digest(userID.getBytes(StandardCharsets.UTF_8));

                    topic = Base64.encodeToString(hash, Base64.DEFAULT).trim();

                    StringBuffer sb = new StringBuffer();
                    for(char c : topic.toCharArray()) {
                        if(Character.isLetterOrDigit(c)) {
                            sb.append(c);
                        }
                        else {
                            sb.append("_");
                        }
                    }

                    topic = sb.toString();

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (sharedPreferences.getBoolean(key, false)) {

                    prefVibration.setEnabled(true);

                    prefSound.setEnabled(true);

                    prefWakeup.setEnabled(true);

                    FirebaseMessaging.getInstance().subscribeToTopic(topic);

                /*
                // Token Refresh anfordern
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                            FirebaseInstanceId.getInstance().getToken();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                */
                }
                else {

                    this.prefPush.setIcon(getResources().getDrawable(R.mipmap.ic_notification_off));

                    this.prefVibration.setChecked(false);
                    this.prefSound.setChecked(false);
                    this.prefWakeup.setChecked(false);

                    this.prefVibration.setEnabled(false);
                    this.prefSound.setEnabled(false);
                    this.prefWakeup.setEnabled(false);

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                }
            }
        }
    }
}
