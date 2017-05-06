package com.aphart.wgumobileapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UpcomingAssessmentsActivity extends AppCompatActivity implements ShowableAssociatedCourses, UpdateNotes {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_assessments);
        if(savedInstanceState == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.upcomingAssessmentFrame, new ViewUpcomingAssesmentsFragment());
            fragmentTransaction.commit();

        }











        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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
    }
//drawer code
    public void launchSelectedAssessment(int position){
        AddAssessmentFragment addAssesmentFragment = new AddAssessmentFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.upcomingAssessmentFrame, addAssesmentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        addAssesmentFragment.launchExistingAssessment(position);
    }

    public void addNote(){
        AddNoteFragment addNoteFragment = new AddNoteFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.upcomingAssessmentFrame, addNoteFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void showAssociatedCourses(String termName){
        Intent intent = new Intent(this, ViewEditAllCoursesActivity.class);
        intent.putExtra("title", termName);
        startActivity(intent);
    }
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
}
