package com.techventus.timefly;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.techventus.timefly.R;

public class PerformingHabbit extends SherlockFragmentActivity implements TimerFragment2.TimerListener, CreateNoteFragment.NoteCreateListener
{

	public static final String TAG = PerformingHabbit.class.getSimpleName();
	RelativeLayout mRoot;
	TimerFragment2 timerFragment;

	boolean started;

	private long intendedTime;
	private long timeSpent;
	private long startTime;

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
			if(extras.containsKey(TimerService.BundleKey.SETUP))
			{
				Toast.makeText(PerformingHabbit.this,"Performing Habbit Acknowledges Setup Complete",Toast.LENGTH_SHORT).show();
				timerFragment.setupComplete();
			}
			else if(extras.containsKey(TimerService.BundleKey.START))
			{
				timerFragment.timerStarted();
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
				timerFragment.ended(extras.getLong(TimerService.BundleKey.TIME_SPENT));
			}
			else if(extras.containsKey(TimerService.BundleKey.SNOOZE))
			{
				timerFragment.snooze();
			}
			else if(extras.containsKey(TimerService.BundleKey.FINISH))
			{
				timerFragment.ended(extras.getLong(TimerService.BundleKey.TIME_SPENT));
				timeSpent =  extras.getLong(TimerService.BundleKey.TIME_SPENT);
				startTime = System.currentTimeMillis() - timeSpent;
				timerFragmentSwap();
			}
//			else if(extras.containsKey(TimerService.BundleKey.SNOOZE))
//			{
//				timerFragment.sn
//			}

				//			else if(extras.containsKey(BundleKey.START))
				//			{
				//				start();
				//			}
				//			else if(extras.containsKey(BundleKey.STOP))
				//			{
				//				stop();
				//			}
				//			else if(extras.containsKey(BundleKey.SNOOZE))
				//			{
				//
				//				 snooze(extras.getLong(BundleKey.SNOOZE));
				//			}


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
		filter.addAction("com.techventus.timefly.updatetimervisuals");
		registerReceiver(thisReceiver,filter);

		startService(new Intent(this, TimerService.class));



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
		getSupportMenuInflater().inflate(R.menu.main, menu);
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


//	@Override
//	public void onTimerFinished(long intendedTime, long timeSpent)
//	{
//
//		long currentTime = System.currentTimeMillis();
//		startTime = currentTime - timeSpent;
//
//		this.timeSpent = timeSpent;
//		this.intendedTime = intendedTime;
//
//		try
//		{
//
//			if (timerFragment != null)
//			{
//				Log.v(TAG, "timeFragment not null");
//			}
//
//			Fragment f = getSupportFragmentManager().findFragmentByTag("timer");
//			if (f != null)
//			{
//				getSupportFragmentManager().beginTransaction().remove(f).commit();
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		try
//		{
//
//			CreateNoteFragment noteFragment = new CreateNoteFragment();
//			getSupportFragmentManager().beginTransaction().add(R.id.root, noteFragment, "note").commitAllowingStateLoss();
//
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}


	@Override
	public void onNoteFinished(String note)
	{
		Toast.makeText(this, "Note Recorded: "+note, Toast.LENGTH_LONG).show();
		addNote(note);
		timerFragment = null;
		stopService(new Intent(PerformingHabbit.this,TimerService.class));
//		getSupportFragmentManager()..findFragmentByTag("timer");
		finish();
	}

	void addNote(String note)
	{

		if (goal_id != -1 && goal_name != null && startTime > 0)
		{
			DatabaseHelper db = new DatabaseHelper(this);

			//			String insertStatement;
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
			Toast.makeText(this, "ERROR: GOAL ID IS -1 - Contact the developer.", Toast.LENGTH_LONG).show();
		}

	}


	@Override
	public void onTimerStarted()
	{
		started = true;
	}

}
