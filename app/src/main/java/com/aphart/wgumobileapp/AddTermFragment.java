package com.aphart.wgumobileapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.TERM_END_DATE;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.TERM_START_DATE;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.TERM_TABLE_NAME;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.TERM_TITLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTermFragment extends android.app.Fragment {


    //VARIABLES
    private View selectedDateView = null;
    private Calendar endDateCalendar;
    private Calendar startDateCalendar;
    private String title;
    private final int COURSES_ASSOCTIATED = 0;
    private final int CAN_DELETE_NO_ASSOCIATIONS = 1;
    private final int NO_SUCH_TERM_IN_DB = 2;
    private final Calendar myCalendar = Calendar.getInstance();
    private EditText termName;
    private EditText startDate;
    private EditText endDate;
    private CheckBox warnStart;
    private CheckBox warnEnd;
    private String originalTitle = "";
    private boolean editTermBoolean;
    private int termSearchPosition;
    private CheckBox currentTermCheckBox;

    //DATE PICKER LISTENER
    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(selectedDateView);
            //I should clone myCalendar and test if it's endDate, or startDate based upon the id of the selectedDateView in updateLabel
        }

    };


    public AddTermFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //CREATE INTENT REPRESENTING CALLING INTENT
        Intent intent = getActivity().getIntent();

        //IF THIS IS CALLED FROM A LAUNCHING INTENT GET THE STRINGS AND POPULATE THE FIELDS



    }

    public void setTermFields(String... termFields){
        termName.setText(termFields[0]);
        startDate.setText(termFields[1]);
        endDate.setText(termFields[2]);
        originalTitle = termName.getText().toString();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment





        return inflater.inflate(R.layout.fragment_add_term, container, false);
    }


    @Override
    public void onStart(){
        super.onStart();


        warnEnd = (CheckBox) getView().findViewById(R.id.warnEnd);
        warnStart = (CheckBox) getView().findViewById(R.id.warnStart);
        termName = (EditText) getView().findViewById(R.id.termName);
        startDate = (EditText) getView().findViewById(R.id.termStartDateSelector);
        endDate = (EditText) getView().findViewById(R.id.termEndDateSelector);
        currentTermCheckBox = (CheckBox) getView().findViewById(R.id.currentTermCheck);

        //alarmBroadcastTest();

        //CANCEL BUTTON. BACK OUT OF THE FRAGMENT.
        Button cancelBtn = (Button) getView().findViewById(R.id.termCancelButton);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backOutFrag();
            }
        });


         Button associatedCourses = (Button) getView().findViewById(R.id.associatedCourses);
        associatedCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ShowableAssociatedCourses showableAssociatedCourses = (ShowableAssociatedCourses) getActivity();
                showableAssociatedCourses.showAssociatedCourses(originalTitle);
            }
        });

        //SUBMIT BUTTON IS BOTH UPDATE AND SUBMIT. IF THERE IS AN ORIGINAL TITLE FOR THE TERM THEN THIS WILL SERVE TO UPDATE THE ENTRY IN THE DATABASE
        //IF NO ORIGINAL TITLE THEN THIS WILL SERVE TO SUBMIT THE INITIAL ENTRY IN THE DATABASE.

        Button submitButton = (Button) getView().findViewById(R.id.termSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WGUAppDatabaseHelper wgu = new WGUAppDatabaseHelper(getActivity());

                //TEST TO MAKE SURE THAT NO FIELDS ARE EMPTY AND THAT THE TWO DATE FIELDS ARE NOT MATCHING ON THE SAME DAY.
                //IF THE REMIND CHECK BOXES ARE CHECKED CREATE THE REMINDERS FOR 1 WEEK PRIOR
                if (!endDate.getText().toString().equals(startDate.getText().toString()) && !endDate.getText().toString().equals(termName.getText().toString()) &&
                !startDate.getText().toString().equals(endDate.getText().toString()) && !endDate.getText().toString().equals("") && !startDate.getText().toString().equals("") &&
                        originalTitle.equals("")) {

                    wgu.addTerm(termName.getText().toString(), startDate.getText().toString(), endDate.getText().toString(), ""+currentTermCheckBox.isChecked());

                    if (endDateCalendar != null && warnEnd.isChecked()) {
                        remind(endDateCalendar, "End Date for " + termName.getText().toString(), "The term is ending a week from now!");
                    }
                    if (startDateCalendar != null && warnStart.isChecked()){
                        remind(startDateCalendar, "Start Date for "+ termName.getText().toString(), "The term is starting a week from now, get ready!");
                    }
                    //TEST THE SYSTEM FOR VERIFICATION
                    alarmBroadcastTest();
                    //TEST THE SYSTEM FOR VERIFICATION
                    alarmBroadcastTest();

                    wgu.finalize();
                    backOutFrag();
                }else if (!endDate.getText().toString().equals(startDate.getText().toString()) && !endDate.getText().toString().equals(termName.getText().toString()) &&
                        !startDate.getText().toString().equals(endDate.getText().toString()) && !endDate.getText().toString().equals("") && !startDate.getText().toString().equals(""))
                {
                    if (endDateCalendar != null && warnEnd.isChecked()) {
                        remind(endDateCalendar, "End Date for " + termName.getText().toString(), "The term is ending a week from now!");
                    }
                    if (startDateCalendar != null && warnStart.isChecked()){
                        remind(startDateCalendar, "Start Date for "+ termName.getText().toString(), "The term is starting a week from now, get ready!");
                    }
                    wgu.updateTerm(termName.getText().toString(), startDate.getText().toString(), endDate.getText().toString(), originalTitle, ""+currentTermCheckBox.isChecked());
                    wgu.finalize();
                    backOutFrag();
                }else
                {
                    Toast.makeText(getActivity(), "You need to fill out all the fields, and the start and end date cannot be the same.", Toast.LENGTH_LONG).show();
                }


            }
        });

        //DELETE THE TERM AS LONG AS THERE ARE NO ASSOCIATED COURSES
        Button deleteButton = (Button) getView().findViewById(R.id.deleteTermButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText termtitle = (EditText) getView().findViewById(R.id.termName);
                CanIDeleteThisTerm canIDelete = new CanIDeleteThisTerm();
                Log.e("deletion", "Term title to delete" + termtitle.getText().toString());
                title = termtitle.getText().toString();
                canIDelete.execute(title);


            }
        });



        //SELECT A START DATE
        EditText startDateSelector = (EditText) getView().findViewById(R.id.termStartDateSelector);
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

        //SELECT AN END DATE
        EditText endDateSelector = (EditText) getView().findViewById(R.id.termEndDateSelector);
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
    //DB TEST
        /*
        WGUAppDatabaseHelper dbhelper = WGUAppDatabaseHelper.getInstance(getActivity().getApplicationContext());
        WGUAppDatabaseHelper dbhelper2 = WGUAppDatabaseHelper.getInstance(getActivity().getApplicationContext());
        WGUAppDatabaseHelper dbhelper3 = WGUAppDatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbhelper3.addTerm("Title", "Start", "End");
        Cursor c = dbhelper.getTermAllCursor();
        c.moveToFirst();
        String termTit = c.getString(c.getColumnIndex(WGUAppDBContract.WGUAppTermTableEntry.TERM_TITLE));
        Toast.makeText(getActivity(), termTit, Toast.LENGTH_LONG).show();
*/

    if (editTermBoolean){
        new LoadInformationToFields().execute(termSearchPosition);
    }


    }

    public void backOutFrag(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        Activity activityManager = getActivity();
        activityManager.finish();
    }

    public void loadTerm(int position){
        termSearchPosition = position;
        editTermBoolean = true;
    }

    //inner class extending async that tests if you can delete this term
    //TEST IF THE TERM CAN BE DELETED. NO IF THERE IS AN ASSOCIATED COURSE. NO IF THERE IS NO MATCH. ELSE DELETE. COMMUNICATED EITHER OF THESE
    //WITH A TOAST FOR THE USER.
    //TESTED GOOD!!
    private class CanIDeleteThisTerm extends AsyncTask< String, Void, Integer[]>{

        @Override
        protected Integer[] doInBackground(String... strings) {
            int result;
            int increment = 0;
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            Cursor cursor = wguAppDatabaseHelper.getAssociatedCourses(strings[0]);
            if(cursor.moveToFirst()){
                wguAppDatabaseHelper.finalize();
                return new Integer[]{COURSES_ASSOCTIATED};
            }else{
                cursor = wguAppDatabaseHelper.getTermTitleCursor();
                if (cursor.moveToFirst()){
                    if (cursor.getString(0).equals(strings[0])) {
                        Log.e("deletion", "1st row at with column 0 " + cursor.getString(0));
                     wguAppDatabaseHelper.finalize();

                        return new Integer[]{CAN_DELETE_NO_ASSOCIATIONS};
                    }
                    while (cursor.moveToNext()){

                        if (cursor.getString(0).equals(strings[0])){
                            Log.e("deletion", "Row " + increment + " at with column 0 " + cursor.getString(0).toString() + " strings [0] = " + strings[0]);
                            wguAppDatabaseHelper.finalize();
                            return new Integer[]{CAN_DELETE_NO_ASSOCIATIONS};
                        }
                        increment++;
                    }
                }
                wguAppDatabaseHelper.finalize();
                Log.e("deletion", "No such term in DB");
                return new Integer[]{NO_SUCH_TERM_IN_DB};
            }


        }
        @Override
        protected void onPostExecute(Integer... result){
            switch (result[0]){
                case COURSES_ASSOCTIATED: {
                    Toast.makeText(getActivity(), "There are still courses belonging to this term. To" +
                            " delete you need to delete those course first", Toast.LENGTH_LONG).show();
                }break;
                case CAN_DELETE_NO_ASSOCIATIONS: {
                    WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
                        Log.e("deletion", "Can Delete and will attempt");
                    if (wguAppDatabaseHelper.deleteTerm(title)){
                        Toast.makeText(getActivity(), "Succeesfully Deleted", Toast.LENGTH_LONG).show();
                    }
                }break;
                case NO_SUCH_TERM_IN_DB: {
                    Toast.makeText(getActivity(), "There is no such term in the Database. Did you change the name or not submit this one yet?", Toast.LENGTH_LONG).show();
                }break;
            }
        }
    }



   //THE TESTER FOR ALARM BROADCAST. WORKS!
    private void alarmBroadcastTest(){
        Calendar calendarForTime = Calendar.getInstance();
        Date date = calendarForTime.getTime();
        Toast.makeText(getActivity(), date.toString(), Toast.LENGTH_LONG).show();

        Intent alarmBroadcast = new Intent(getActivity(), WGUApplicationBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000, pendingIntent);
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

    //Set up a future notification by assigning to AlarmManager and sending information to a broadcast
    private void remind(Calendar calendar, String title, String message){
        Intent alarmBroadcast = new Intent(getActivity(), WGUApplicationBroadcastReciever.class);
        alarmBroadcast.putExtra("title", title);
        alarmBroadcast.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 86400000 /* 1 week in milliseconds*/, pendingIntent);
    }
    private class LoadInformationToFields extends AsyncTask<Integer, Void, String[]>
    {
        private Cursor cursor;

      /*Blow is the order of retrieval
        TERM_TITLE,
        TERM_START_DATE,
        TERM_END_DATE,
        TERM_CURRENT,*/
        @Override
        protected String[] doInBackground(Integer... integers) {
            WGUAppDatabaseHelper wguAppDatabaseHelper = new WGUAppDatabaseHelper(getActivity());
            cursor = wguAppDatabaseHelper.getTermAllCursor();
            String[] strings = new String[4];
            cursor.moveToPosition(integers[0]);
            for (int i =0; i < 4; i++){
                strings[i] = cursor.getString(i);
            }


            return strings;
        }



        @Override
        protected void onPostExecute(String[] result){
            originalTitle = result[0];
            termName.setText(result[0]);
            startDate.setText(result[1]);
            endDate.setText(result[2]);
            Log.e("checkBox", "IS: " + result[3]);
            if (result[3].equals("true")){
                CheckBox checkBox = (CheckBox) getView().findViewById(R.id.currentTermCheck);
                checkBox.setChecked(true);
            }

        }



    }
}
