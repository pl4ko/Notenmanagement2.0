package at.htlbraunau.notenmanagement.FragmentActivity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import at.htlbraunau.notenmanagement.R;
import at.htlbraunau.notenmanagement.EarlyWarnings;
import at.htlbraunau.notenmanagement.HomePupilActivity;
import at.htlbraunau.notenmanagement.NotenmanagementServerAPI;
import at.htlbraunau.notenmanagement.NotenmanagementSingleton;
import at.htlbraunau.notenmanagement.SettingsActivity;

public class AllSubjectsFragment extends ListFragment {

    private View previousView;
    private int previousIndex = -1;
    private String previousSubject = "";
    private boolean performedItemClickViaMethod = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.mnuOption_home:
                Intent intent = new Intent(getActivity(), HomePupilActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.mnuOption_settings_icon:
                intent = new Intent(getActivity(), SettingsActivity.class);
                this.startActivity(intent);
                return true;

            case R.id.mnuOption_early_warning_icon:
                intent = new Intent(getActivity(), EarlyWarnings.class);
                this.startActivity(intent);
                return true;

            case R.id.mnuOption_marks_icon:
                intent = new Intent(getActivity(), AllSubjectsActivity.class);
                this.startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        //Alle Fächer mit LFs vom Server holen und den ListAdapter setzen in der PostExecute() Methode
        GetAllSubjectsOfStudentAsyncTask subjectstask = new GetAllSubjectsOfStudentAsyncTask();
        subjectstask.execute();

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            this.previousIndex = savedInstanceState.getInt("previousIndex", 0);
            this.previousSubject = savedInstanceState.getString("previousSubject", "");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt("previousIndex", this.previousIndex);
        outState.putString("previousSubject", this.previousSubject);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.pupil_menu, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        ListView lv = getListView();

         lv.setSelector(R.drawable.border_transparent_background);
         lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
         lv.setSelection(this.previousIndex);

        if(this.previousIndex >= 0)
        {
            //Falls der Index größer als -1 ist: Detailsansicht starten/anzeigen
            ((AllSubjectsActivity)this.getActivity()).showDetails(this.previousIndex, this.previousSubject);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        //Display --> Landscape-Modus (Horizontal)
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //Abfrage ob mit "performClick()" oder durch den Benutzer der onListItemClickListener ausgelöst wurde
            if (!this.performedItemClickViaMethod) {

                // angeklicktes Listitem
                TextView tv = (TextView) v.findViewById(R.id.tv_list_item_subjects);

                //Abfrage ob ein unterschiedliches Item angeklickt wurde
                if (pos != previousIndex  && this.previousIndex != -1) {

                    //Falls previousView null ist (aufgrund einer Bildschrimdrehung) --> mit Hilfmethode nochmals setzen
                    if(this.previousView == null){
                        this.previousView = this.getViewByPosition(this.previousIndex, l);
                    }

                    TextView previousTv =(TextView) this.previousView.findViewById(R.id.tv_list_item_subjects);

                    //Setzen der Hintergrundfarbe des vorherigen Items
                    if (this.previousIndex % 2 == 0) {
                        previousTv.setBackgroundColor(getResources().getColor(R.color.colorGrey));

                    } else {
                        previousTv.setBackgroundColor(getResources().getColor(R.color.colorWhite));;
                    }

                    //Highlighter setzen
                    tv.setBackground(getResources().getDrawable(R.drawable.border_orange_background));

                    //speichern der neuen Werte des angeklickten Listitems
                    this.previousIndex = pos;
                    this.previousSubject = l.getItemAtPosition(pos).toString();
                    this.previousView = v;
                    ((AllSubjectsActivity) this.getActivity()).showDetails(pos, this.previousSubject);

                }
            }
            //Listener mit "performClick()" ausgelöst
            else {

                if(this.previousSubject != ""){
                    this.performedItemClickViaMethod = false;
                    //this.previousView = v;
                    this.previousSubject  = l.getItemAtPosition(pos).toString();
                    ((AllSubjectsActivity) this.getActivity()).showDetails(pos, this.previousSubject);
                }
                else{
                    this.performedItemClickViaMethod = false;
                    this.previousView = v;
                    this.previousSubject  = l.getItemAtPosition(0).toString();
                    ((AllSubjectsActivity) this.getActivity()).showDetails(pos, this.previousSubject);
                }

            }
        }
        //Display --> Portrait-Modus (Vertikal)
        else{

            this.performedItemClickViaMethod = false;

            if(pos != this.previousIndex && this.previousIndex != -1) {

                //Falls previousView null ist (aufgrund einer Bildschrimdrehung) --> mit Hilfsmethode nochmals setzen
                if(this.previousView == null) {

                    this.previousView = this.getViewByPosition(this.previousIndex, l);
                }

                    TextView tv2 = (TextView) this.previousView.findViewById(R.id.tv_list_item_subjects);

                    //Setzen der Hintergrundfarbe des vorherigen Items
                    if (this.previousIndex % 2 == 0) {
                        tv2.setBackgroundColor(getResources().getColor(R.color.colorGrey));

                    } else {
                        tv2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    }
                }

                TextView tv = (TextView) v.findViewById(R.id.tv_list_item_subjects);
                tv.setBackground(getResources().getDrawable(R.drawable.border_orange_background));

                //Setzen der neuen Werte
                this.previousIndex = pos;
                this.previousSubject = l.getItemAtPosition(pos).toString();
                this.previousView = v;
                ((AllSubjectsActivity) this.getActivity()).showDetails(pos, this.previousSubject);
            }
        }

