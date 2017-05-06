package com.aphart.wgumobileapp;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.UriPermission;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends android.app.Fragment {

    //VARIABLES FOR THE CLASS
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri photoFileUri;
    private WGUAppDatabaseHelper dbHelper;
    private AddNoteFragment selfFragment = this;
    private String originalTitle = "";
    private File fileOfPhoto;
    private ImageView noteImageView;
    private Spinner associatedCourseSpinner;
    private EditText noteEditText;
    private EditText noteTitle;
    private boolean fragmentStartedFlag = false;
    private int previouslyAssociatedCourse = 0;
    private String associatedCourse = "";
    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        //FIND OUT IF THE NOTE WAS CALLED BY AN INTENT THAT HAD PUT EXTRAS.
        //IF THERE WERE EXTRAS THERE FILL OUT THE FIELDS IN THE NOTE
        //HAVE AN ORIGINAL TITLE FOR UPDATES
        //NEED TO LOAD THE PHOTO FROM THE FILE, THOUGH




        //SETTING THE DATABASE HELPER FOR THE WHOLE CLASS
        dbHelper = new WGUAppDatabaseHelper(getActivity());

            new SetCourseTitlesToSpiner().execute(associatedCourse);



    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //dbHelper = new WGUAppDatabaseHelper(getActivity().getApplication().getApplicationContext());
        return inflater.inflate(R.layout.fragment_add_note, container, false);



    }

    @Override
    public void onStart(){
        super.onStart();

        fragmentStartedFlag = true;

        noteTitle = (EditText)getView().findViewById(R.id.noteTitle);
        noteEditText = (EditText)getView().findViewById(R.id.noteEditText);
        associatedCourseSpinner = (Spinner)getView().findViewById(R.id.associatedCourseSpinner);
        noteImageView = (ImageView) getView().findViewById(R.id.noteImageView);

        Button shareButton = (Button) getView().findViewById(R.id.shareNote);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str;
                if (!(str = noteEditText.getText().toString()).equals("")){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
            }

        });


        //TAKE A PHOTO AND SET IT TO THE IMAGE VIEW
        Button addPhoto = (Button) getView().findViewById(R.id.noteAddPictureBtn);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        //REMOVE THE NOTE FROM THE DATABASE
        //DELETE THE FILE SEPERATELY BY REFERENCING THE CONTEXT AND THEN PASSING FILE TO THE DELETE METHOD
        Button deleteNoteButton = (Button) getView().findViewById(R.id.deleteNoteButton);
        deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!originalTitle.equals("")){
                //THIS MUST BE IN THE DATABASE

                if(dbHelper.deleteNote(originalTitle)){
                    try{
                        getActivity().deleteFile(fileOfPhoto.toString());
                    }catch (Exception e){
                        Log.e("Deletion", "Failed to delete: " + fileOfPhoto);
                    }
                    Toast.makeText(getActivity(), "Successfully deleted the note from the database", Toast.LENGTH_LONG).show();

                    backOutOfFragment();
                }else{
                    Toast.makeText(getActivity(), "Oh, snap, one this didn't delete anything. This really shouldn't have happened and might just be a permission thing", Toast.LENGTH_LONG).show();
                }
            }else if (dbHelper.deleteNote(noteTitle.getText().toString())){
                    //MIGHT BE IN THE DATABASE, SO GIVE IT A TRY AND COMMUNICATE THE RESULT
                    try{
                        getActivity().deleteFile(fileOfPhoto.toString());
                    }catch (Exception e){
                        Log.e("Deletion", "Failed to delete: " + fileOfPhoto);
                    }
                    Toast.makeText(getActivity(), "Successfully deleted the note from the database", Toast.LENGTH_LONG).show();

                    backOutOfFragment();

            }else {
                    //AT THIS POINT THE STATEMENTS EVALUATED AND NOTHING WAS DELETED, SO THIS IS OBVIOUSLY A NEW NOTE
                    Toast.makeText(getActivity(), "There isn't anything to delete, must be a new Note", Toast.LENGTH_LONG).show();

                }

                dbHelper.finalize();
            }
        });

        //CHECK THAT ALL THE FIELDS ARE FILLED OUT EXCEPT THE PHOTO, THEN SUBMIT THE FIELDS TO THE DATABASE
        //IF ORIGINAL TITLE IS NOT EMPTY THEN UPDATE THE ENTRY IN THE DATABASE INSTEAD.
        //EITHER WAY CLOSE THE FRAGMENT OR ALERT THE USER OF SUCCESS
        Button submit = (Button) getView().findViewById(R.id.noteSubmitBtn);
        submit.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){
               //At the contents of the note to the note DB table and destroy the fragment.

               Log.e("Original Title", "Original Title is " + originalTitle);
               if (!originalTitle.equals("")){

                   String photoFileString;
                   if(photoFileUri != null)
                   {
                       photoFileString = photoFileUri.toString();
                   }else{
                       photoFileString = "noPhoto";
                   }
                   dbHelper.updateNote(noteTitle.getText().toString(),
                           noteEditText.getText().toString(),
                           photoFileString, associatedCourseSpinner.getSelectedItem().toString(), originalTitle);
                   Log.e("Original Title", "The note is updated for  " + originalTitle);
                   //REMOVE THE FRAGMENT
                   FragmentManager fm = getFragmentManager();
                   Activity activityManager = getActivity();
                   activityManager.finish();
                   fm.popBackStack();
               }

               if (originalTitle.equals("")) {
                   String photoFileString;
                   if(photoFileUri != null)
                   {
                       photoFileString = photoFileUri.toString();
                   }else{
                       photoFileString = "noPhoto";
                   }
                   dbHelper.addNote(noteTitle.getText().toString(),
                                    noteEditText.getText().toString(),
                                    photoFileString, associatedCourseSpinner.getSelectedItem().toString());

                   //REMOVE THE FRAGMENT
                   FragmentManager fm = getFragmentManager();

                   Activity activityManager = getActivity();


                   activityManager.finish();
                   fm.popBackStack();
               }

               //startActivity(coursesNotesIntent);


           }
        });

        //BACK OUT OF THE FRAGMENT
        Button cancelBtn = (Button) getView().findViewById(R.id.noteCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backOutOfFragment();
            }
        });



    }


    private void backOutOfFragment(){
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        Activity activityManager = getActivity();
        activityManager.finish();
    }

    public void updateFragmentFields(String... strings){
        associatedCourse = strings[3];
        AssignValuesToFields avtf = new AssignValuesToFields();
        avtf.execute(strings);
    }

    //DISPATCH AN INTENT FOR LAUNCHING THE THE PHOTO CAPTURE AND GET THE RESULT.
    private void dispatchTakePictureIntent() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFileUri = getOutPutMediaUri(); // create a file to save the image

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);// set the image file name
        Toast.makeText(getActivity().getApplicationContext(), "Image saved to:\n" +
               photoFileUri.toString(), Toast.LENGTH_LONG).show();

     startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     /*   if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent


                ImageView image = (ImageView)getView().findViewById(R.id.noteImageView);
                image.setImageURI(data.getData());
                Toast.makeText(getActivity().getApplicationContext() , data.getData().toString() ,Toast.LENGTH_LONG).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
*/

        Toast.makeText(getActivity().getApplicationContext(), resultCode + " " + requestCode, Toast.LENGTH_LONG).show();
        //Test whether the file was created in the app's directory. If is null then don't save directory to the DB.
        File file = new File(photoFileUri.toString());
        if (file.exists()) {
            noteImageView.setImageURI(photoFileUri);
        }else{
            photoFileUri = null;
            Log.e("message", "The activity was canceled");
        }
    }


    private static Uri getOutPutMediaUri(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "WGUApp");
        //Create if does not exist
        if(!mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("WGUApp", "failed to create directory " + mediaStorageDir.toString());
            }
        }
        //Create media file name

        File mediaFile;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        Log.e("URIcreated", Uri.fromFile(mediaFile).toString());

        return Uri.fromFile(mediaFile);
    }




    private class SetCourseTitlesToSpiner extends AsyncTask< String, Void, ArrayList<String>> {
        private String previouslySelectedCourse;
        private int positionInArray = -1;
        private boolean found;
        @Override
        protected ArrayList doInBackground(@Nullable String... strings) {
            //THE RETURNED OBJECT ARRAY WILL HAVE CURSOR AT 0 AND STRING AT 1. LENGTH 2;
            ArrayList<String> arrayList = new ArrayList<>();
            if (strings[0] != null ) {
            previouslySelectedCourse = strings[0];
            }else{
                previouslySelectedCourse = null;
            }

            Cursor courseNames = dbHelper.getCourseTitle();
            if(courseNames.moveToFirst()){
                String courseTitle =courseNames.getString(0);
                arrayList.add(courseTitle);
                matchFound(courseTitle);
                while (courseNames.moveToNext()){
                    courseTitle = courseNames.getString(0);
                    arrayList.add(courseTitle);
                    matchFound(courseTitle);}
            }
            return arrayList;
        }
        @Override
        protected void onPostExecute(ArrayList<String> result){
            String[] stringArray = new String[result.size()];
            stringArray = result.toArray(stringArray);


           associatedCourseSpinner.setAdapter(new ArrayAdapter<String>(
                   getActivity(),
                   R.layout.spinner,
                   stringArray));

            if (found){
                associatedCourseSpinner.setSelection(positionInArray);
            }

        }
        private void matchFound(String match) {
            if (match.equals("")) {
                return;
            }

            if (!found) {
                found = match.equals(previouslySelectedCourse);

                ++positionInArray;
            }
        }

        }

   /* private class CanIDeleteThisTerm extends AsyncTask< String, Void, Integer[]> {

        @Override
        protected Integer[] doInBackground(String... strings) {


        }
        @Override
        protected void onPostExecute(Integer... result){

        }
    }
*/

    private class AssignValuesToFields extends AsyncTask< String, Void, String[]> {
        int count = 0;
        @Override
        protected String[] doInBackground(String... strings) {
        while (!fragmentStartedFlag){

        }
            Cursor cursor = dbHelper.getAllNotes();

            if (cursor.moveToFirst()){
                if(strings[3].equals(cursor.getString(3))){
                    previouslyAssociatedCourse = count;
                }
                while (cursor.moveToNext()){
                    count++;
                    if (strings[3].equals(cursor.getString(3))){
                        previouslyAssociatedCourse = count;
                    }
                }
            }
            return strings;
        }


        @Override
        protected void onPostExecute(String... result){
            noteTitle.setText(result[0]);
            Log.e("Result Text", "Result text: " + result[0]);
            originalTitle = result[0];
            noteEditText.setText(result[1]);
            noteImageView.setImageURI(Uri.parse(result[2]));
            associatedCourseSpinner.setSelection(previouslyAssociatedCourse);

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



