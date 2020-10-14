package at.htlbraunau.notenmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import at.htlbraunau.notenmanagement.R;

public class HomePupilActivity extends MenuOptionsActivity {

    private Bundle bundleAssessments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pupil);

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);

        NotenmanagementServerAPI.getLastFiveAssessmentsOfPupil(this.handler);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotenmanagementServerAPI.LAST_FIVE_ASSESSMENTS_SUCCESSFUL:
                    bundleAssessments = (Bundle) msg.obj;
                    ArrayList<Assessment> arrayListAssessments = bundleAssessments.getParcelableArrayList("key1");
                    final ListView lv = (ListView) findViewById(R.id.lv_last_five_assessments);

                    ArrayAdapter<Assessment> adapterAssessments= new ArrayAdapter<Assessment>(HomePupilActivity.this, R.layout.list_item_last_five_assessments, R.id.tv_item, arrayListAssessments);
                    lv.setAdapter(adapterAssessments);

                    //Listener für  das Auswählen eines Listitems
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,int position, long id)
                        {
                            Assessment assessment = (Assessment)lv.getItemAtPosition(position);
                            Intent intent = new Intent(HomePupilActivity.this, SelectedAssessmentActivity.class);
                            intent.putExtra("assessment", assessment);
                            startActivity(intent);
                        }});
                   break;
                case NotenmanagementServerAPI.LAST_FIVE_ASSESSMENTS_FAILED:
                    break;
            }
        }
    };
}
