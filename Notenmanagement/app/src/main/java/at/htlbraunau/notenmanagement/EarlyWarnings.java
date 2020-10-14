package at.htlbraunau.notenmanagement;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import at.htlbraunau.notenmanagement.R;

public class EarlyWarnings extends MenuOptionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_early_warnings);
        TextView tvHeader =(TextView)findViewById(R.id.tv_header_earlyWarnings);

        NotenmanagementSingleton.setContext(this.getApplicationContext());
        NotenmanagementSingleton.setCurrentAcitivity(this);

       tvHeader.setText(this.getString(R.string.tv_header_all_earlyWarnings) +
                " " +
                NotenmanagementSingleton.getInstance().getUser().userName + " " +
                "(" +  NotenmanagementSingleton.getInstance().getUser().className + ")");

        NotenmanagementServerAPI.getAllEarlyWarnings(handler);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotenmanagementServerAPI.EARLY_WARNINGS_AVAILABLE:
                    Bundle bundle = (Bundle)msg.obj;
                    ArrayList<EarlyWarning> earlyWarnings = bundle.getParcelableArrayList("earlywarnings");

                    if(earlyWarnings.size() != 0) {
                        Date endOfFirstTerm = (Date) bundle.getSerializable("endOfTerm");

                        GridView gvHeader = (GridView) findViewById(R.id.gv_header_early_warnings);
                        gvHeader.setVisibility(View.VISIBLE);
                        EarlyWarningsHeaderBaseAdapter earlyWarningsHeaderBaseAdapter = new EarlyWarningsHeaderBaseAdapter(getApplicationContext());
                        gvHeader.setAdapter(earlyWarningsHeaderBaseAdapter);

                        ListView lv = (ListView) findViewById(R.id.lv_all_early_warnings);
                        EarlyWarningsArrayAdapter ewaa = new EarlyWarningsArrayAdapter(EarlyWarnings.this, R.layout.list_item_early_warning, earlyWarnings, endOfFirstTerm);
                        lv.setAdapter(ewaa);
                    }
                    else{
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.ll_early_warning);
                        TextView tv = new TextView(EarlyWarnings.this);
                        tv.setTextSize(20);
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setText(R.string.noEarlyWarningsAvailable);
                        linearLayout.addView(tv);
                    }
                     break;
                case NotenmanagementServerAPI.NO_EARLY_WARNINGS_AVAILABLE:
                    break;
            }
        }
    };
}


class EarlyWarningsHeaderBaseAdapter extends BaseAdapter{

    private Context context;

    public EarlyWarningsHeaderBaseAdapter(Context context) {
        this.context = context;
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
            view = layoutInflater.inflate(R.layout.grid_item_header_early_warnings, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_header_early_warnings);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(25);
        }

        switch (i) {
            case 0:
                tv.setText(R.string.subject);
                break;
            case 1:
                tv.setText(R.string.firstTerm);
                break;
            case 2:
                tv.setText(R.string.secondTerm);
                break;
        }
        return view;
    }
}

class EarlyWarningsBaseAdapter extends BaseAdapter{
    private ArrayList<EarlyWarning> earlyWarnings;
    private Context context;
    private int position;
    private Date endOfFirstTerm;

    public EarlyWarningsBaseAdapter(Context context, ArrayList<EarlyWarning> earlyWarnings, int position, Date endOfFirstTerm) {
        this.context = context;
        this.earlyWarnings = earlyWarnings;
        this.position = position;
        this.endOfFirstTerm = endOfFirstTerm;
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
            view = layoutInflater.inflate(R.layout.grid_item_early_warning, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tv_grid_item_early_warning);

        //Falls der Bildschirm breit genug ist --> Text vergrößern
        if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
            tv.setTextSize(20);
        }

        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY);
        Date insertDate = null;

        try {
             insertDate = sdf.parse(this.earlyWarnings.get(position).getDateOfinsert());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (i) {
            case 0:
                tv.setText(this.earlyWarnings.get(position).getSubject());
                break;
            case 1:
                if(insertDate.compareTo(this.endOfFirstTerm) < 0)
                {
                    tv.setText(convertDate(this.earlyWarnings.get(position).getDateOfinsert()));
                }
                else{
                    tv.setText("");
                }
                break;
            case 2:
                if(insertDate.compareTo(this.endOfFirstTerm) > 0)
                {
                    tv.setText(convertDate(this.earlyWarnings.get(position).getDateOfinsert()));
                }
                else{
                    tv.setText("");
                }
                break;
        }
        return view;
    }

    public String convertDate(String date){
        StringTokenizer st = new StringTokenizer(date, "-T");
        String year = st.nextToken();
        String month = st.nextToken();
        String day = st.nextToken();

        return day + "." + month + "." + year;
    }

}

class EarlyWarningsArrayAdapter extends  ArrayAdapter{
    private ArrayList<EarlyWarning> earlyWarnings;
    private Context context;
    private Date endOfFirstTerm;

    public EarlyWarningsArrayAdapter(Context context, int resource, ArrayList<EarlyWarning> earlyWarnings, Date endOfFirstTerm) {
        super(context, resource, earlyWarnings);
        this.earlyWarnings = earlyWarnings;
        this.context = context;
        this.endOfFirstTerm = endOfFirstTerm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_early_warning, parent, false);
        }

        return this.drawGridViewItemEarlyWarning(convertView, position);
    }

    public View drawGridViewItemEarlyWarning(View view, int position){
        GridView gv = new GridView(this.context);

        gv.setLayoutParams((new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)));

        gv.setNumColumns(3);
        gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        gv.setPadding(2,2,2,2);
        gv.setBackgroundResource(R.color.colorGrey);
        gv.setBackgroundResource(0);
        EarlyWarningsBaseAdapter ewba = new EarlyWarningsBaseAdapter(this.context, this.earlyWarnings, position, this.endOfFirstTerm);
        gv.setAdapter(ewba);

        return  gv;
    }

}
