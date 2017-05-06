package com.aphart.wgumobileapp;




import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aphart.wgumobileapp.CurrentNotesActivity;
import com.aphart.wgumobileapp.R;
import com.aphart.wgumobileapp.WGUAppDBContract;
import com.aphart.wgumobileapp.WGUAppDatabaseHelper;

public class CurrentNotesFragment extends android.app.Fragment {
    private WGUAppDatabaseHelper dbHelper;
    private ListView listView;
    private SimpleCursorAdapter simpleCursorAdapter;
    private boolean alreadyInit;

    public CurrentNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new WGUAppDatabaseHelper(getActivity());
        simpleCursorAdapter =
                new SimpleCursorAdapter(getActivity(),
                        R.layout.list_home_fragment,
                        null,
                        new String[]{WGUAppDBContract.NoteTableEntry.NOTE_TITLE},
                        new int[]{R.id.listHomeFragmentLayout});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_notes, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        listView = (ListView) getView().findViewById(R.id.currentNotesListView);
        listView.setAdapter(simpleCursorAdapter);
        LoadNotesAndUpdateListView load = new LoadNotesAndUpdateListView();
        if (!alreadyInit) {
            load.execute();
            alreadyInit = true;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //PASS THE POSITION TO EXECUTE TO SELECT THE PROPER COURSE
                CurrentNotesActivity currentNotesActivity = (CurrentNotesActivity) getActivity();
                currentNotesActivity.loadNoteSelected(position);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.finalize();

    }
    private class LoadNotesAndUpdateListView extends AsyncTask< Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor= dbHelper.getAllNotes();

            return cursor;
        }
        @Override
        protected void onPostExecute(Cursor result){

            simpleCursorAdapter.swapCursor(result);

            // Toast.makeText(activityContext, result.getCount(), Toast.LENGTH_LONG).show();
        }
    }
}
