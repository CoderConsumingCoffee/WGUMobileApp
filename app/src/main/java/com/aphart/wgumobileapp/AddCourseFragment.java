package com.aphart.wgumobileapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddCourseFragment extends android.app.Fragment {


    //PRIVATE VARIABLES
    private SimpleCursorAdapter cursorAdapter;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private final Calendar myCalendar = Calendar.getInstance();
    private View selectedDateView = null;
    private WGUAppDatabaseHelper dbhelper;
    private EditText courseName;
    private EditText startDate;
    private EditText endDate;
    private Spinner courseStatus;
    private EditText mentorName;
    private EditText mentorPrimaryPhone;
    private EditText mentorSecondPhone;
    private EditText mentorPrimaryEmail;
    private EditText mentorSecondEmail;
    private Spinner termAssociation;
    private boolean fragmentStarted;
    private boolean extras;
    private String[] noteFieldInfo = new String[13];
    private SimpleCursorAdapter termAdapter;
    private boolean init;
    private int termAssociationSelectorPosition;
    private String originalCourseName = "";
    private CheckBox warnStart;
    private CheckBox warnEnd;
    private int spinnerLoads;
    private Spinner noteSpinner;


    //SET THE LISTENER THAT WILL UPDATE THE NON-FOCUSABLE EDIT TEXT DATE FIELD
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(selectedDateView);
        }

    };

    public AddCourseFragment() {
        // Required empty public constructor
    }

    public AddCourseFragment(String[] strings) {
        noteFieldInfo = strings;
        extras = true;
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbhelper = new WGUAppDatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_course, container, false);
    }
    @Override
    public void onStart(){
        super.onStart();

        spinnerLoads = 0;
        //IF THERE IS NO AVAILABLE TERMS TO ASSIGN THE COURSES TO ALERT WITH A TOAST EVERY 10 SECONDS THAT THIS CANNOT BE SUBMITTED.
        //IF THIS WAS CALLED TO EXAMINE AN ALREADY EXISTING COURSE POPULATE ALL THE FIELDS WITH THE PREVIOUS INFORMATION FROM THE DATABASE

        courseName = (EditText) getActivity().findViewById(R.id.courseName);
        startDate = (EditText) getActivity().findViewById(R.id.courseStartDateSelector);
        endDate = (EditText) getActivity().findViewById(R.id.courseEndDateSelector);
        courseStatus = (Spinner) getActivity().findViewById(R.id.courseProgress);
        mentorName = (EditText) getActivity().findViewById(R.id.courseMentorName);
        mentorPrimaryPhone = (EditText) getActivity().findViewById(R.id.courseMentorPrimaryPhone);
        mentorSecondPhone = (EditText) getActivity().findViewById(R.id.courseMentorSecondPhone);
        mentorPrimaryEmail = (EditText) getActivity().findViewById(R.id.courseMentorEmail);
        mentorSecondEmail = (EditText) getActivity().findViewById(R.id.courseMentorecondEmail);
        termAssociation = (Spinner) getActivity().findViewById(R.id.termAssociation);
        warnStart = (CheckBox) getView().findViewById(R.id.remindStartCourse);
        warnEnd = (CheckBox) getView().findViewById(R.id.remindWeekPriorToCourseEnd);

       if(!init) {
           termAssociation.setAdapter(termAdapter = new SimpleCursorAdapter(getActivity(), R.layout.spinner, null, new String[]{WGUAppDBContract.WGUAppTermTableEntry.TERM_TITLE},
                   new int[]{R.id.spinnerText}));
           new LoadTermsForAssociation().execute();
       }




        //CANCEL SELECTED, SO BACK OUT
        Button cancelBtn = (Button) getView().findViewById(R.id.courseEditCancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                Activity activityManager = getActivity();
                activityManager.finish();
            }
        });


        //SUBMIT SELECTED. CHECK TO MAKE SURE THAT ALL THE FIELDS ARE ENTERED AND THAT THE END AND START DATES ARE NOT THE SAME.
        //IF THE NOTIFY WEEK PRIOR IS SELECTED SET AN ALARM MANAGER TO SEND A NOTIFICATION TO A BROADCAST RECEIVER.
        //THIS ALSO SERVES TO UPDATE, SO IF THERE WAS A NON-EMPTY ORIGINAL TITLE YOU NEED TO UPDATE THIS IN THE DATABASE

        Button submitBtn = (Button) getView().findViewById(R.id.courseEditSubmitButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());

                Cursor termstring = (Cursor) termAssociation.getSelectedItem();
                termstring.moveToPosition(termAssociation.getSelectedItemPosition());
                String termAssociationName = termstring.getString(0);
                if (originalCourseName.equals("")){


                    if (endDateCalendar != null && warnEnd.isChecked()) {
                        remind(endDateCalendar, "End Date for " + courseName.getText().toString(), "The term is ending a week from now!");
                    }
                    if (startDateCalendar != null && warnStart.isChecked()){
                        remind(startDateCalendar, "Start Date for "+ courseName.getText().toString(), "The term is starting a week from now, get ready!");
                    }
                    //TEST THE SYSTEM FOR VERIFICATION
                    alarmBroadcastTest();

                    boolean success = wguAppDatabaseHelper.addCourse(
                        courseName.getText().toString(),
                        startDate.getText().toString(),
                        endDate.getText().toString(),
                        courseStatus.getSelectedItem().toString(),
                        mentorName.getText().toString(),
                        mentorPrimaryPhone.getText().toString(),
                        mentorSecondPhone.getText().toString(),
                        mentorPrimaryEmail.getText().toString(),
                        mentorSecondEmail.getText().toString(),
                        termAssociationName,
                       ""+termAssociation.getSelectedItemPosition(),
                        ""+courseStatus.getSelectedItemPosition());

Log.e("TermAssociation", ""+termAssociationName);
                if (success){
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                    Activity activityManager = getActivity();
                    activityManager.finish();
                }
            }else{
                    wguAppDatabaseHelper.updateCourse(
                            courseName.getText().toString(),
                            startDate.getText().toString(),
                            endDate.getText().toString(),
                            courseStatus.getSelectedItem().toString(),
                            mentorName.getText().toString(),
                            mentorPrimaryPhone.getText().toString(),
                            mentorSecondPhone.getText().toString(),
                            mentorPrimaryEmail.getText().toString(),
                            mentorSecondEmail.getText().toString(),
                            ""+termAssociation.getSelectedItemPosition(),
                            ""+courseStatus.getSelectedItemPosition(),
                            termAssociationName,
                            originalCourseName);
                    Log.e("TermAssociation", ""+termAssociationName);
                    if (endDateCalendar != null && warnEnd.isChecked()) {
                        remind(endDateCalendar, "End Date for " + courseName.getText().toString(), "The term is ending a week from now!");
                    }
                    if (startDateCalendar != null && warnStart.isChecked()){
                        remind(startDateCalendar, "Start Date for "+ courseName.getText().toString(), "The term is starting a week from now, get ready!");
                    }

                    //TEST THE SYSTEM FOR VERIFICATION
                        alarmBroadcastTest();

                }
                wguAppDatabaseHelper.finalize();

                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                Activity activityManager = getActivity();
                activityManager.finish();
            }
        });

        //LAUNCH THE NOTE ACTIVITY. PUT THE NAME OF THE COURSE AS AN EXTRA.
        Button addNoteBtn = (Button) getView().findViewById(R.id.courseAddNote);
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewEditAllCoursesActivity  parentActivity = (ViewEditAllCoursesActivity) getActivity();
                parentActivity.launchAddNoteFragment();

            }
        });

        //DELETE THE COURSE. IF THERE IS NO SUCH COURSE ALERT THE USER WITH A TOAST AND ASK IF THEY CHANGED THE NAME OF THE COURSE
        Button deleteBtn = (Button) getView().findViewById(R.id.courseDeleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
                boolean success = wguAppDatabaseHelper.deleteAssessment(originalCourseName);
                if (success){
                    Toast.makeText(getActivity(), "The deletion was Successfully", Toast.LENGTH_LONG).show();
                    wguAppDatabaseHelper.finalize();
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStack();
                    Activity activityManager = getActivity();
                    activityManager.finish();

                }else {
                    Toast.makeText(getActivity(), "The Course is not in the DB. Did you submit this one yet?", Toast.LENGTH_LONG).show();
                }
            wguAppDatabaseHelper.finalize();
            }
        });

        Button launchNote = (Button) getView().findViewById(R.id.launchSelectedNote);
        launchNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTitle = noteSpinner.getSelectedItem().toString();
                new GetNotePosition().execute(noteTitle);
            }
        });

        //LISTEN FOR THE SELECTION OF THE SPINNER AND GET THE NOTE
        noteSpinner = (Spinner) getView().findViewById(R.id.noteSpinner);
        noteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                spinnerLoads = position;


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //LAUNCH THE ASSESSMENT WITH THE COURSE TITLE
        Button addAssementBtn = (Button) getView().findViewById(R.id.addAssesmentToCourse);
        addAssementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ViewEditAllCoursesActivity viewEditAllCoursesActivity = (ViewEditAllCoursesActivity) getActivity();
                viewEditAllCoursesActivity.launchAssesmentCreation();
            }
        });

        //SELECT A DATE FOR THE START OF THE COURSE. OBVIOUSLY NOT MUCH TO IT
        EditText startDateSelector = (EditText) getView().findViewById(R.id.courseStartDateSelector);
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

        //SELECT A DATE FOR THE END OF THE COURSE. OBVIOUSLY NOT MUCH TO IT
        EditText endDateSelector = (EditText) getView().findViewById(R.id.courseEndDateSelector);
        endDateSelector.setOnClickListener(new View.OnClickListener() {

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
        //LOAD COURSES TO SPINNER
        new LoadCoursesToSpinnerInBackground().execute();
        new LoadAssessmentsSpinner().execute();
        fragmentStarted = true;
        if(extras){
            internalFieldUpdate(noteFieldInfo[0],noteFieldInfo[1],noteFieldInfo[2],noteFieldInfo[3],noteFieldInfo[4],noteFieldInfo[5],noteFieldInfo[6],
                    noteFieldInfo[7],noteFieldInfo[8],noteFieldInfo[9],noteFieldInfo[10],noteFieldInfo[11]);
        }
    }

    //LOAD COURSES TO THE SPINNER USING AN ARRAY LIST TO FULFIL REQUIREMENT OF USING AN ARRAY LIST
    private class LoadCoursesToSpinnerInBackground extends AsyncTask<Void, Void, Object[]>
    {
        private Cursor cursor;

        @Override
        protected Object[] doInBackground(Void... voids) {
            ArrayList<String> arrayList = new ArrayList<>();
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity().getApplicationContext());
            cursor = wguAppDatabaseHelper.getNotesAssociatedWithCourse(originalCourseName);
            if(cursor.moveToFirst()){
                arrayList.add(cursor.getString(0));
                while (cursor.moveToNext()) {
                    arrayList.add(cursor.getString(0));
                }
            }
            return  arrayList.toArray();
        }
        @Override
        protected void onPostExecute(Object[] strings){
            Spinner noteSpinner = (Spinner) getView().findViewById(R.id.noteSpinner);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner, strings);
            noteSpinner.setAdapter(arrayAdapter);
        }



    }
    private class LoadAssessmentsSpinner extends AsyncTask<Void, Void, Object[]>
    {
        private Cursor cursor;


        @Override
        protected Object[] doInBackground(Void... voids) {
            ArrayList<String> arrayList = new ArrayList<>();
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity().getApplicationContext());
            cursor = wguAppDatabaseHelper.loadAssessmentsAssociatedWithCourse(originalCourseName);
            if(cursor.moveToFirst()){
                arrayList.add(cursor.getString(0));
                while (cursor.moveToNext()) {
                    arrayList.add(cursor.getString(0));
                }
            }
            return  arrayList.toArray();
        }
        @Override
        protected void onPostExecute(Object[] strings){
            Spinner noteSpinner = (Spinner) getView().findViewById(R.id.spinner);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.spinner, strings);
            noteSpinner.setAdapter(arrayAdapter);
        }



    }

    private class LoadTermsForAssociation extends AsyncTask<Void, Void, Cursor>
    {
        private Cursor cursor;

        @Override
        protected Cursor doInBackground(Void... voids) {

            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getTermTitleCursor();
            return  cursor;
        }
        @Override
        protected void onPostExecute(Cursor cursor){
           termAdapter.swapCursor(cursor);
            termAssociation.setSelection(termAssociationSelectorPosition);
        }



    }



    private class GetNotePosition extends AsyncTask<String, Void, Cursor>
    {
        private Cursor cursor;
        private int count = 0;


        @Override
        protected Cursor doInBackground(String... strings) {

            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getAllNotes();

            if (cursor.moveToFirst()){
                if(cursor.getString(0).equals(strings[0])){
                    Intent intent = new Intent(getActivity(), CurrentNotesActivity.class);
                    intent.putExtra("position", count);
                    startActivity(intent);
                }
                while (cursor.moveToNext()){
                    count++;
                    if (cursor.getString(0).equals(strings[0])){
                        Intent intent = new Intent(getActivity(), CurrentNotesActivity.class);
                        intent.putExtra("position", count);
                        startActivity(intent);
                    }

                }

            }


            return  cursor;
        }
        @Override
        protected void onPostExecute(Cursor cursor){

        }



    }


    //UPDATES THE APPROPRIATE EDIT TEXT FIELD WITH SELECTED DATE AND CLONES THE CALENDAR OBJECT INTO A SEPARATE REFERENCE FOR THE PURPOSE OF REMINDING
    //VIA THE REMINDER FUNCTION
    private void updateLabel(View selectedDateView){
        String myFormat = "MM/dd/yy"; //In which you need put here
        String sdf = (String) DateFormat.format(myFormat, myCalendar);
        EditText startDateSelector = (EditText) getView().findViewById(selectedDateView.getId());
        startDateSelector.setText(sdf);
        if (selectedDateView.getId() == R.id.termEndDateSelector){
            endDateCalendar = (Calendar) myCalendar.clone();
            Log.e("Calendar Test", endDateCalendar.toString());
        }else{
            startDateCalendar = (Calendar) myCalendar.clone();
            Log.e("Calendar Test", startDateCalendar.toString());
        }

    }

    //GO OVER THIS TO MAKE SURE IT'S SUITABLE FOR THE COURSES CLASS
    //Set up a future notification by assigning to AlarmManager and sending information to a broadcast
    private void remind(Calendar calendar, String title, String message){
        Intent alarmBroadcast = new Intent(getActivity(), WGUApplicationBroadcastReciever.class);
        alarmBroadcast.putExtra("title", title);
        alarmBroadcast.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 86400000 /* 1 week in milliseconds*/, pendingIntent);
    }



    private void internalFieldUpdate  (String name, String startDate1, String endDate1, String status, String mentorName, String mentorPrimaryPhone,
                                       String mentorSecondaryPhone, String mentorEmailOne, String mentorEmailTwo, String termAssociation, String termPos, String statPos){

        originalCourseName = name;
        courseName.setText(name);
        startDate.setText(startDate1);
        endDate.setText(endDate1);
        courseStatus.setSelection(Integer.parseInt(statPos));
        this.mentorName.setText(mentorName);
        this.mentorPrimaryPhone.setText(mentorPrimaryPhone);
        mentorSecondPhone.setText(mentorSecondaryPhone);
        mentorPrimaryEmail.setText(mentorEmailOne);
        mentorSecondEmail.setText(mentorEmailTwo);

        try {


            termAssociationSelectorPosition = Integer.parseInt(termPos);
        }catch (NumberFormatException nfe){
            Log.e("TermAssociation issue", nfe.toString() + " Term Position " + termPos);
        }
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
}
