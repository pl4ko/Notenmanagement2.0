package at.htlbraunau.notenmanagement;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import at.htlbraunau.notenmanagement.R;

public class ListOfSubjects extends MenuOptionsActivity {

    private Subject[] subjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_subjects);

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);

        TextView tv = (TextView)findViewById(R.id.tv_list_of_subjects);
        tv.setText(this.getText(R.string.tv_list_of_subjects));

        //Request aller Fächer
        NotenmanagementServerAPI.getAllSubjects(this.handler);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotenmanagementServerAPI.ALL_SUBJECTS_AVAILABLE:
                    subjects = (Subject[]) msg.obj;
                    final ListView lv = (ListView)findViewById(R.id.lv_list_of_subjects);
                    AllShortcutSubjectsArrayAdapter adapter = new AllShortcutSubjectsArrayAdapter(ListOfSubjects.this,R.layout.list_item_allsubjects, subjects);
                    lv.setAdapter(adapter);
                    break;
            }
        }
    };
}

class AllShortcutSubjectsArrayAdapter extends ArrayAdapter<Subject>{
    private Subject[] subjects;

    public AllShortcutSubjectsArrayAdapter(Context context,int resource, Subject[] subjects) {
        super(context, resource, subjects);
        this.subjects = subjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_allsubjects, parent, false);

            holder = new ViewHolder();
            holder.subject = (TextView)convertView.findViewById(R.id.tv_allsubjects_shortcut);
            holder.subjectLabel = (TextView)convertView.findViewById(R.id.tv_allsubjects_full);
            convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

        holder.subject.setText(this.subjects[position].subject);
        holder.subjectLabel.setText(this.subjects[position].subjectLabel);

        holder.subject.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        holder.subjectLabel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if(position% 2 == 0 )
        {
            holder.subject.setBackgroundResource(R.color.colorGrey);
            holder.subjectLabel.setBackgroundResource(R.color.colorGrey);
        }
        else{
            holder.subject.setBackgroundResource(R.color.colorWhite);
            holder.subjectLabel.setBackgroundResource(R.color.colorWhite);
        }

        return convertView;
    }

     static class ViewHolder{
        TextView subject;
        TextView subjectLabel;

    }


}
