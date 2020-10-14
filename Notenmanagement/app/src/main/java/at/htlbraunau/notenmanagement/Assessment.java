package at.htlbraunau.notenmanagement;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.StringTokenizer;

import at.htlbraunau.notenmanagement.R;

/**
 * Created by Konrad on 26.09.2017.
 */

public class Assessment implements Parcelable {
    private int LF_ID;
    private String date;
    private int teacher_ID;
    private String subject;
    private String type;
    private String maxPoints;
    private String LF_Comment;
    private int mark;
    private double points;
    private String comment;

    public Assessment( int LF_ID, String date, int teacher_ID,
                        String subject, String type, String maxPoints, String LF_Comment,
                       int mark, double points, String comment){
        this.LF_ID = LF_ID;
        this.date  = date;
        this.teacher_ID = teacher_ID;
        this.subject = subject;
        this.type = type;
        this.maxPoints = maxPoints;
        this.LF_Comment = LF_Comment;
        this.mark = mark;
        this.points = points;
        this.comment = comment;
    }

    public void setLF_ID(int LF_ID) {
        this.LF_ID = LF_ID;
    }

    public int getLF_ID(){return this.LF_ID;}

    public String getDate() {
        StringTokenizer st = new StringTokenizer(this.date, "-T");
        String year = st.nextToken();
        String month = st.nextToken();
        String day = st.nextToken();

        return day + "." + month + "." + year;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTeacher_ID() {
        return teacher_ID;
    }

    public void setTeacher_ID(int teacher_ID) {
        this.teacher_ID = teacher_ID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(String maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getLF_Comment() {
        return LF_Comment;
    }

    public void setLF_Comment(String LF_Comment) {
        this.LF_Comment = LF_Comment;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Assessment(Parcel parcel){
        this.LF_ID = parcel.readInt();
        this.date  = parcel.readString();
        this.teacher_ID = parcel.readInt();
        this.subject = parcel.readString();
        this.type = parcel.readString();
        this.maxPoints = parcel.readString();
        this.LF_Comment = parcel.readString();
        this.mark = parcel.readInt();
        this.points = parcel.readDouble();
        this.comment = parcel.readString();
    }


    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getSubject() +": " + getType() + " vom " + getDate();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeInt(this.LF_ID);
        parcel.writeString(this.date);
        parcel.writeInt(this.teacher_ID);
        parcel.writeString(this.subject);
        parcel.writeString(this.type);
        parcel.writeString(this.maxPoints);
        parcel.writeString(this.LF_Comment);
        parcel.writeInt(this.mark);
        parcel.writeDouble(this.points);
        parcel.writeString(this.comment);

    }

}

class AssessmentGeneralBaseAdapter extends BaseAdapter{

    private Context context;
    private Assessment assessment;

    public AssessmentGeneralBaseAdapter(Context context, Assessment assessment) {
        this.context = context;
        this.assessment = assessment;
    }

    @Override
    public int getCount() {
        return 3;
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
            view = layoutInflater.inflate(R.layout.grid_item_general, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_general);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        switch (i) {
            case 0:
                tv.setText(R.string.gv_item_info);
                break;
            case 1:
                tv.setText(R.string.gv_item_maxPoints);
                break;
            case 2:
                tv.setText(R.string.gv_item_date);
                break;
        }
        return view;
    }



    public Assessment getAssessment() {
            return assessment;
            }

    public void setAssessment(Assessment assessment) {
            this.assessment = assessment;
            }
}

class AssessmentGeneralValuesBaseAdapter extends BaseAdapter{

    private Context context;
    private Assessment assessment;

    public AssessmentGeneralValuesBaseAdapter(Context context, Assessment assessment) {
        this.context = context;
        this.assessment = assessment;
    }

    @Override
    public int getCount() {
        return 3;
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
            view = layoutInflater.inflate(R.layout.grid_item_general_values, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_general_value);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        switch (i) {
            case 0:
                tv.setText(this.assessment.getLF_Comment());
                break;
            case 1:
                if(this.assessment.getMaxPoints().equals("null")){
                    tv.setText("");
                    break;
                }
                tv.setText(this.assessment.getMaxPoints());
                break;
            case 2:
                tv.setText(this.assessment.getDate());
                break;
        }
        return view;
    }
}

class AssessmentResultBaseAdapter extends BaseAdapter{

    private Context context;
    private Assessment assessment;

    public AssessmentResultBaseAdapter(Context context, Assessment assessment) {
        this.context = context;
        this.assessment = assessment;
    }

    @Override
    public int getCount() {
        return 4;
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
            view = layoutInflater.inflate(R.layout.grid_item_result, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_result);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        switch (i) {
            case 0:
                tv.setText(R.string.gv_item_comment);
                break;
            case 1:
                tv.setText(R.string.gv_item_mark);
                break;
            case 2:
                tv.setText(R.string.gv_item_percent);
                break;
            case 3:
                tv.setText(R.string.gv_item_points);
                break;
        }
        return view;
    }
}

class AssessmentResultValuesBaseAdapter extends BaseAdapter{

    private Context context;
    private Assessment assessment;

    public AssessmentResultValuesBaseAdapter(Context context, Assessment assessment) {
            this.context = context;
            this.assessment = assessment;
            }

    @Override
    public int getCount() {
            return 4;
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
                view = layoutInflater.inflate(R.layout.grid_item_result_values, null);
            }
            TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_result_value);
            tv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

            switch(i) {
                case 0:
                    tv.setText(this.assessment.getComment());
                break;

                case 1:
                    switch(this.assessment.getMark()){
                        case  0:
                            tv.setText("Gefehlt");
                            break;
                        case  -1:
                            tv.setText("");
                            break;
                        default:
                            tv.setText(Integer.toString(this.assessment.getMark()));
                            break;
                    }
                break;

                case 2:
                    if(this.assessment.getPoints() == -1.0){
                        tv.setText("");
                        break;
                    }
                   double percentage = (this.assessment.getPoints() / Double.parseDouble(this.assessment.getMaxPoints()) *100);
                    tv.setText(String.format("%.2f", percentage) + "%");
                    break;

                case 3:
                    if(this.assessment.getPoints() == -1.0){
                        tv.setText("");
                        break;
                    }
                    tv.setText(Double.toString(this.assessment.getPoints()));
                break;
                }
                return view;
            }

}

class AssessmentOverviewBaseAdapter extends BaseAdapter{
    private Context context;
    private Assessment assessment;

