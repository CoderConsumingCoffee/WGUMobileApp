package com.aphart.wgumobileapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ViewTermsFragment extends android.app.Fragment {

    private SimpleCursorAdapter cursorAdapter;
    private Button addTermButton;
    private ViewEditAllTermsActivity viewEditAllTermsActivity;

    private WGUAppDatabaseHelper wguAppDatabaseHelper;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_view_terms, container, false);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }
    public void onStart(){
        super.onStart();

            wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());

        viewEditAllTermsActivity = (ViewEditAllTermsActivity) getActivity();
        //Load the list of courses from the database
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_home_fragment, null, new String[]{WGUAppDBContract.WGUAppTermTableEntry.TERM_TITLE}, new int[]{ R.id.listHomeFragmentLayout}, 0);
        ListView listView = (ListView) getView().findViewById(R.id.listViewForCurseAd);
        listView.setAdapter(cursorAdapter);

        addTermButton = (Button) getView().findViewById(R.id.addTermButton);
        addTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewEditAllTermsActivity.addTerm();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                viewEditAllTermsActivity.viewEditTerm(position);
            }
        });
        new myLoader().execute();

    }

    @Override
    public void onPause() {
        super.onPause();
        wguAppDatabaseHelper.finalize();
    }

    private class myLoader extends AsyncTask<Void, Void, Cursor>
    {
        private Cursor cursor;

        @Override
        protected Cursor doInBackground(Void... voids) {

            wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getTermTitleCursor();
            return cursor;
        }



        @Override
        protected void onPostExecute(Cursor cursor){
            cursorAdapter.changeCursor(cursor);
        }



    }
}