package at.htlbraunau.notenmanagement.FragmentActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import at.htlbraunau.notenmanagement.MenuOptionsActivity;

public class AllMarksDetailsActivity extends MenuOptionsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, it means
            // that our MainActivity is being shown with both
            // the titles and the text, so this activity is
            // no longer needed. Bail out and let the MainActivity
            // do all the work.
            finish();
            return;
        }

        if(this.getIntent() != null) {
            // This is another way to instantiate a  fragment
            AllMarksDetailsFragment pf = AllMarksDetailsFragment.newInstance(this.getIntent().getExtras());

            this.getFragmentManager().beginTransaction()
                    .add(android.R.id.content, pf)
                    .commit();
        }
    }
}

