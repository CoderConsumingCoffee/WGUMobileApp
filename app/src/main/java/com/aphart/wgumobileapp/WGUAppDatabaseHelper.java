package com.aphart.wgumobileapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppCourseTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTableConstructors.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.NoteTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAssesmentEntry.*;

/**
 * Created by aphart on 7/18/2016.
 */
public class WGUAppDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 12;
    private static Context singletonContext;
    //private static WGUAppDatabaseHelper wguAppDatabaseHelper = null;
    //The DB object will be used and open for the life of the
    private SQLiteDatabase db;
    //Declaration of strings for creating/updating/etc of the DB tables
    public static final String DATABASE_NAME = "WGUAppDatabase.db";



//The singleton DB OpenHelper for the app
    public WGUAppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        singletonContext = context;
        db = this.getWritableDatabase();
    }
    //Return an instance of the singlton that is thread safe. If there is one already constructed, create a new one with proper context recursively.
    /*public static synchronized WGUAppDatabaseHelper getInstance(Context context){
        if (wguAppDatabaseHelper == null){
            singletonContext = context;
            wguAppDatabaseHelper = new WGUAppDatabaseHelper(context);
            db = wguAppDatabaseHelper.getWritableDatabase();
            return wguAppDatabaseHelper;
        }
        else{
            wguAppDatabaseHelper.close();
            wguAppDatabaseHelper = new WGUAppDatabaseHelper(context);
            db = wguAppDatabaseHelper.getWritableDatabase();
            return wguAppDatabaseHelper;

        }


    }
*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TERM_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ASSESMENT_TABLE_TITLE);
        sqLiteDatabase.execSQL(CREATE_TERM_TABLE);
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_NOTE_TABLE);
        sqLiteDatabase.execSQL(CREATE_ASSESMENT_TABLE);
        Log.e("DB CREATION", "Database was created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        /*if (wguAppDatabaseHelper == null){

            db = this.getWritableDatabase();
        }*/


        onCreate(sqLiteDatabase);
    }
    //Add term. Maybe I should return a boolean and test?


    //TERM RELATED METHODS
    public boolean addTerm(String termTitle, String termStart, String termEnd, String currentTerm) {

        ContentValues cv = new ContentValues();
        cv.put(TERM_TITLE, termTitle);
        cv.put(TERM_START_DATE, termStart);
        cv.put(TERM_END_DATE, termEnd);
        cv.put(TERM_CURRENT, currentTerm);
        return db.insert(TERM_TABLE_NAME, null, cv) >0;

        }


    public Cursor getTermAllCursor() {
        String[] columnsReturned = {
                TERM_TITLE,
                TERM_START_DATE,
                TERM_END_DATE,
                TERM_CURRENT
        };


        return db.query(TERM_TABLE_NAME, columnsReturned, null, null, null, null, null, null);

    }

    public boolean updateTerm (String termTitle, String termStart, String termEnd, String origianlTermName, String currentTerm) {

        ContentValues cv = new ContentValues();
        cv.put(TERM_TITLE, termTitle);
        cv.put(TERM_START_DATE, termStart);
        cv.put(TERM_END_DATE, termEnd);
        cv.put(TERM_CURRENT, currentTerm);

        return  db.update(TERM_TABLE_NAME, cv, TERM_TITLE + " = ?", new String[]{origianlTermName}) > 0;
    }

    //return a cursor of the courses associated with the term
    public Cursor getAssociatedCourses(String quary){
        Cursor cursor;
        String[] columnsReturned= {
                COURSE_NAME,
                COURSE_START_DATE,
                COURSE_END_DATE,
                COURSE_STATUS,
                COURSE_MENTOR_NAME ,
                COURSE_MENTOR_PHONE_ONE,
                COURSE_MENTOR_PHONE_TWO,
                COURSE_MENTOR_EMAIL_ONE,
                COURSE_MENTOR_EMAIL_TWO,
                COURSE_TERM_POSITION,
                COURSE_STATUS_POSITION,
                COURSE_TERM_ASSOCIATION,
                WGUAppDBContract.WGUAppCourseTableEntry._ID
        };
        cursor = db.query(COURSE_TABLE_NAME, columnsReturned, COURSE_TERM_ASSOCIATION + " = ?", new String[]{quary},null, null, null,null );

        return cursor;
    }

    //Return cursor for title of
    public Cursor getTermTitleCursor(){
        String[] columnsReturned = {
                TERM_TITLE,
                WGUAppDBContract.WGUAppTermTableEntry._ID
        };
        return db.query(TERM_TABLE_NAME, columnsReturned, null, null, null,null,null,null);
    }

    public Cursor getCurrentTerm(){
        String [] colomnsReturned = {
        TERM_TITLE,
                TERM_CURRENT
        };
        return db.query(TERM_TABLE_NAME, colomnsReturned, TERM_CURRENT +" = ?" , new String[]{"true"}, null, null, null, null );
    }


    public boolean deleteTerm(String string){
        Log.e("deletion", "In the delete method");
        return  db.delete(TERM_TABLE_NAME, TERM_TITLE + "= ?",new String[]{string}) > 0;
    }


    //NOTE RELATED METHODS

    public boolean addNote(String title, String content, String photoUri, String noteCourseAssociation){
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COURSE_ASSOCIATION, noteCourseAssociation);
        cv.put(NOTE_TITLE, title);
        cv.put(NOTE_CONTENT, content);
        cv.put(NOTE_PHOTO_FILE, photoUri);
        return db.insert(NOTE_TABLE_NAME, null, cv) > 0;

    }

    public Cursor getAllNotes(){
        String[] noteColomnsReturned = {
                NOTE_TITLE,
                NOTE_CONTENT,
                NOTE_PHOTO_FILE,
                NOTE_COURSE_ASSOCIATION,
                WGUAppDBContract.NoteTableEntry._ID
        };
        return db.query(NOTE_TABLE_NAME, noteColomnsReturned, null, null, null, null, null, null);

    }

    public boolean deleteNote(String noteTitle){
        return  db.delete(NOTE_TABLE_NAME, NOTE_TITLE + " = ?", new String[]{noteTitle})>0;
    }

    public boolean updateNote(String title, String content, String photoUri, String noteCourseAssociation, String originalNoteTitle){
        ContentValues cv = new ContentValues();
        cv.put(NOTE_COURSE_ASSOCIATION, noteCourseAssociation);
        cv.put(NOTE_TITLE, title);
        cv.put(NOTE_CONTENT, content);
        cv.put(NOTE_PHOTO_FILE, photoUri);
        return db.update(NOTE_TABLE_NAME, cv, NOTE_TITLE + " = ?", new String[]{originalNoteTitle}) >0;
    }
    public Cursor getNotesAssociatedWithCourse(String courseTitle){
        String[] noteColomnsReturned = {
                NOTE_TITLE,
                NOTE_COURSE_ASSOCIATION,

                WGUAppDBContract.NoteTableEntry._ID
        };
        return db.query(NOTE_TABLE_NAME, noteColomnsReturned, NOTE_COURSE_ASSOCIATION + " = ?", new String[]{courseTitle}, null, null, null, null);
    }


    //I may want to return a something signifying it's success?
    //addCourse will be called to add a course. Both assessments must have a string and not a null
    public boolean addCourse(String courseName, String courseStart, String courseEnd, String courseStatus, String courseMentorName,
                          String courseMenPhone1, String courseMenPhone2, String courseMenEmail1, String courseMenEmail2, String termAssociation,
                             String termPos, String statPos) {

        //Place all the course info into a content value object and send it as a parm to insert
        ContentValues cv = new ContentValues();

        cv.put(COURSE_NAME, courseName);
        cv.put(COURSE_START_DATE, courseStart);
        cv.put(COURSE_END_DATE, courseEnd);
        cv.put(COURSE_STATUS, courseStatus);
        cv.put(COURSE_MENTOR_NAME, courseMentorName);
        cv.put(COURSE_MENTOR_PHONE_ONE, courseMenPhone1);
        cv.put(COURSE_MENTOR_PHONE_TWO, courseMenPhone2);
        cv.put(COURSE_MENTOR_EMAIL_ONE, courseMenEmail1);
        cv.put(COURSE_MENTOR_EMAIL_TWO, courseMenEmail2);
        cv.put(COURSE_TERM_ASSOCIATION, termAssociation);
        cv.put(COURSE_TERM_POSITION,termPos);
        cv.put(COURSE_STATUS_POSITION, statPos);
        return db.insert(COURSE_TABLE_NAME, null, cv) > 0;





    }

    public Cursor getCourseAllEntry(){
        String[] courseColomnsReturned = {COURSE_NAME,

        COURSE_START_DATE,
        COURSE_END_DATE,
        COURSE_STATUS,
        COURSE_MENTOR_NAME,
        COURSE_MENTOR_PHONE_ONE,
        COURSE_MENTOR_PHONE_TWO,
        COURSE_MENTOR_EMAIL_ONE,
        COURSE_MENTOR_EMAIL_TWO,
        COURSE_TERM_ASSOCIATION,
        COURSE_TERM_POSITION,
                COURSE_STATUS_POSITION,
                WGUAppDBContract.WGUAppCourseTableEntry._ID
        };
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(COURSE_TABLE_NAME, courseColomnsReturned, null, null, null, null, null, null);

    }
    public Cursor getCourseTitle(){
        String[] courseColomnsReturned = {COURSE_NAME,
                WGUAppDBContract.WGUAppCourseTableEntry._ID};
        db.getPath();
        return  db.query(COURSE_TABLE_NAME, courseColomnsReturned,  null, null, null, null, null, null);
    }

    public boolean deleteCourse(String title){

        return db.delete(COURSE_TABLE_NAME, COURSE_NAME + " = ?", new String[]{title}) > 0;
    }

    public boolean updateCourse (String courseName, String courseStart, String courseEnd, String courseStatus, String courseMentorName,
                                 String courseMenPhone1, String courseMenPhone2, String courseMenEmail1, String courseMenEmail2, String courseTermPos, String courseStatPos,
                                 String termAssociation, String titleToUpdateFrom) {

        //Place all the course info into a content value object and send it as a parm to insert
        ContentValues cv = new ContentValues();

        cv.put(COURSE_NAME, courseName);
        cv.put(COURSE_START_DATE, courseStart);
        cv.put(COURSE_END_DATE, courseEnd);
        cv.put(COURSE_STATUS, courseStatus);
        cv.put(COURSE_MENTOR_NAME, courseMentorName);
        cv.put(COURSE_MENTOR_PHONE_ONE, courseMenPhone1);
        cv.put(COURSE_MENTOR_PHONE_TWO, courseMenPhone2);
        cv.put(COURSE_MENTOR_EMAIL_ONE, courseMenEmail1);
        cv.put(COURSE_MENTOR_EMAIL_TWO, courseMenEmail2);
        cv.put(COURSE_TERM_POSITION, courseTermPos);
        cv.put(COURSE_STATUS_POSITION, courseStatPos);
        cv.put(COURSE_TERM_ASSOCIATION, termAssociation);

        return db.update(COURSE_TABLE_NAME, cv, COURSE_NAME + " = ?", new String[]{titleToUpdateFrom}) >0;

    }


    // ASSESMENT RELATED METHODS
    public Cursor getAllAssessmentInformation(String assessmentTitle){
        String [] assesmentColomnsReturned = {
                ASSESMENT_COURSE_ASSOCIATION,
                ASSESMENT_TYPE,
                ASSESMENT_DATE,
                ASSESMENT_TITLE,
                WGUAppDBContract.WGUAssesmentEntry._ID
        };
        return db.query(ASSESMENT_TABLE_TITLE, assesmentColomnsReturned, ASSESMENT_TITLE +" = ?" , new String[]{assessmentTitle}, null, null, null, null );
    }

    public Cursor loadAssessmentsAssociatedWithCourse(String courseTitle){
        String [] assesmentColomnsReturned = {
                ASSESMENT_TITLE,
                ASSESMENT_TYPE,
                ASSESMENT_DATE,
                WGUAppDBContract.WGUAssesmentEntry._ID
        };
        return db.query(ASSESMENT_TABLE_TITLE, assesmentColomnsReturned, ASSESMENT_COURSE_ASSOCIATION +" = ?" , new String[]{courseTitle}, null, null, null, null );
    }


    public boolean deleteAssessment(String assessmentTitle){
        return  db.delete(ASSESMENT_TABLE_TITLE, ASSESMENT_TITLE +" = ?" , new String[]{assessmentTitle}) > 0;
    }

    public boolean submitAssesment(String title, String date, String type, String associatedCourse){
        ContentValues cv = new ContentValues();
        cv.put(ASSESMENT_TITLE, title);
        cv.put(ASSESMENT_DATE, date);
        cv.put(ASSESMENT_TYPE, type);
        cv.put(ASSESMENT_COURSE_ASSOCIATION, associatedCourse);

        return db.insert(ASSESMENT_TABLE_TITLE, null, cv) > 0;
    }

    public  boolean updateAssesment(String title, String date, String type, String associatedCourse, String originalTitle){
        ContentValues cv = new ContentValues();
        cv.put(ASSESMENT_TITLE, title);
        cv.put(ASSESMENT_DATE, date);
        cv.put(ASSESMENT_TYPE, type);
        cv.put(ASSESMENT_COURSE_ASSOCIATION, associatedCourse);

        return db.update(ASSESMENT_TABLE_TITLE, cv, ASSESMENT_TITLE + " = ? ", new String[]{ originalTitle}) > 0;
    }
    public Cursor loadAllAssessmentTitles(){
        String[] colomns = {
                ASSESMENT_TITLE,
                WGUAppDBContract.WGUAssesmentEntry._ID
        };
        return db.query(ASSESMENT_TABLE_TITLE, colomns, null, null, null , null, null);
    }

    @Override
    public void finalize(){
        db.close();
        this.close();

        //wguAppDatabaseHelper = null;
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
