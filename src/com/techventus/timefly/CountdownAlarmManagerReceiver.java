package com.techventus.timefly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.techventus.timefly.model.ApplicationState;
import com.techventus.timefly.model.STATE;

import java.sql.Time;

/**
 * Created by Joseph on 08.04.15.
 */
public class CountdownAlarmManagerReceiver extends BroadcastReceiver
{
	private long mTimeStarted;
	private String mGoalName;
	private int mGoalId;
	private long mPractiseFutureTime;

	private void deconstructAlarmFinishedIntent(Intent intent)
	{
		mTimeStarted = intent.getLongExtra(TimerService.BundleKey.TIME_STARTED,-1);
		mGoalName = intent.getStringExtra(TimerService.BundleKey.GOAL_NAME);
		mGoalId = intent.getIntExtra(TimerService.BundleKey.GOAL_ID, -1);
		mPractiseFutureTime = intent.getLongExtra(TimerService.BundleKey.TIME_SPENT, -1);
//		intent.putExtra(TimerService.BundleKey.GOAL_TIME,timeInFuture);
//		intent.putExtra(TimerService.BundleKey.GOAL_NAME, mGoal_name);
//		intent.putExtra(TimerService.BundleKey.GOAL_ID, mGoal_id);
	}

	@Override
	public void onReceive(final Context context, Intent intent)
	{

		Log.v("CountdownAlarmManagerReceiver", "Countdown Alarm Received "+intent.getLongExtra("GOAL_TIME",0L));//+intent.toString());
		deconstructAlarmFinishedIntent(intent);
		startAlarmBroadcast(context);

		Intent serviceIntent = new Intent(context, TimerService.class);
		context.startService(new Intent(context, TimerService.class));
		Log.v("CountdownAlarmManagerReceiver", "Service started again");

		new Handler() .postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				startAlarmBroadcast(context);
			}
		},3000);


	}


	private void startAlarmBroadcast(Context context)
	{
		ApplicationStatePersistenceManager aspm = new ApplicationStatePersistenceManager(context);
		ApplicationState applicationState = aspm.getSavedState();
		if(applicationState.getApplicationState()== STATE.STARTED)
		{
			Intent broadcastIntent = new Intent();

			broadcastIntent.setAction(TimerService.BundleKey.UPDATE_TIMER_SETTINGS);
			broadcastIntent.putExtra(TimerService.BundleKey.GOAL_NAME, mGoalName);
			broadcastIntent.putExtra(TimerService.BundleKey.GOAL_ID,mGoalId);
			broadcastIntent.putExtra(TimerService.BundleKey.ALARM, true);
			Log.v("CountdownAlarmManagerReceiver","RESULTS "+mTimeStarted+" id: "+mGoalId+"; name: "+mGoalName+"; "+mPractiseFutureTime);
			context.sendBroadcast(broadcastIntent);
		}
	}
}