    //View an bestimmter Position bekommen
    public View getViewByPosition(int pos, ListView listView) {

        final int firstListItemPosition = listView.getFirstVisiblePosition();

        listView.setItemChecked(pos, true);
        View wantedView = listView.getAdapter().getView(pos, null, listView);

        return wantedView;
    }

    class GetAllSubjectsOfStudentAsyncTask extends AsyncTask<Void, Void , String[]>{

        @Override
        protected String[] doInBackground(Void... params) {
            return NotenmanagementServerAPI.getAllSubjectsWithAssessmentssOfStudent();
        }

        @Override
        protected void onPostExecute(String[] subjects) {

            ListView lv = AllSubjectsFragment.this.getListView();
            int index  =  AllSubjectsFragment.this.previousIndex;

            // Populate list with our array of classes.
            AllSubjectsFragment.this.setListAdapter(new SubjectsArrayAdapter(getActivity(),
                    R.layout.list_item_assessments, subjects));

            if(AllSubjectsFragment.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

                //Falls schon ein Fach ausgewählt wurde
                if(AllSubjectsFragment.this.previousSubject != "" ){

                    AllSubjectsFragment.this.performedItemClickViaMethod = true;
                   // AllSubjectsFragment.this.previousView = this.getViewByPosition(index, lv).findViewById(R.id.tv_list_item_subjects);
                    AllSubjectsFragment.this.previousSubject = lv.getItemAtPosition(index).toString();
                    lv.performItemClick(lv, index, lv.getItemIdAtPosition( index));
                }
                //Falls kein Fach ausgewählt wurde --> Erstes Fach automatisch auswählen
                else{
                    getListView().setSelection(0);
                    AllSubjectsFragment.this.performedItemClickViaMethod = true;
                    AllSubjectsFragment.this.previousIndex = 0;
                    lv.performItemClick(lv, 0, lv.getItemIdAtPosition(0));
                }

            }
        }
    }

    class SubjectsArrayAdapter extends ArrayAdapter<String>{
        private String[] subjects;

        public SubjectsArrayAdapter(Context context,int resource, String[] subjects) {
            super(context, resource, subjects);
            this.subjects = subjects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_subjectswithassessments, parent, false);
            }

            TextView tv =(TextView) convertView.findViewById(R.id.tv_list_item_subjects);

            //Falls der Bildschirm breit genug ist --> Text vergrößern
            if(NotenmanagementSingleton.getWidthOfDevice() > NotenmanagementServerAPI.maxWidthOfSmallScreen){
                tv.setTextSize(25);
            }

            tv.setText(this.subjects[position]);

            highlightItem(position, tv);

            return convertView;
        }

        private void highlightItem(int position, TextView result) {
            if(position == previousIndex) {

                result.setBackground(getResources().getDrawable(R.drawable.border_orange_background));
            } else {

                if(position% 2 == 0 )
                {
                    result.setBackgroundResource(R.color.colorGrey);

                }
                else{
                    result.setBackgroundResource(R.color.colorWhite);
                }
            }
        }

    }
}
