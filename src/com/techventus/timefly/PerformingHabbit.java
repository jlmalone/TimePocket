package com.techventus.timefly;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.techventus.timefly.model.ApplicationState;

public class PerformingHabbit extends SherlockFragmentActivity implements TimerFragment2.TimerListener, CreateNoteFragment.NoteCreateListener
{

	public static final String TAG = PerformingHabbit.class.getSimpleName();
	private TimerFragment2 timerFragment;

	boolean started;
	private long timeSpent;
	private long startTime;

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//---save whatever you need to persist—
		outState.putBoolean("STARTED", started);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		//---retrieve the information persisted earlier---
		started = savedInstanceState.getBoolean("STARTED");
	}

	class BundleKey
	{
		public static final String EXTRA_GOAL_ID = "EXTRA_GOAL_ID";
		public static final String EXTRA_GOAL_NAME = "EXTRA_GOAL_NAME";
		public static final String EXTRA_DATES = "EXTRA_DATES";
	}

	int goal_id;
	String goal_name;

	@Override
	public void onBackPressed()
	{
		if (started)
		{
			Toast.makeText(this, getResources().getString(R.string.back_button_disabled), Toast.LENGTH_SHORT).show();
		}
		else
		{

			super.onBackPressed();
		}
	}


	private BroadcastReceiver thisReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{

			Bundle extras = intent.getExtras() ;
			Log.v(TAG, "Performing Habbit thisReceiver hit "+(intent!=null?intent.toString():"null"));

			String gname = extras.getString(TimerService.BundleKey.GOAL_NAME);
			int gid = extras.getInt(TimerService.BundleKey.GOAL_ID,-1);
			if(gname!=null && !gname.isEmpty())
			{
				 goal_name = gname;
			}

			if(goal_id == -1 && gid != -1)
			{
				 goal_id = gid;
			}

			if(extras.containsKey(TimerService.BundleKey.SETUP))
			{
				Log.v(TAG, "Performing Habbit thisReceiver SETUP HIT");

				//				Toast.makeText(PerformingHabbit.this,"Performing Habbit Acknowledges Setup Complete",Toast.LENGTH_SHORT).show();
				timerFragment.setupComplete();
			}
			else if(extras.containsKey(TimerService.BundleKey.START))
			{
				timerFragment.timerStarted();
				started = true;
			}
			else if(extras.containsKey(TimerService.BundleKey.UPDATE_TIME))
			{
				timerFragment.updateTime(extras.getLong(TimerService.BundleKey.UPDATE_TIME));
			}
			else if(extras.containsKey(TimerService.BundleKey.ALARM))
			{
				timerFragment.alarmActive();
			}
			else if (extras.containsKey(TimerService.BundleKey.STOP))
			{
				//TODO method was not finished.
//				timerFragment.ended(extras.getLong(TimerService.BundleKey.TIME_SPENT));
			}
			else if(extras.containsKey(TimerService.BundleKey.SNOOZE))
			{
				timerFragment.snooze();
			}
			else if(extras.containsKey(TimerService.BundleKey.FINISH))
			{
				//TODO method was not finished
//				timerFragment.ended(extras.getLong(TimerService.BundleKey.TIME_SPENT));
				timeSpent =  extras.getLong(TimerService.BundleKey.TIME_SPENT);
				startTime = System.currentTimeMillis() - timeSpent;
				timerFragmentSwap();
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		goal_id = intent.getIntExtra(BundleKey.EXTRA_GOAL_ID, -1);
		goal_name = intent.getStringExtra(BundleKey.EXTRA_GOAL_NAME);

		IntentFilter filter = new IntentFilter();
		filter.addAction(TimerService.BundleKey.TIMER_VISUAL_ADDRESS);
		registerReceiver(thisReceiver,filter);

		Intent serviceIntent = new Intent(this, TimerService.class);
		serviceIntent.putExtra(TimerService.BundleKey.GOAL_NAME,goal_name);
		serviceIntent.putExtra(TimerService.BundleKey.GOAL_ID,goal_id);

		startService(serviceIntent);

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.root) != null)
		{
			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null)
			{
				timerFragment = (TimerFragment2)getSupportFragmentManager().findFragmentByTag("timer");
				return;
			}

			Log.v(TAG,"add the fragment") ;

			Fragment fragment = getSupportFragmentManager().findFragmentByTag("timer");

			if(fragment==null)
			{
				Log.v(TAG,"add the fragment. it is null") ;
			}

			timerFragment = new TimerFragment2();

			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			timerFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout

			getSupportFragmentManager().beginTransaction().add(R.id.root, timerFragment, "timer").commitAllowingStateLoss();
		}
	}


	@Override
	protected void onResume()
	{
		super.onResume();
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(thisReceiver);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
//		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	void timerFragmentSwap()
	{
		try
		{

			if (timerFragment != null)
			{
				Log.v(TAG, "timeFragment not null");
			}

			Fragment f = getSupportFragmentManager().findFragmentByTag("timer");
			if (f != null)
			{
				getSupportFragmentManager().beginTransaction().remove(f).commit();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			CreateNoteFragment noteFragment = new CreateNoteFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.root, noteFragment, "note").commitAllowingStateLoss();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}




	@Override
	public void onNoteFinished(String note)
	{
		Toast.makeText(this, "Note Recorded: "+note, Toast.LENGTH_LONG).show();
		addNote(this, note, goal_name, goal_id, timeSpent, startTime);
		timerFragment = null;
		stopService(new Intent(PerformingHabbit.this,TimerService.class));
		finish();
	}

	public static void addNote(Context context, String note, String goal_name, int goal_id, long timeSpent,  long startTime)
	{
		if(!(goal_id != -1 && goal_name != null && startTime > 0))
		{
			ApplicationStatePersistenceManager applicationStatePersistenceManager = new ApplicationStatePersistenceManager(context);
			ApplicationState applicationState = applicationStatePersistenceManager.getSavedState();
			goal_id = applicationState.getGoalId() ;
			startTime = applicationState.getInitialStartingTime();
			timeSpent = applicationState.getPractiseTime();
			goal_name = applicationState.getGoalName();

		}


		if (goal_id != -1 && goal_name != null && startTime > 0)
		{
			DatabaseHelper db = new DatabaseHelper(context);
			ContentValues content = new ContentValues();
			content.put(DatabaseHelper.field_practice_goals_id_integer, goal_id);
			content.put(DatabaseHelper.field_practice_goals_name_text, goal_name);
			content.put(DatabaseHelper.field_practice_date_long, startTime);
			content.put(DatabaseHelper.field_practice_notes_text, note);
			content.put(DatabaseHelper.field_practice_seconds_integer, (int) (timeSpent / 1000));
			db.getWritableDatabase().insert(DatabaseHelper.PRACTICE_TABLE_NAME, null, content);

			db.close();
		}
		else
		{


			Toast.makeText(context, "ERROR: GOAL ID IS -1 - Contact the developer. timeSpent:"+timeSpent+" startTime:"+startTime+" note:"+note+" "+goal_name,
					Toast
					.LENGTH_LONG).show();
		}

	}

	@Override
	public void onTimerStarted()
	{
		started = true;
	}

}
