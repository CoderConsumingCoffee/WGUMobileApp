package com.aphart.wgumobileapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    String[] options = {"Current Term Details", "View/Edit All Terms",
            "View/Edit All Courses", "Current Notes", "Upcoming Assessments"};

    int currentApiVersion = Build.VERSION.SDK_INT;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] activityNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       //Always start off with a call to super
        super.onCreate(savedInstanceState);

        //Specify the layout to create
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Drawer
        mTitle = mDrawerTitle = getTitle();
        activityNames = getResources().getStringArray(R.array.main_menu_selector);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, activityNames));
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




        //Ask for permissions if SDK >= 23
        if(currentApiVersion >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                //No explanation needed.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                }, 200);
                // the last int is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        }





// Set up an Adapter Array to to set up the list.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getBaseContext() , R.layout.list_home_fragment, options);
        ListView listView = (ListView) findViewById(R.id.listViewFrag);
        listView.setAdapter(arrayAdapter);





        // set an onItemClickListener on the home page list view and override onItemClick to pass the position in
        // ListView to a switch statement and launch the corresponding intents.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

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

            }        });
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
}