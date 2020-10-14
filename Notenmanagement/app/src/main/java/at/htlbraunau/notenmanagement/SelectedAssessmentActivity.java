package at.htlbraunau.notenmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.htlbraunau.notenmanagement.R;
import at.htlbraunau.notenmanagement.FragmentActivity.AllMarksDetailsFragment;

public class SelectedAssessmentActivity extends MenuOptionsActivity {

    private Assessment actualAssessment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);

        //Hilfsmethode, damit onCreate übersichtlich bleibt
        drawInformationOfAssessment();

        NotenmanagementServerAPI.getOverviewOfGradesOfAssessment(this.handler, actualAssessment.getLF_ID());

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotenmanagementServerAPI.OVERVIEW_OF_GRADES_AVAILABLE:
                    int[] overview = (int[])msg.obj;
                    drawOverviewOfMarks(overview);
                    break;
                case NotenmanagementServerAPI.NO_OVERVIEW_OF_GRADES_AVAILABLE:
                    break;
            }
        }
    };

    //Information über Leistungsfestellung anzeigen
    public void drawInformationOfAssessment(){
        setContentView(R.layout.activity_selected_assessment);
        Intent intent = getIntent();
        actualAssessment = intent.getParcelableExtra("assessment");
        TextView tv = (TextView)findViewById(R.id.tv_header);
        tv.setText(actualAssessment.getType() + " in " + actualAssessment.getSubject());

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(AllMarksDetailsFragment.widthOfDevice > 1200 ){
            tv.setTextSize(20);
        }

        GridView gvGeneral = (GridView)findViewById(R.id.gv_general);
        AssessmentGeneralBaseAdapter assessmentBaseAdapter1 = new AssessmentGeneralBaseAdapter(this, actualAssessment);
        gvGeneral.setAdapter(assessmentBaseAdapter1);

        GridView gvGeneralValues = (GridView)findViewById(R.id.gv_general_values);
        AssessmentGeneralValuesBaseAdapter assessmentBaseAdapter2 = new AssessmentGeneralValuesBaseAdapter(this, actualAssessment);
        gvGeneralValues.setAdapter(assessmentBaseAdapter2);

        GridView gvResult = (GridView)findViewById(R.id.gv_result);
        AssessmentResultBaseAdapter assessmentBaseAdapter3 = new AssessmentResultBaseAdapter(this, actualAssessment);
        gvResult.setAdapter(assessmentBaseAdapter3);

        GridView gvResultValues = (GridView)findViewById(R.id.gv_result_values);
        AssessmentResultValuesBaseAdapter assessmentBaseAdapter4 = new AssessmentResultValuesBaseAdapter(this, actualAssessment);
        gvResultValues.setAdapter(assessmentBaseAdapter4);

    }
    //Notenspiegel falls vorhanden darstellen
    void drawOverviewOfMarks(int[] overview){

        TextView tvHeaderOverview = new TextView(SelectedAssessmentActivity.this);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rl_selected_Assessment);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.gv_result_values);
        params.setMargins(25,50,25,0);


        tvHeaderOverview.setId(View.generateViewId());
        tvHeaderOverview.setLayoutParams(params);
        tvHeaderOverview.setTextSize(20);
        tvHeaderOverview.setPadding(0,5,0,5);
        tvHeaderOverview.setBackgroundResource(R.drawable.border_blue_background);
        tvHeaderOverview.setText(R.string.tv_header_overview);
        tvHeaderOverview.setTextColor(Color.WHITE);
        tvHeaderOverview.setGravity(Gravity.CENTER);

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tvHeaderOverview.setTextSize(25);
        }

        layout.addView(tvHeaderOverview);

        GridView gvOverview = new GridView(SelectedAssessmentActivity.this);
        RelativeLayout.LayoutParams params2= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.BELOW,tvHeaderOverview.getId());
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.setMargins(25,15,25,0);

        gvOverview.setPadding(5,5,5,5);
        gvOverview.setId(View.generateViewId());
        gvOverview.setLayoutParams(params2);
        gvOverview.setGravity(Gravity.CENTER_HORIZONTAL);
        gvOverview.setClickable(false);
        gvOverview.setBackgroundResource(R.drawable.border_lightblue_background);
        gvOverview.setNumColumns(7);
        AssessmentOverviewBaseAdapter assessmentBaseAdapter1 = new AssessmentOverviewBaseAdapter(SelectedAssessmentActivity.this,actualAssessment);
        gvOverview.setAdapter(assessmentBaseAdapter1);

        layout.addView(gvOverview);


        GridView gvOverviewValues = new GridView(SelectedAssessmentActivity.this);
        RelativeLayout.LayoutParams params3= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.BELOW,gvOverview.getId());
        params3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.setMargins(25,0,25,50);
        params3.topMargin = 1-10;


        gvOverviewValues.setLayoutParams(params3);
        gvOverviewValues.setGravity(Gravity.CENTER_HORIZONTAL);
        gvOverviewValues.setPadding(5,5,5,5);
        gvOverviewValues.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        gvOverviewValues.setBackgroundResource(R.drawable.border_grey_background);
        gvOverviewValues.setNumColumns(7);
        AssessmentOverviewValuesBaseAdapter assessmentBaseAdapter2 = new AssessmentOverviewValuesBaseAdapter(SelectedAssessmentActivity.this,actualAssessment, overview);
        gvOverviewValues.setAdapter(assessmentBaseAdapter2);

        layout.addView(gvOverviewValues);
    }
}
