package at.htlbraunau.notenmanagement.FragmentActivity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import at.htlbraunau.notenmanagement.R;
import at.htlbraunau.notenmanagement.Assessment;
import at.htlbraunau.notenmanagement.NotenmanagementServerAPI;
import at.htlbraunau.notenmanagement.NotenmanagementSingleton;
import at.htlbraunau.notenmanagement.SelectedAssessmentActivity;

public class AllMarksDetailsFragment extends Fragment {

    public static  final int widthOfDevice = 0;
    private int index = -1;
    private String subject;
    public ListView l;

    public static AllMarksDetailsFragment newInstance(int index, String subject) {
        AllMarksDetailsFragment pf = new AllMarksDetailsFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putString("subject", subject);
        pf.setArguments(args);
        return pf;
    }

    public static AllMarksDetailsFragment newInstance(Bundle bundle)	{
        int index = bundle.getInt("index", 0);
        String subject = bundle.getString("subject", "");

        return AllMarksDetailsFragment.newInstance(index, subject);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Alle LF's des Schülers
        GetAllAssessmentsOfStudentAsyncTask task = new GetAllAssessmentsOfStudentAsyncTask();
        task.execute();

        this.index = this.getArguments().getInt("index", 0);
        this.subject = this.getArguments().getString("subject", "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //xml für Detailansicht
        View view = inflater.inflate(R.layout.fragment_all_marks_details, container, false);
        TextView tv = (TextView)view.findViewById(R.id.tv_details);
        tv.setText(this.getString(R.string.assessmentsOfStudent) + " " + NotenmanagementSingleton.getUser().userName +
                    " (" + NotenmanagementSingleton.getUser().className + ") " + this.getString(R.string.in)+ " " + subject);

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(25);
        }

        GridView gvHeader = (GridView)view.findViewById(R.id.gv_header_assessments);
        gvHeader.setAdapter(new AssessmentsHeaderBaseAdapter(getActivity()));

        return view;
    }


    public int getShownIndex() {
        return this.index;
    }

    class GetAllAssessmentsOfStudentAsyncTask extends AsyncTask<Void, Void, Assessment[]> {

        @Override
        protected Assessment[] doInBackground(Void... voids) {
            return NotenmanagementServerAPI.getAllAssessmentsOfStudent();
        }

        @Override
        protected void onPostExecute(Assessment[] assessments) {

            ArrayList<Assessment> assessmentsOfSelectedSubject = new  ArrayList<Assessment>();

            for (int i = 0; i < assessments.length ; i++) {

                if (assessments[i].getSubject().equals(AllMarksDetailsFragment.this.subject)) {
                    assessmentsOfSelectedSubject.add(assessments[i]);
                }
            }
            try {

                ListView l = (ListView) getView().findViewById(R.id.lv_assessments);
                l.setAdapter(new AssessmentsArrayAdapter(getActivity(),
                        R.layout.list_item_assessments, assessmentsOfSelectedSubject.toArray(new Assessment[]{})));
            }
            catch(Exception e){

            }

        }
    }

}

class AssessmentsArrayAdapter extends ArrayAdapter<Assessment>{
    private Assessment[] assessments;
    private Context context;

    public AssessmentsArrayAdapter(Context context, int textViewResourceId, Assessment[] items) {
        super(context, textViewResourceId, items);
        this.assessments = items;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.list_item_assessments, parent, false);
        }
        convertView.setBackgroundColor(this.context.getResources().getColor(R.color.colorGrey));

        return drawGridItemAllAssessments(convertView, position);
    }


    public View drawGridItemAllAssessments(View v, final int position){
        final GridView gv = new GridView(v.getContext());

        gv.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position2, long id) {

                switch(position2){
                    case 0:
                        Intent intent = new Intent(AssessmentsArrayAdapter.this.context, SelectedAssessmentActivity.class);
                        intent.putExtra("assessment", assessments[position]);
                        AssessmentsArrayAdapter.this.context.startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }

            }
        });
        gv.setNumColumns(5);
        gv.setPadding(2,2,2,2);
        AssessmentsBaseAdapter aba = new AssessmentsBaseAdapter(v.getContext(),position, this.assessments);
        gv.setAdapter(aba);
        return  gv;
    }
}

class AssessmentsHeaderBaseAdapter extends BaseAdapter {

    private Context context;

    public AssessmentsHeaderBaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            view = layoutInflater.inflate(R.layout.grid_item_header_assessments, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_header_assessments);
        tv.setGravity(Gravity.LEFT);

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(25);
        }

        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        switch (i) {
            case 0:
                tv.setText(R.string.gv_item_date);
                break;
            case 1:
                tv.setText(R.string.gv_item_info);
                break;
            case 2:
                tv.setText(R.string.gv_item_points);
                break;
            case 3:
                tv.setText(R.string.gv_item_mark);
                break;
            case 4:
                tv.setText(R.string.gv_item_comment);
                break;
        }
        return view;
    }
}

class AssessmentsBaseAdapter extends BaseAdapter {
    private Assessment[] assessments;
    private Context context;
    private int position;

    public AssessmentsBaseAdapter(Context context,int position,  Assessment[] assessments) {
        this.context = context;
        this.assessments = assessments;
        this.position = position;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            view = layoutInflater.inflate(R.layout.grid_item_assessments, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_assessments);
        tv.setGravity(Gravity.LEFT);

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(25);
        }

        switch (i) {
            case 0:
                tv.setText(this.assessments[position].getDate() + "\n" + this.assessments[position].getType());
                tv.setTextColor(this.context.getResources().getColor(R.color.colorPrimary));

                tv.setPaintFlags(tv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                break;
            case 1:
                tv.setText(this.assessments[position].getLF_Comment());
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                break;
            case 2:
                if(this.assessments[position].getPoints() > -1){
                    tv.setText(Double.toString(this.assessments[position].getPoints()));
                }
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                break;
            case 3:
                if(this.assessments[position].getMark() > 0){
                    tv.setText(Integer.toString(this.assessments[position].getMark()));
                }
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                break;
            case 4:
                tv.setText(this.assessments[position].getComment());
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                break;
        }
        return view;
    }
}
