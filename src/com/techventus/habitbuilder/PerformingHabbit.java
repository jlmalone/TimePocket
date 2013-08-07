package com.techventus.habitbuilder;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class PerformingHabbit extends SherlockFragmentActivity implements TimerFragment.TimerListener, CreateNoteFragment.NoteCreateListener{
	
	public static final String TAG = PerformingHabbit.class.getSimpleName();
	RelativeLayout mRoot ;
	TimerFragment timerFragment;
	
	   
    private long intendedTime;
    private long timeSpent;
    private long startTime ;
	
	class BundleKey
	{
		public static final String EXTRA_GOAL_ID = "EXTRA_GOAL_ID";
		public static final String EXTRA_GOAL_NAME = "EXTRA_GOAL_NAME";
	}

	int goal_id;
	String goal_name;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = getIntent();
        goal_id=intent.getIntExtra(BundleKey.EXTRA_GOAL_ID,-1);
        goal_name=intent.getStringExtra(BundleKey.EXTRA_GOAL_NAME);
        
        
//        mRoot = (RelativeLayout)findViewById(R.id.root);

        Log.v(TAG, "CALL ONCREATE");

//        

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.root) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }


            timerFragment = new TimerFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            timerFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root, timerFragment,"timer").commitAllowingStateLoss();
        }


      
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


 
    
	@Override
	public void onTimerFinished(long intendedTime, long timeSpent) {
		// TODO Auto-generated method stub
//        getSupportFragmentManager().beginTransaction()
//        .add(R.id.root, timerFragment).commit();
		Log.v(TAG,"CALL ONTIMERFINISHED");
		
		long currentTime = System.currentTimeMillis();
		startTime = currentTime-timeSpent;
		
		this.timeSpent = timeSpent;
		this.intendedTime = intendedTime;
		
		try{
			
			if(timerFragment!=null)
			{
				Log.v(TAG, "timeFragment not null");
			}
			
			Fragment f = getSupportFragmentManager().findFragmentByTag("timer");
			if(f!=null)
				getSupportFragmentManager().beginTransaction()
		        .remove(f).commit();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try{

			CreateNoteFragment noteFragment = new CreateNoteFragment();
			getSupportFragmentManager().beginTransaction()
			.add(R.id.root, noteFragment,"note").commitAllowingStateLoss();
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onNoteFinished(String note) {
		Toast.makeText(this, note, Toast.LENGTH_LONG).show();
		addNote(note);
		finish();
	}

	void addNote(String note)
	{
		
		if(goal_id!=-1 && goal_name!=null && startTime>0)
		{
			DatabaseHelper db = new DatabaseHelper(this);
			
//			String insertStatement;
			ContentValues content = new ContentValues();
			content.put(DatabaseHelper.field_practice_goals_id_integer, goal_id);
			content.put(DatabaseHelper.field_practice_goals_name_text, goal_name);
			content.put(DatabaseHelper.field_practice_date_long, startTime);
			content.put(DatabaseHelper.field_practice_notes_text, note);
			content.put(DatabaseHelper.field_practice_seconds_integer, (int)(timeSpent/1000));
			db.getWritableDatabase().insert(DatabaseHelper.PRACTICE_TABLE_NAME, null, content);
			
//			db.getWritableDatabase().execSQL("INSERT INTO "+DatabaseHelper.PRACTICE_TABLE_NAME+" ("+DatabaseHelper.field_practice_goals_id_integer+","+DatabaseHelper.field_practice_goals_name_text+","+DatabaseHelper.field_practice_seconds_integer+", "+DatabaseHelper.field_practice_date_long+" ,"+DatabaseHelper.field_practice_notes_text+") VALUES ('"+goal_id+"','"+goal_name+"','"+(int)(timeSpent/1000)+"','"+startTime+"','"+note+"');");
			db.close();
		}else{
			Toast.makeText(this, "ERROR GOAL ID IS -1", Toast.LENGTH_LONG);
		}

	}
	

}
