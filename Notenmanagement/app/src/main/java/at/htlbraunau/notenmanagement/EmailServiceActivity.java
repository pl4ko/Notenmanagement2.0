package at.htlbraunau.notenmanagement;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.htlbraunau.notenmanagement.R;

public class EmailServiceActivity extends MenuOptionsActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_service);

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);
    }

    public void onClickedEmail(View v){
        EditText edt = (EditText)findViewById(R.id.edt_eMail);

        //Knopfdruck Animation
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        v.startAnimation(animation1);

        if(isEmailValid(edt.getText().toString())) {
            NotenmanagementServerAPI.change2ndEmail(this.handler, edt.getText().toString());
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.invalidEmail, Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotenmanagementServerAPI.EMAIL_CHANGED_SUCCESSFULL:
                    String email = (String)msg.obj;
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.emailChangeSuccessful) + " " + email, Toast.LENGTH_LONG).show();
                    break;
                case NotenmanagementServerAPI.EMAIL_CHANGED_FAILED:
                    Toast.makeText(getApplicationContext(), R.string.emailChangeFailed, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //Validierung von eingegebener E-Mail Adresse
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

