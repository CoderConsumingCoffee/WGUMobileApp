package com.aphart.wgumobileapp;


import android.app.FragmentManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppCourseTableEntry.COURSE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCurrentCoursesFragment extends android.app.Fragment {

    private SimpleCursorAdapter cursorAdapter;
    private boolean initFlag;
    private ViewEditAllCoursesActivity viewEditAllCoursesActivity;
    private boolean allTerms = true;
    private String termNameForQuery ="";

    public ViewCurrentCoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_current_courses, container, false);
    }

    @Override
    public void onStart() {
        viewEditAllCoursesActivity = (ViewEditAllCoursesActivity) getActivity();
        super.onStart();
        //Load the list of courses from the database
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_home_fragment, null, new String[]{COURSE_NAME}, new int[]{ R.id.listHomeFragmentLayout}, 0);

        final ListView listView = (ListView) getView().findViewById(R.id.listViewForCurseAd);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(allTerms){viewEditAllCoursesActivity.launchCourse(position);
                }else{

                    SimpleCursorAdapter magic = (SimpleCursorAdapter)listView.getAdapter();
                    Cursor manny = magic.getCursor();
                    manny.moveToPosition(position);
                    String courseName = manny.getString(0);
                    LoadSelecetedAssociation tmi = new LoadSelecetedAssociation();
                   tmi.execute(courseName);
                }
            }
        });
        if (!initFlag && !allTerms){
            new myLoaderForTermAssociatedCourses().execute(termNameForQuery);

            initFlag = true;
        }

        if (!initFlag && allTerms) {
            new myLoader().execute();
            initFlag = true;

        }


        Button addCourse = (Button) getView().findViewById(R.id.addCourseButton);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewEditAllCoursesActivity.createNewCourse();
            }
        });

    }

    private class myLoader extends AsyncTask<Void, Void, Cursor>
    {
        private Cursor cursor;

        public myLoader() {

        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getCourseTitle();
            return cursor;

        }



        @Override
        protected void onPostExecute(Cursor cursor){
            cursorAdapter.changeCursor(cursor);
        }



    }
    public void associatedCourses(String termName){
        allTerms = false;
        termNameForQuery = termName;
        Log.e("CoursesLoaded", "AssociatedCourses method is called");
    }


    private class LoadSelecetedAssociation extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            int count = 0;
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            Cursor cursor = wguAppDatabaseHelper.getCourseTitle();
            if (cursor.moveToFirst()){
                if (cursor.getString(0).equals(strings[0])){
                    viewEditAllCoursesActivity.launchCourse(count);
                }
                while (cursor.moveToNext()){
                    count++;
                    if (cursor.getString(0).equals(strings[0])){
                        viewEditAllCoursesActivity.launchCourse(count);
                    }
                }
            }

            return null;
        }
    }

    private class myLoaderForTermAssociatedCourses extends AsyncTask<String, Void, Cursor>
    {
        private Cursor cursor;



        @Override
        protected Cursor doInBackground(String... strings) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getAssociatedCourses(strings[0]);
            Log.e("Loader For ASSCOURSES", "The term name is " + strings[0]);

                Log.e("Loader For ASSCOURSES", "The course name is " + (cursor == null));

            return cursor;
        }



        @Override
        protected void onPostExecute(Cursor cursors){
            cursorAdapter.changeCursor(cursor);
        }



    }


}
