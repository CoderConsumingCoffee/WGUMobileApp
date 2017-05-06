package com.aphart.wgumobileapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppCourseTableEntry.*;

public class ViewEditAllCoursesActivity extends AppCompatActivity implements UpdateNotes //implements LoaderManager.LoaderCallbacks<Cursor>
{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private ArrayAdapter arrayAdapter;
    private String [] projection = {COURSE_NAME};
    private SimpleCursorAdapter cursorAdapter;
    private String[] noteFieldInfo = new String[13];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_all_courses);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        Bundle callingBundle = intent.getExtras();

        if(callingBundle.get("title") != null && savedInstanceState == null){
            Log.e("CoursesLoaded", "The proper thing in intend is called.");
            ViewCurrentCoursesFragment viewCurrentCoursesFragment = new ViewCurrentCoursesFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.viewEditAllcoursesFrame, viewCurrentCoursesFragment);
            fragmentTransaction.commit();
            viewCurrentCoursesFragment.associatedCourses(callingBundle.getString("title"));
            Log.e("CoursesLoaded", "The title from bundle is " + callingBundle.getString("title"));
        }

        if (savedInstanceState == null && callingBundle.get("title") == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.viewEditAllcoursesFrame, new ViewCurrentCoursesFragment());
            fragmentTransaction.commit();
        }

        //Drawer
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.main_menu_selector);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.open,  /* "open drawer" description for accessibility */
                R.string.close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }
        //Trial Courses
        //new WGUAppDatabaseHelper(this).addCourse("Course Name", "sdfajk", "asdf ", "fdsaf", "afsd", "asfd ", "afsd","asdf","asdf","safd","asdf","asdf");





    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void launchAssesmentCreation(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewEditAllcoursesFrame, new AddAssessmentFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    //drawer code

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }


        private void selectItem(int position) {
            // update the main content by replacing fragments
            switch (position) {
                case 0:
                    Intent currentTermDetainsIntent = new Intent(getBaseContext(), CurrentTermDetailsActivity.class);
                    startActivity(currentTermDetainsIntent);
                    break;
                case 1:
                    Intent viewEditAllTermsIntent = new Intent(getBaseContext(), ViewEditAllTermsActivity.class);
                    startActivity(viewEditAllTermsIntent);
                    break;
                case 2:
                    Intent viewEditAllCoursesIntent = new Intent(getBaseContext(), ViewEditAllCoursesActivity.class);
                    viewEditAllCoursesIntent.putExtra("Tiger", "The intent successfully started and retrieved the information");
                    startActivity(viewEditAllCoursesIntent);
                    break;
                case 3:
                    Intent coursesNotesIntent = new Intent(getBaseContext(), CurrentNotesActivity.class);
                    startActivity(coursesNotesIntent);
                    break;
                case 4:
                    Intent upcomingAssessmentsIntent = new Intent(getBaseContext(), UpcomingAssessmentsActivity.class);
                    startActivity(upcomingAssessmentsIntent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Case selection out of bouds, REVISE", Toast.LENGTH_LONG).show();
            }

            // update selected item and title, then close the drawer
        }

    }

    public void createNewCourse(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewEditAllcoursesFrame ,new AddCourseFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void launchAddNoteFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewEditAllcoursesFrame ,new AddNoteFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void launchCourse(int position){
        new LoadCourseForReviewAndUpdate().execute(position);
    }
    private class LoadCourseForReviewAndUpdate extends AsyncTask< Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {

            Cursor cursor = new WGUAppDatabaseHelper(getApplication()).getCourseAllEntry();
            cursor.moveToFirst();
            cursor.move(integers[0]);
            for (int i = 0; i < 12; i++){
                noteFieldInfo[i] = cursor.getString(i);
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            //REPLACE THE ACTIVITY WITH THE ADD NOTE FRAGMENT
            AddCourseFragment addNoteFragment = new AddCourseFragment(noteFieldInfo);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.viewEditAllcoursesFrame, addNoteFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();




        }
    }
    public void addNote(){
        AddNoteFragment addNoteFragment = new AddNoteFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewEditAllcoursesFrame, addNoteFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}