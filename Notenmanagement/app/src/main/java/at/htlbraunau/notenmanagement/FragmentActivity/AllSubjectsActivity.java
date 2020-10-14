package at.htlbraunau.notenmanagement.FragmentActivity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import at.htlbraunau.notenmanagement.R;
import at.htlbraunau.notenmanagement.Assessment;
import at.htlbraunau.notenmanagement.MenuOptionsActivity;
import at.htlbraunau.notenmanagement.NotenmanagementSingleton;

public class AllSubjectsActivity extends MenuOptionsActivity {

    private Assessment[] assessments;
    private String[] subjectsWithAssessments;
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_subjects_list);

        NotenmanagementSingleton.setCurrentAcitivity(this);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            this.setTitle(getResources().getString(R.string.app_name_long));
        }
        else{
            this.setTitle(getResources().getString(R.string.app_name_short));
        }
    }

    public  boolean isMultiPane() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    public void showDetails(int index, String subject) {

        if(this.isMultiPane()) {
            // Check what fragment is shown, replace if needed.
            AllMarksDetailsFragment details = (AllMarksDetailsFragment)this.getFragmentManager().findFragmentById(R.id.details_marks);

            if (details == null || details.getShownIndex() != index) {

                // Make a new fragment to show this selection
                AllMarksDetailsFragment pf = AllMarksDetailsFragment.newInstance(index, subject);

                // Execute a transaction, replacing any existing fragment with this one inside the frame
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("details");
                ft.replace(R.id.details_marks, pf);
                ft.commit();
            }
        }
        else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(this, AllMarksDetailsActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("subject", subject);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