    public AssessmentOverviewBaseAdapter(Context context, Assessment assessment) {
        this.context = context;
        this.assessment = assessment;
    }

    @Override
    public int getCount() {
        return 7;
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
            view = layoutInflater.inflate(R.layout.grid_item_overview, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_overview);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        switch(i) {
            case 0:
                tv.setText("1");
                break;
            case 1:
                tv.setText("2");
                break;
            case 2:
                tv.setText("3");
                break;
            case 3:
                tv.setText("4");
                break;
            case 4:
                tv.setText("5");
                break;
            case 5:
                tv.setText("Ø");
                break;
            case 6:
                tv.setText("Gefehlt");
                break;
        }
        return view;
    }
}

class AssessmentOverviewValuesBaseAdapter extends BaseAdapter{
    private Context context;
    private Assessment assessment;
    private int[] overview;

    public AssessmentOverviewValuesBaseAdapter(Context context, Assessment assessment, int[] overview) {
        this.overview = overview;
        this.context = context;
        this.assessment = assessment;
    }

    @Override
    public int getCount() {
        return 7;
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
            view = layoutInflater.inflate(R.layout.grid_item_overview_values, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_overview_value);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        switch(i) {
            case 0:
                tv.setText(Integer.toString(this.getOverview()[0]));
                break;
            case 1:
                tv.setText(Integer.toString(this.getOverview()[1]));
                break;
            case 2:
                tv.setText(Integer.toString(this.getOverview()[2]));
                break;
            case 3:
                tv.setText(Integer.toString(this.getOverview()[3]));
                break;
            case 4:
                tv.setText(Integer.toString(this.getOverview()[4]));
                break;
            case 5:
                double summ = (this.getOverview()[0]*1 + this.getOverview()[1]*2 +this.getOverview()[2]*3 +this.getOverview()[3]*4 +this.getOverview()[4]*5);
                double average = summ/(this.getOverview()[0] + this.getOverview()[1] + this.getOverview()[2] + this.getOverview()[3] + this.getOverview()[4]);
                tv.setText(String.format("%.2f", average));
                break;
            case 6:
                tv.setText(Integer.toString(this.getOverview()[5]));
                break;

        }
        return view;
    }

    public int[] getOverview() {
        return overview;
    }
}

