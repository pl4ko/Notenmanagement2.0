package at.htlbraunau.notenmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.UUID;

import at.htlbraunau.notenmanagement.R;

public class LoginActivity extends AppCompatActivity {

    private static final int WAITED_20_MIN = 2222;
    private static boolean TOKEN_REFRESHED = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.setTitle(getResources().getString(R.string.app_name_long));
        }
        else{
            this.setTitle(getResources().getString(R.string.app_name_short));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotenmanagementSingleton.getInstance().setCurrentAcitivity(this);
        NotenmanagementSingleton.getInstance().setContext(this);

        setContentView(R.layout.activity_login);
        final EditText edtUser = (EditText)findViewById(R.id.edt_user);
        final EditText edtPassword = (EditText)findViewById(R.id.edt_password);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.setTitle(getResources().getString(R.string.app_name_long));
        }
        else{
            this.setTitle(getResources().getString(R.string.app_name_short));
        }

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edtPassword.setBackgroundResource(R.drawable.border_white_background);
            }
        });

        edtUser.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edtUser.setBackgroundResource(R.drawable.border_white_background);
            }
        });

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);

        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Prüfen, ob GUID vorhanden -> wenn ja, verwenden, wenn nein erstellen
        if(sharedPreferences.getString("InstanceID","") == null) {
            String guid = UUID.randomUUID().toString();

            editor.putString("InstanceID", guid);
            editor.commit();
        }

        NotenmanagementSingleton.setInstanceID(sharedPreferences.getString("InstanceID", ""));

        if(sharedPreferences.contains("username"))
        {
           final EditText user = (EditText)findViewById(R.id.edt_user);
            user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    user.setBackgroundResource(R.drawable.border_white_background);
                }
            });
            user.setText(sharedPreferences.getString("username", ""));
        }

        if(sharedPreferences.contains("password"))
        {
            edtPassword.setText(sharedPreferences.getString("password", ""));
        }

        if(sharedPreferences.contains("switch"))
        {
            Switch sw = (Switch)findViewById(R.id.sw_remember_data);
            sw.setChecked(sharedPreferences.getBoolean("switch", false));
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);

        if(sharedPreferences.contains("username"))
        {
            EditText edt = (EditText)findViewById(R.id.edt_user);
            edt.setText(sharedPreferences.getString("username", ""));
        }
        else{
            EditText edtUser = (EditText)findViewById(R.id.edt_user);
            edtUser.setText("");
        }

        if(sharedPreferences.contains("password"))
        {
            EditText edt = (EditText)findViewById(R.id.edt_password);
            edt.setText(sharedPreferences.getString("password", ""));
        }
        else {
            EditText edtPassword = (EditText)findViewById(R.id.edt_password);
            edtPassword.setText("");
        }

}

    public int getSreenWidth(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        else{
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }
    }

    public int getScreenHeight(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            int value = Resources.getSystem().getDisplayMetrics().heightPixels;

            return value;
        }
        else{
            int value = Resources.getSystem().getDisplayMetrics().widthPixels;

            return value;
        }
    }


    public void onClickedLogin(View v) {
        String user = ((EditText) findViewById(R.id.edt_user)).getText().toString();
        String password = ((EditText) findViewById(R.id.edt_password)).getText().toString();

        NotenmanagementSingleton.getInstance().setWidthOfDevice(getSreenWidth());
        NotenmanagementSingleton.getInstance().setHeightOfDevice(getScreenHeight());

        final Button b = (Button)findViewById(R.id.btn_login);

        //Nach ersten Click wird Button disabled
        b.setEnabled(false);

        //Progressbar sichtbar machen
        ProgressBar loading = (ProgressBar)findViewById(R.id.prgBar_login);
        loading.setVisibility(View.VISIBLE);


        Switch sw =(Switch)findViewById(R.id.sw_remember_data);

        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //Überprüfen ob "Login merken" -Switch eingeschalte ist --> um sich Logindaten zu merken
        if(sw.isChecked()) {
            editor.putString("username", user);
            editor.putString("password", password);
            editor.putBoolean("switch", true);
            editor.commit();
        }
        else{
            editor.clear();
            editor.commit();
        }

        //Knopfdruckanimation
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        v.startAnimation(animation1);

        //Request für Login
        NotenmanagementServerAPI.login(user, password, handler);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Button b = (Button)findViewById(R.id.btn_login);

            switch (msg.what) {
                case NotenmanagementServerAPI.LOGIN_SUCCESFULL:
                    ProgressBar loading = (ProgressBar)findViewById(R.id.prgBar_login);
                    loading.setVisibility(View.INVISIBLE);

                    //Button wieder aktivieren
                    b.setEnabled(true);

                    User user = (User) msg.obj;
                    NotenmanagementSingleton.getInstance().setUser(user);

                    /*Wenn der Token automatisch refresht worden ist --> Activity */
                    /* Wenn TOKEN_REFRESHED false ist --> normale Login: LoginActivity -> Navigation zu HomeActivity */
                    if(!LoginActivity.TOKEN_REFRESHED){
                        Intent homePupil = new Intent(LoginActivity.this, HomePupilActivity.class);
                        startActivity(homePupil);
                    }

                    startTimer();
                    break;

                case NotenmanagementServerAPI.LOGIN_FAILED:
                    loading = (ProgressBar)findViewById(R.id.prgBar_login);
                    loading.setVisibility(View.INVISIBLE);

                    b.setEnabled(true);

                    EditText edtUser = (EditText) findViewById(R.id.edt_user);
                    EditText edtPassword = (EditText) findViewById(R.id.edt_password);

                    //Setzen der Hintergrundfarbe auf rot der Eingabefelder
                    edtUser.setBackgroundResource(R.drawable.border_red_backround);
                    edtPassword.setBackgroundResource(R.drawable.border_red_backround);

                    Toast.makeText(LoginActivity.this, R.string.toast_login_failed, Toast.LENGTH_SHORT).show();
                    break;

                case LoginActivity.WAITED_20_MIN:
                    SharedPreferences preferences = LoginActivity.this.getPreferences(MODE_PRIVATE);

                    /*Wenn der Benutzer den Switch "Logindaten merken" einschaltet -> AccesToken wird automatisch angefordert
                      Wenn der Benutzer den Switch "Logindaten merken" ausschaltet -> AlertDialog wird angezeigt & der Benutzer wird
                      nach 5 min abgemeldet*/
                    if((preferences.getString("username", "").equals("")) && (preferences.getString("password", "").equals("")))
                    {
                        AlertDialog.Builder bob = new AlertDialog.Builder(NotenmanagementSingleton.getCurrentAcitivity());
                        bob
                                .setTitle(R.string.alert_title)
                                .setMessage(R.string.alert_token)
                                .setPositiveButton(R.string.alert_close, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        new CountDownTimer(1000*60*5, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }
                                            @Override
                                            public void onFinish() {
                                                NotenmanagementSingleton.getCurrentAcitivity().finish();
                                            }
                                        }.start();
                                    }
                                })
                                .setCancelable(false);
                        bob.create();
                        bob.show();
                    }
                    else{
                        LoginActivity.TOKEN_REFRESHED = true;
                        NotenmanagementServerAPI.login(preferences.getString("username","") ,preferences.getString("password", ""), handler);
                    }
                    break;
            }
        }
    };
    //CountDownTimer auf 20 min einstellen (für Refresh des Acces Tokens)
    public void startTimer(){

                new CountDownTimer(1000*60*20, 1000){
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        LoginActivity.this.handler.obtainMessage(WAITED_20_MIN).sendToTarget();
                    }
                }.start();
            }
}

