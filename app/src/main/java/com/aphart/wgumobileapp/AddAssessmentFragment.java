package com.aphart.wgumobileapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddAssessmentFragment extends android.app.Fragment {


    //VARIABLES FOR THE CLASS

    private String assessmentTitle;
    private String assessmentOriginalTitle = "";
    private String assessmentDueDate;
    private String courseAssociation;
    private String assessmentType;
    private Calendar endDateCalendar;
    private ArrayAdapter cursorAdapter;
    private final Calendar myCalendar = Calendar.getInstance();
    private View selectedDateView = null;
    private int positionOfAssociatedCourse;
    private int positionOfAssessmentInDB;
    private boolean isAlreadyExisting;
    private boolean isCourseSpinnerLoaded;
    private EditText assessmentTitleField;
    private EditText assessmentDueDateField;
    private EditText startDateSelector;
    private Spinner assessmentTypeSpinner;
    private Spinner courseAssociationSpinner;
    private CheckBox notifyOneWeekCheckbox;
    private Button assessmentSubmitButton;
    private Button assessmentDeleteButton;
    private Button assessmentCancelButton;
    private Button addNoteButton;




    private int courseAssociationPosition = -1;

    //DATE PICKER LISTENER FOR SELECTING THE DUE DATE
    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth)
        {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(selectedDateView);
        }
    };


    //PUBLIC NON LIFECYCLE METHODS

    //CALLED BY ACTIVITY WHEN THE ASSESSMENT IS ALREADY IN EXISTENCE AND MUST BE VIEWED OR EDITED
    public void launchExistingAssessment(int position)
    {
        positionOfAssessmentInDB = position;
        isAlreadyExisting = true;
    }


    //PRIVATE NON LIFE CYCLE METHODS

    //Set up a future notification by assigning to AlarmManager and sending information to a broadcast
    private void remind(Calendar calendar, String title, String message){
        Intent alarmBroadcast = new Intent(getActivity(), WGUApplicationBroadcastReciever.class);
        alarmBroadcast.putExtra("title", title);
        alarmBroadcast.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 86400000 /* 1 week in milliseconds*/, pendingIntent);
    }

    private void alarmBroadcastTest(){
        Calendar calendarForTime = Calendar.getInstance();
        Date date = calendarForTime.getTime();
        Toast.makeText(getActivity(), date.toString(), Toast.LENGTH_LONG).show();

        Intent alarmBroadcast = new Intent(getActivity(), WGUApplicationBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, pendingIntent);
    }

    //UPDATE THE NON FOCUSABLE EDIT TEXT WITH THE SELECTED DATE IN A FORMATTED STRING AND CLONE THE CALENDAR OBJECT FOR USE IN NULL CHECKING AND REMINDER SETTING
    //TESTED GOOD!!
    private void updateLabel(View selectedDateView)
    {

        String myFormat = "MM/dd/yy"; //In which you need put here
        String formattedDate = (String) DateFormat.format(myFormat, myCalendar);
        startDateSelector.setText(formattedDate);

        if (selectedDateView.getId() == R.id.assesmentDateSelector)
        {
            endDateCalendar = (Calendar) myCalendar.clone();
            Log.e("Calendar Test", endDateCalendar.toString());
        }

    }

    private void backOutFragment(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        Activity activityManager = getActivity();
        activityManager.finish();
    }


    //INNER CLASSES FOR ASYNCHRONOUSLY WORKING WITH DB

    //INNER CLASS FOR LOADING COURSE TITLES AND UPDATING THE ADAPTER
    private class LoadCourseTitlesForSpinner extends AsyncTask< String, Void, Cursor> {
        private Cursor cursor;
        @Override
        protected Cursor doInBackground(String... strings) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getCourseTitle();
            return cursor;
        }


        @Override
        protected void onPostExecute(Cursor cursor){
            ArrayList<String> arrayList = new ArrayList<>();
            int count = 0;
            if (cursor.moveToFirst()){
                arrayList.add(cursor.getString(0));
                Log.e("Course Loader", cursor.getString(0));
                if (cursor.getString(0).equals(courseAssociation+"")){
                    courseAssociationPosition = 0;

                }
                count++;
                while (cursor.moveToNext()){
                    arrayList.add(cursor.getString(0));
                    count++;
                    if (cursor.getString(0).equals(courseAssociation+"")){
                        courseAssociationPosition = count;
                    }
                    Log.e("Course Loader", cursor.getString(0));

                }
            }

        courseAssociationSpinner.setAdapter(new ArrayAdapter(getActivity(), R.layout.spinner, arrayList.toArray()));
            courseAssociationSpinner.setSelection(positionOfAssociatedCourse);
            isCourseSpinnerLoaded = true;
        }

    }



    private class LoadExistingAssessment extends AsyncTask< Integer, Void, Cursor> {
        private Cursor assessmentInfoCursor;
        private Cursor coursesCursor;
        @Override
        protected Cursor doInBackground(Integer... integers) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            Cursor searchCursor = wguAppDatabaseHelper.loadAllAssessmentTitles();
            searchCursor.moveToPosition(integers[0]);
            String assessmentTitle = searchCursor.getString(0);
            assessmentInfoCursor = wguAppDatabaseHelper.getAllAssessmentInformation(assessmentTitle);
            coursesCursor = wguAppDatabaseHelper.getCourseTitle();

            assessmentInfoCursor.moveToFirst();
            courseAssociation = assessmentInfoCursor.getString(0);
            assessmentDueDate = assessmentInfoCursor.getString(2);
            assessmentOriginalTitle = assessmentInfoCursor.getString(3);

            if (coursesCursor.moveToFirst())
            {
                int count = 0;
                if (coursesCursor.getString(0).equals(courseAssociation)){
                    positionOfAssociatedCourse = 0;
                }
                while (coursesCursor.moveToNext()){
                    count++;
                    if (coursesCursor.getString(0).equals(courseAssociation)){
                        positionOfAssociatedCourse = count;
                    }

                }

            }



            return assessmentInfoCursor;
        }


        @Override
        protected void onPostExecute(Cursor cursor) {


            ArrayList arrayList = new ArrayList<>();
            /*
            Currently, the layout of the assessmentInfoCursor
            *   0: ASSESSMENT_COURSE_ASSOCIATION,
                1: ASSESSMENT_TYPE,
                2: ASSESSMENT_DATE,
                3: ASSESSMENT_TITLE,
                4: WGUAppDBContract.WGUAssessmentEntry._ID
            *
            * */

            assessmentDueDateField.setText(assessmentDueDate);

            assessmentTitle = assessmentOriginalTitle;
            assessmentTitleField.setText(assessmentTitle);

            if (cursor.getString(1).equals("Objective")) {
                assessmentTypeSpinner.setSelection(0);
            } else {
                assessmentTypeSpinner.setSelection(1);
            }

            /*if (assessmentInfoCursor.moveToFirst())
            {
                arrayList.add(assessmentInfoCursor.getString(0));

                while (assessmentInfoCursor.moveToNext())
                {
                    arrayList.add(assessmentInfoCursor.getString(0));
                }
            }
            cursorAdapter = new ArrayAdapter(getActivity(), R.layout.spinner, arrayList);
        }*/
            if (isCourseSpinnerLoaded)
            {
                courseAssociationSpinner.setSelection(positionOfAssociatedCourse);
            }

        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_assesment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //ASSIGN REFERENCE TO THE FRAGMENTS VIEWS
        try {
            assessmentTitleField     = (EditText) getView().findViewById(R.id.mannyman);
            assessmentDueDateField   = (EditText) getView().findViewById(R.id.assesmentDateSelector);
            assessmentTypeSpinner    = (Spinner) getView().findViewById(R.id.assesmentType);
            courseAssociationSpinner = (Spinner) getView().findViewById(R.id.assesmentCourseAssociation);
            notifyOneWeekCheckbox    = (CheckBox) getView().findViewById(R.id.assesmentNotifyOption);
            assessmentSubmitButton   = (Button) getView().findViewById(R.id.assesmentSubmitButton);
            assessmentDeleteButton   = (Button) getView().findViewById(R.id.assementDeleteButton);
            assessmentCancelButton   = (Button) getView().findViewById(R.id.assesmentCancelButton);
            addNoteButton            = (Button) getView().findViewById(R.id.assesmentAddNote);
            startDateSelector        = (EditText) getView().findViewById(R.id.assesmentDateSelector);
            }
            catch (NullPointerException npe)
            {
            Log.e(LogableConstants.ASSESSMENT, LogableConstants.ASS_VIEW + npe.toString());
            }

        //CHECK TO SEE IF THIS IS AN ALREADY EXISTING ASSIGNMENT, IF SO, LOAD THE FIELDS
        if (isAlreadyExisting)
        {
            new LoadExistingAssessment().execute(positionOfAssessmentInDB);
            Log.e(LogableConstants.ASSESSMENT, "Already existing assessment is being loaded.");
        }





        //MAKE THIS ONLY LAUNCHABLE FROM THE COURSE'S TAB SO THAT THERE IS ALWAYS A COURSE THAT CAN BE ASSOCIATED WITH IT.
        //CANNOT ADD ONE DIRECTLY FROM THE DISPLAY LIST OF ASSESSMENTS, BUT CAN MODIFY OR DELETE


        //LOAD A LIST OF COURSES TO ASSOCIATE WITH THE ASSESSMENT


        //SET THE EDIT TEXT FOR THE START DATE. THERE IS NO END DATE, JUST A DUE DATE
        startDateSelector.setOnClickListener(new View.OnClickListener() {

            //AddTermFragment.this
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity() , date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                selectedDateView = v;
            }
        });


        //LAUNCH THE ADD NOTE, BUT REQUIRE THAT AN ASSOCIATED COURSE IS SELECTED FOR FEEDING TO THE NOTE.
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateNotes updateNotes = (UpdateNotes) getActivity();
                updateNotes.addNote();
            }
        });

        //CANCELS THE LAUNCH AND BACKS OUT.
        assessmentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backOutFragment();
            }
        });


        //DELETES THE ENTRY ALERT THE USER IF IT CANNOT BE DELETED, OR IF IT IS DELETED. THE METHOD DOES RETURN A BOOLEAN
        assessmentDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
                boolean success = wguAppDatabaseHelper.deleteAssessment(assessmentOriginalTitle);

                if (success){
                    Toast.makeText(getActivity(), "The deletion was Successfully", Toast.LENGTH_LONG).show();

                    backOutFragment();

                }else {
                    Toast.makeText(getActivity(), "The Assessment is not in the DB. Did you submit this one yet?", Toast.LENGTH_LONG).show();
                }

                wguAppDatabaseHelper.finalize();
            }
        });

        //SUBMIT THE NEW INFORMATION IF ALL FIELDS ARE FILLED OUT. IF NOTIFY IS SELECTED SET A REMINDER FOR THE 1 WEEK PRIOR TO DUE DATE.
        //UPDATE IF THE ORIGINAL TITLE IS NOT EMPTY
        assessmentSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    Log.e("Submission Click", "Original Title is : " + assessmentOriginalTitle);
                    //       Log.e("Submission Click", courseAssociationSpinner.getSelectedItem().toString());
                    WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());

                    if ((assessmentOriginalTitle +"").equals(""))
                    {
                    EditText edt = (EditText)getView().findViewById(R.id.mannyman);
                    wguAppDatabaseHelper.submitAssesment(edt.getText().toString(),
                            assessmentDueDateField.getText().toString(),
                            assessmentTypeSpinner.getSelectedItem().toString(),
                            courseAssociationSpinner.getSelectedItem().toString());
                    Log.e("Submission assessment", "Selected Type " + assessmentTypeSpinner.getSelectedItem().toString());
                    Log.e("Submission assessment", "Selected Course " + courseAssociationSpinner.getSelectedItem().toString());
                        if (endDateCalendar != null && notifyOneWeekCheckbox.isChecked()) {
                            remind(endDateCalendar, "End Date for " + assessmentDueDateField.getText().toString(), "The assessment is a week from now!");
                        }
                        //TEST THE SYSTEM FOR VERIFICATION
                        alarmBroadcastTest();

                    }
                    else
                    {
                            wguAppDatabaseHelper.updateAssesment(
                            assessmentTitleField.getText().toString(),
                            assessmentDueDateField.getText().toString(),
                            assessmentTypeSpinner.getSelectedItem().toString(),
                            courseAssociationSpinner.getSelectedItem().toString(),

                                    assessmentOriginalTitle);
                        if (endDateCalendar != null && notifyOneWeekCheckbox.isChecked()) {
                            remind(endDateCalendar, "End Date for " + assessmentDueDateField.getText().toString(), "The assessment is a week from now!");
                            //TEST THE SYSTEM FOR VERIFICATION
                            alarmBroadcastTest();

                        }

                    }

                    wguAppDatabaseHelper.finalize();
                    backOutFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "You need select a course. If one is not available, then you need to make one.", Toast.LENGTH_LONG).show();
                }
            }
        });



        new LoadCourseTitlesForSpinner().execute();

    }




    @Override
    public void onDetach() {
        super.onDetach();

    }





    @Override
    public void onPause() {
        super.onPause();


    }



    }
