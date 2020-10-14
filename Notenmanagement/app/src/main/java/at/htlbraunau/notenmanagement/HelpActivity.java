package at.htlbraunau.notenmanagement;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import at.htlbraunau.notenmanagement.R;

public class HelpActivity extends MenuOptionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);
    }

    public void onSendMessage(View v){
        final EditText edtEmail = (EditText)findViewById(R.id.edt_MailOfUser);
        EditText edtSubjectMail = (EditText)findViewById(R.id.edt_subjectMail);
        EditText edtMessage = (EditText)findViewById(R.id.edt_Message);

        //Knopfdruck Animation
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(500);
        v.startAnimation(animation1);


        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                edtEmail.setBackgroundResource(R.drawable.border_white_background);
            }
        });

        String subject = edtSubjectMail.getText().toString();
        String email = edtEmail.getText().toString();
        String message = edtMessage.getText().toString();


        //Clientseitige Validierung der eingegebenen E-Mail Adresse
        if(EmailServiceActivity.isEmailValid(email)){
            NotenmanagementServerAPI.sendMessageToHelpDesk(this.handler,subject, email, message );
        }
        else{
            edtEmail.setBackgroundResource(R.drawable.border_red_backround);
            Toast.makeText(this, R.string.invalidEmail, Toast.LENGTH_SHORT).show();
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case NotenmanagementServerAPI.MESSAGE_TO_HELPDESK_SENT_SUCCESSFUL:
                    Toast.makeText(HelpActivity.this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    break;
                case NotenmanagementServerAPI.MESSAGE_TO_HELPDESK_SENT_FAILED:
                    Toast.makeText(HelpActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
