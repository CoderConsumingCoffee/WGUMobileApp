package com.aphart.wgumobileapp;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewUpcomingAssesmentsFragment extends android.app.Fragment {

    private boolean listInit;
    private SimpleCursorAdapter simpleCursorAdapter;

    public ViewUpcomingAssesmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_upcoming_assesments, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView) getView().findViewById(R.id.assessmentList);


        if(!listInit){

            listInit = true;
            new LoadAssessments().execute();
            listView.setAdapter(simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_home_fragment, null, new String[]{WGUAppDBContract.WGUAssesmentEntry.ASSESMENT_TITLE},
                    new int[]{R.id.listHomeFragmentLayout}, 0));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UpcomingAssessmentsActivity upcomingAssessmentsActivity = (UpcomingAssessmentsActivity) getActivity();
                upcomingAssessmentsActivity.launchSelectedAssessment(position);
            }
        });
    }

    private class LoadAssessments extends AsyncTask<Void,Void,Cursor>{

        @Override
        protected Cursor doInBackground(Void... voids) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            Cursor cursor = wguAppDatabaseHelper.loadAllAssessmentTitles();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            simpleCursorAdapter.swapCursor(cursor);

        }
    }

}
