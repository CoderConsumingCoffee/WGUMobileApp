package com.aphart.wgumobileapp;

import android.provider.BaseColumns;


import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppCourseTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAppTermTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.NoteTableEntry.*;
import static com.aphart.wgumobileapp.WGUAppDBContract.WGUAssesmentEntry.*;


public final class WGUAppDBContract {
    public WGUAppDBContract(){}

    public static abstract class WGUAppTermTableEntry implements BaseColumns{
        //Names for the term table
        public static final String TERM_TABLE_NAME = "term";
        public static final String TERM_TITLE = "termtitle";
        public static final String TERM_START_DATE = "termstartdate";
        public static final String TERM_END_DATE = "termenddate";
        public static final String TERM_CURRENT = "termcurrentdate";

    }
    public static abstract class WGUAppCourseTableEntry implements BaseColumns{
        //Names for the course tab
        public static final String COURSE_TABLE_NAME = "course";

        public static final String COURSE_NAME = "coursename";
        public static final String COURSE_START_DATE = "cstartdate";
        public static final String COURSE_END_DATE = "cenddate";
        public static final String COURSE_STATUS = "cstatus";
        public static final String COURSE_MENTOR_NAME = "cmentor";
        public static final String COURSE_MENTOR_PHONE_ONE = "cmenphoneone";
        public static final String COURSE_MENTOR_PHONE_TWO = "cmenphonetwo";
        public static final String COURSE_MENTOR_EMAIL_ONE = "cmenemailone";
        public static final String COURSE_MENTOR_EMAIL_TWO = "cmenemailtwo";
        public static final String COURSE_TERM_POSITION = "ctermpos";
        public static final String COURSE_STATUS_POSITION = "cstatpos";
        public static final String COURSE_TERM_ASSOCIATION = "ctassosiation";
    }
    public static abstract class WGUAssesmentEntry implements BaseColumns{
        public static final String ASSESMENT_TABLE_TITLE = "assessmenttable";
        public static final String ASSESMENT_TITLE =  "assesmenttitle";
        public static final String ASSESMENT_DATE = "assesmentdate";
        public static final String ASSESMENT_TYPE = "assesmenttype";
        public static final String ASSESMENT_COURSE_ASSOCIATION = "assesmentcourseassociation";
    }
    public static abstract class NoteTableEntry implements BaseColumns{
        public static final String NOTE_TABLE_NAME = "notetable";
        public static final String NOTE_TITLE = "notetitle";
        public static final String NOTE_CONTENT = "notecontent";
        public static final String NOTE_PHOTO_FILE = "notephotofile";
        public static final String NOTE_COURSE_ASSOCIATION = "notecourseassociation";

    }

    public static abstract class WGUAppTableConstructors{

        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = " , ";
        public static final String CREATE_TERM_TABLE = "CREATE TABLE " + TERM_TABLE_NAME + " ("
                + WGUAppDBContract.WGUAppTermTableEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP + TERM_TITLE + TEXT_TYPE + COMMA_SEP + TERM_START_DATE + TEXT_TYPE + COMMA_SEP
                + TERM_END_DATE + TEXT_TYPE + COMMA_SEP +
                TERM_CURRENT + TEXT_TYPE +")";
        public static final String CREATE_COURSE_TABLE = "CREATE TABLE " + COURSE_TABLE_NAME + " (" + WGUAppDBContract.WGUAppCourseTableEntry._ID + " INTEGER PRIMARY KEY " + COMMA_SEP
                + COURSE_NAME + TEXT_TYPE + COMMA_SEP
                + COURSE_START_DATE + TEXT_TYPE + COMMA_SEP
                + COURSE_END_DATE + TEXT_TYPE + COMMA_SEP
                + COURSE_STATUS + TEXT_TYPE + COMMA_SEP
                + COURSE_MENTOR_NAME + TEXT_TYPE + COMMA_SEP
                + COURSE_MENTOR_PHONE_ONE + TEXT_TYPE + COMMA_SEP
                + COURSE_MENTOR_PHONE_TWO + TEXT_TYPE + COMMA_SEP
                + COURSE_MENTOR_EMAIL_ONE + TEXT_TYPE + COMMA_SEP
                + COURSE_MENTOR_EMAIL_TWO + TEXT_TYPE + COMMA_SEP
                + COURSE_TERM_POSITION + TEXT_TYPE + COMMA_SEP
                + COURSE_STATUS_POSITION + TEXT_TYPE + COMMA_SEP
                + COURSE_TERM_ASSOCIATION + TEXT_TYPE + ")";
        public static final String CREATE_NOTE_TABLE = "CREATE TABLE " +  NOTE_TABLE_NAME +" (" + NoteTableEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
                + NOTE_TITLE + TEXT_TYPE + COMMA_SEP
                + NOTE_CONTENT + TEXT_TYPE + COMMA_SEP
                + NOTE_COURSE_ASSOCIATION + TEXT_TYPE + COMMA_SEP
                + NOTE_PHOTO_FILE + TEXT_TYPE  + ")";
        public static final String CREATE_ASSESMENT_TABLE = "CREATE TABLE " + ASSESMENT_TABLE_TITLE + " (" + WGUAssesmentEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP
                + ASSESMENT_TITLE + TEXT_TYPE + COMMA_SEP
                + ASSESMENT_DATE + TEXT_TYPE + COMMA_SEP
                + ASSESMENT_TYPE + TEXT_TYPE + COMMA_SEP
                + ASSESMENT_COURSE_ASSOCIATION + TEXT_TYPE  + ")";

    }
}
