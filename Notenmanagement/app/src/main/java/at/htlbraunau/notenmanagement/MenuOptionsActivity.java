package at.htlbraunau.notenmanagement;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import at.htlbraunau.notenmanagement.R;
import at.htlbraunau.notenmanagement.FragmentActivity.AllSubjectsActivity;

//Diese Activity dient nur zur "Vorlage" für andere Activities die das selbe Menü
// beinhalten wie MenuOptionsActivity (eigentlich jede Activity außer Login)
public class MenuOptionsActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_options);

        this.overridePendingTransition(R.anim.enter, R.anim.leave);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.setTitle(getResources().getString(R.string.app_name_long));
        }
        else{
            this.setTitle(getResources().getString(R.string.app_name_short));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.pupil_menu, menu);
        return true;
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {

         switch(item.getItemId())
        {
            //Cases für Menüpunkte
            case R.id.mnuOption_marks:
                Intent intent;
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("AllSubjectsActivity")){
                    intent = new Intent(this,AllSubjectsActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_early_warning:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("EarlyWarnings")){
                    intent = new Intent(this,EarlyWarnings.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_subject_list:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("ListOfSubjects")){
                    intent = new Intent(this,ListOfSubjects.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_eMail_service:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("EmailServiceActivity")){
                    intent = new Intent(this,EmailServiceActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_exit:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("LoginActivity")){
                    intent = new Intent(this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(intent);
                }
                this.finish();
                return true;

            case R.id.mnuOption_settings:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("SettingsActivity")){
                    intent = new Intent(this,SettingsActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_help:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("HelpActivity")){
                    intent = new Intent(this,HelpActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_credits:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("CreditsActivity")){
                    intent = new Intent(this,CreditsActivity.class);
                    this.startActivity(intent);
                }
                return true;


            //Cases für Icons in der ActionBar
            case R.id.mnuOption_home:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("HomePupilActivity")){
                    intent = new Intent(this,HomePupilActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_settings_icon:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("SettingsActivity")){
                    intent = new Intent(this,SettingsActivity.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_early_warning_icon:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("EarlyWarnings")){
                    intent = new Intent(this,EarlyWarnings.class);
                    this.startActivity(intent);
                }
                return true;

            case R.id.mnuOption_marks_icon:
                if(!NotenmanagementSingleton.getCurrentAcitivity().getLocalClassName().equals("AllSubjectsActivity")){
                    intent = new Intent(this,AllSubjectsActivity.class);
                    this.startActivity(intent);
                }
                return true;

            default:
                return false;
        }
    }
}
