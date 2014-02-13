package com.techventus.timefly;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DatabaseHelper.class.getSimpleName();

	private static final String DATABASE_NAME = "habits";
	private static final int DATABASE_VERSION = 1;
    protected static final String GOALS_TABLE_NAME = "goals";
    protected static final String PRACTICE_TABLE_NAME = "practice";
    protected static final String field_goals_goals_text = "goal";
    protected static final String field_goals_id_integer = "_id";
    protected static final String field_practice_id_integer = "_id";
    protected static final String field_practice_goals_id_integer = "goal_id";
    protected static final String field_practice_goals_name_text = "goal_name";
    protected static final String field_practice_seconds_integer = "seconds";
    protected static final String field_practice_date_long = "date";
    protected static final String field_practice_notes_text = "notes";
    private static final String TABLE_GOALS_CREATE =
                "CREATE TABLE " + GOALS_TABLE_NAME + " ("+field_goals_id_integer+" integer primary key autoincrement, "+field_goals_goals_text+" TEXT, UNIQUE("+field_goals_goals_text+"));";
        
    private static final String TABLE_PRACTICE_LOG =
    		"CREATE TABLE "+PRACTICE_TABLE_NAME +" ("+field_practice_id_integer+" integer primary key autoincrement, "+field_practice_goals_id_integer+" INTEGER NOT NULL, "+field_practice_goals_name_text+" TEXT NOT NULL, "+field_practice_seconds_integer+" INTEGER NOT NULL DEFAULT '0' , "+field_practice_notes_text+" TEXT , "+field_practice_date_long+" LONG ,  FOREIGN KEY ("+field_practice_goals_id_integer+") REFERENCES "+GOALS_TABLE_NAME+" ("+field_goals_id_integer+"));";
    
    DatabaseHelper(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL(TABLE_GOALS_CREATE);
        db.execSQL(TABLE_PRACTICE_LOG);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
	}
	
	
	
//	public List<Practice>  getPracticeList(String selectionCondition, String[] args)
//	{
//		getReadableDatabase().query(d, columns, selection, args, null, null, null);
//		Cursor c = 
//	}
	
	
	public List<Practice> getPracticeList(String selectionCondition, String[] selectionArgs)
	{
		List<Practice> ret = new ArrayList<Practice>();
		
        String[] fields =   {DatabaseHelper.field_practice_notes_text, DatabaseHelper.field_practice_id_integer, DatabaseHelper.field_practice_goals_id_integer,DatabaseHelper.field_practice_goals_name_text, DatabaseHelper.field_practice_date_long, DatabaseHelper.field_practice_seconds_integer};
        
        String orderby = " "+DatabaseHelper.field_practice_date_long+" DESC";
//        Log.v(TAG, "GOAL ID "+goal_id);
        Cursor c  =getReadableDatabase().query(PRACTICE_TABLE_NAME,fields, selectionCondition, selectionArgs, null, null, orderby);
        
        while(c.moveToNext())
        {
        	String note = c.getString(c.getColumnIndex(field_practice_notes_text));
        	
        	long date = c.getLong(c.getColumnIndex(field_practice_date_long));
        	
        	int secs = c.getInt(c.getColumnIndex(field_practice_seconds_integer));
        	
        	
        	int practice_id = c.getInt(c.getColumnIndex(field_practice_id_integer));
        	
        	int goal_id = c.getInt(c.getColumnIndex(field_practice_goals_id_integer));
        	
//        	int colint = c.getColumnIndex(field_practice_goals_name_text);
        	
        	try{
        		int colex = c.getColumnIndexOrThrow(field_practice_goals_name_text);
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        	String [] cols = c.getColumnNames();
        	for(String col:cols)
        	{
        		Log.v(TAG, "Col Name: "+col);
        	}
        	
        	String goal_name = c.getString(c.getColumnIndex(field_practice_goals_name_text));
        	
        	Practice p = new Practice(note, date, secs, practice_id, goal_id, goal_name);
        	
        	ret.add(p);
        }
        c.close();
        
        return ret;
        
        
       
	}
	
	
	
	
	
	
}