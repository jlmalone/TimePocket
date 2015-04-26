package com.techventus.timefly;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import com.techventus.timefly.model.ApplicationState;
import com.techventus.timefly.model.STATE;

/**
 * Created by Joseph on 16.02.14.
 *
 * TODO MAKE NOTIFICATION MANAGER
 *
 */
public class TimerService   extends Service
{

	public class BundleKey
	{

		public static final String TIMER_VISUAL_ADDRESS = "com.techventus.timefly.updatetimervisual";
		public static final String SETUP = "SETUP";
		public static final String START = "START";
		public static final String ERROR_ALREADY_STARTED = "ERROR_ALREADY_STARTED";
		public static final String STOP = "STOP";
		public static final String SNOOZE = "SNOOZE";
	   	public static final String FINISH = "FINISH";
		public static final String ALARM = "ALARM";
		public static final String TIME_STARTED = "TIME_STARTED";
		public static final String TIME_SPENT = "TIME_SPENT";
		public static final String NEW_TIME = "NEW_TIME";
		public static final String TIME_NOT_SET = "TIME_NOT_SET";
		public static final String UPDATE_TIME = "UPDATE_TIME";
		public static final String GOAL_NAME = "GOAL_NAME";
		public static final String GOAL_ID = "GOAL_ID";
		public static final String GOAL_TIME = "GOAL_TIME";

		public static final String UPDATE_TIMER_SETTINGS =   "com.techventus.timefly.updatetimersettings";
	}

	private static final String NOTIFICATION_TITLE_TIMEFLY = "TimeFly";

	//ID OF GOAL BEING PRACTICED
	private int mGoal_id = -1;
	//NAME OF GOAL BEING PRACTICED
	private String mGoal_name = "";

	//MEDIA PLAYER FOR ALARM
	private MediaPlayer mp;

	private long startingCountdownTime = 0;

	private STATE mState = STATE.NOT_SET;

	private long mInitialStartingTime;

	private static final String TAG = TimerService.class.getSimpleName();

	private DetailedCountDown mCountDown;
	private boolean mStarted = false;

	private Notification mNotification;
	private NotificationManager mNotificationManager    ;
	private AlarmManager mAlarmManager;

	private ApplicationState mApplicationState;
	private ApplicationStatePersistenceManager mApplicationStatePersistenceManager;


	private void updateApplicationState()
	{
		Log.v("TIMER SERVICE","UPDATE APPLICATION STATE");
		mApplicationState.setGoalId(mGoal_id);
		mApplicationState.setGoalName(mGoal_name);
		mApplicationState.setApplicationState(mState);
		mApplicationState.setPractiseTime(startingCountdownTime);
		mApplicationState.setInitialStartingTime(mInitialStartingTime);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle extras = intent.getExtras() ;

			int temp_goal_id =  extras.getInt(BundleKey.GOAL_ID,-1);
			String temp_goal_name = extras.getString(BundleKey.GOAL_NAME);//.getString(BundleKey.GOAL_NAME, "");

			if(temp_goal_id != -1)
			{
				mGoal_id = temp_goal_id;

			}
			if(temp_goal_name !=null )
			{
				mGoal_name = temp_goal_name ;
			}

			 if (extras.containsKey(BundleKey.ALARM))
			{
				Log.v(TAG, "Service intent contains key Alarm");
				finishCountdown();
			}

			else if(extras.containsKey(BundleKey.NEW_TIME))
			{
				mState = STATE.SETUP;
//				STATE = BundleKey.SETUP;
				startingCountdownTime = extras.getLong(BundleKey.NEW_TIME);
				Intent i = new Intent();
				i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
				i.putExtra(BundleKey.SETUP, true);
				Log.v(TAG, "New time set on TimerService " + startingCountdownTime);
				sendBroadcast(i);

			}
			else if(extras.containsKey(BundleKey.START))
			{
			   mState = STATE.STARTED;
				mInitialStartingTime = System.currentTimeMillis();
				start();

				//TODO REFACTOR save initial starting time to SharedPreferences.

			}
			else if(extras.containsKey(BundleKey.STOP))
			{
				mState = STATE.STOP;
				Intent i = new Intent();
				i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
				i.putExtra(BundleKey.FINISH, true);
				i.putExtra(BundleKey.TIME_SPENT,(System.currentTimeMillis()- mInitialStartingTime));
				i.putExtra(BundleKey.GOAL_NAME,mGoal_name) ;
				i.putExtra(BundleKey.GOAL_ID,mGoal_id) ;
				sendBroadcast(i);
				stopAlarm();
				if(mCountDown!=null)
				{
					mCountDown.cancel();
				}
				mNotificationManager.cancel(3);
			}
			else if(extras.containsKey(BundleKey.SNOOZE))
			{
				stopAlarm();
				snooze(extras.getLong(BundleKey.SNOOZE));
				Intent i = new Intent();
				i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
				i.putExtra(BundleKey.SNOOZE, true);
				sendBroadcast(i);

			}

			updateApplicationState();
			mApplicationStatePersistenceManager.setSavedState(mApplicationState);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//TODO do something useful
//		AlarmManagerHelper.setAlarms(this);
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		//TODO for communication return IBinder implementation
		return null;
	}

	@Override
	public void onDestroy()
	{
		Log.v(TAG, "ON DESTROY");
		unregisterReceiver(mReceiver);
		super.onDestroy();

	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.w("TAG", "TimerService---OnCreate ");
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mApplicationStatePersistenceManager = new ApplicationStatePersistenceManager(this);
		mApplicationState = mApplicationStatePersistenceManager.getSavedState();

		IntentFilter filter = new IntentFilter();
		filter.addAction(BundleKey.UPDATE_TIMER_SETTINGS);
		registerReceiver(mReceiver,filter);
	}

	private void snooze(long snooze)
	{
		stopAlarm();
		if(mCountDown!=null)
		{
			mCountDown.cancel();
		}

		mCountDown = new DetailedCountDown(snooze,1000L);
		mCountDown.start();
		scheduleAlarm(snooze);
	}

	private void presentPlayingNotification()
	{
		CharSequence contentText = NOTIFICATION_TITLE_TIMEFLY;//currentPlayable.getTitle();
		mNotification =
				new Notification(R.drawable.timeflylogo,
						contentText,
						System.currentTimeMillis());
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		Context context = getApplicationContext();
		CharSequence title = getString(R.string.app_name);

		Class<?> notificationActivity = PerformingHabbit.class;

		Intent notificationIntent = new Intent(this, notificationActivity);
		//			if (currentPlayable.getActivityData() != null) {
		//				notificationIntent.putExtra(Constants.EXTRA_ACTIVITY_DATA,
		//						currentPlayable.getActivityData());
		//				notificationIntent.putExtra(Constants.EXTRA_DESCRIPTION,
		//						R.string.msg_main_subactivity_nowplaying);
		//			}
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
		notificationIntent.putExtra(BundleKey.GOAL_NAME,mGoal_name) ;
		notificationIntent.putExtra(BundleKey.GOAL_ID, mGoal_id) ;
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		mNotification.setLatestEventInfo(context, title, contentText, contentIntent);

		 mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the mNotification later on.
		mNotificationManager.notify(3, mNotification);

	}

	private void start()
	{
		if(mStarted)
		{
			Intent i = new Intent();
			i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
			i.putExtra(BundleKey.ERROR_ALREADY_STARTED,true);
			sendBroadcast(i);
			return;
		}

		if (startingCountdownTime == 0)
		{

			//TODO ALERT that Time is not set
			Intent i = new Intent();
			i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
			i.putExtra(BundleKey.TIME_NOT_SET,true);
			sendBroadcast(i);


			return;
		}
		mStarted = true;

		presentPlayingNotification();

		stopAlarm();
		if(mCountDown!=null)
		{
			mCountDown.cancel();
		}
		mCountDown = new DetailedCountDown(startingCountdownTime,1000L);
		mCountDown.start();
		//TODO RIGHT HERE SET ALARM MANAGER
		scheduleAlarm(startingCountdownTime);

		Intent i = new Intent();
		i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
		i.putExtra(BundleKey.START,true);
		i.putExtra(BundleKey.GOAL_NAME,mGoal_name);
		i.putExtra(BundleKey.GOAL_ID,mGoal_id);
		sendBroadcast(i);
	}


	private void scheduleAlarm(long timeInFuture)
	{
		Log.v(TAG, "Schedule Alarm Manager "+timeInFuture);

		Intent intent = new Intent(this, CountdownAlarmManagerReceiver.class);
		intent.putExtra(BundleKey.GOAL_TIME,timeInFuture);
		if(mGoal_name!=null && mGoal_name.length()!=0)
		{
			intent.putExtra(BundleKey.GOAL_NAME, mGoal_name);
		}
		if(mGoal_id>0)
		{
			intent.putExtra(BundleKey.GOAL_ID, mGoal_id);
		}
		intent.putExtra(BundleKey.TIME_STARTED, mInitialStartingTime);
		PendingIntent pintent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);


		mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+timeInFuture, pintent);

		//TODO FINISH THE ALARM. Schedule this alarm such that it replaces the current countdown mechanism and allows for the service
		//to be killed at any time.
	}

	private class DetailedCountDown extends CountDownTimer
	{

		public DetailedCountDown(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
//			originalTimeInMillis = millisInFuture;
//			timeSpent = 0;
		}

		@Override
		public void onTick(long l)
		{
			//send Broadcast
			Intent i = new Intent();
			i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
			i.putExtra(BundleKey.UPDATE_TIME, l);
			sendBroadcast(i);



			Class<?> notificationActivity = PerformingHabbit.class;

			Intent notificationIntent = new Intent(getApplicationContext(), notificationActivity);
			notificationIntent.setAction(Intent.ACTION_VIEW);
			notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
			notificationIntent.putExtra(BundleKey.GOAL_NAME,mGoal_name) ;
			notificationIntent.putExtra(BundleKey.GOAL_ID,mGoal_id) ;
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			int mins = (int) (l / 60000);
			int secs = (int) ((l % 60000) / 1000);

			String currentTime = String.format("%02d", mins) + ":" + String.format("%02d", secs);

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			mNotification.setLatestEventInfo(getApplicationContext(), "Practise Countdown:", currentTime, contentIntent);

			mNotificationManager .notify(3, mNotification);

		}

		@Override
		public void onFinish()
		{

//			finishCountdown();

		}
	}


	private void finishCountdown()
	{
		{
			Log.v(TAG, "FINISH COUNTDOWN") ;
			//send Broadcast
			Intent i = new Intent();
			i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
			i.putExtra(BundleKey.UPDATE_TIME, 0L);
			sendBroadcast(i);


			Class<?> notificationActivity = PerformingHabbit.class;

			Intent notificationIntent = new Intent(getApplicationContext(), notificationActivity);
			notificationIntent.setAction(Intent.ACTION_VIEW);
			notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
			notificationIntent.putExtra(BundleKey.GOAL_NAME, mGoal_name);
			notificationIntent.putExtra(BundleKey.GOAL_ID, mGoal_id);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			int mins =  0;
			int secs = 0;

			String currentTime = String.format("%02d", mins) + ":" + String.format("%02d", secs);

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			mNotification.setLatestEventInfo(getApplicationContext(), "Practise Countdown:", currentTime, contentIntent);

			mNotificationManager.notify(3, mNotification);
		}

		Class<?> notificationActivity = PerformingHabbit.class;

		Intent notificationIntent = new Intent(getApplicationContext(), notificationActivity);
		//			if (currentPlayable.getActivityData() != null) {
		//				notificationIntent.putExtra(Constants.EXTRA_ACTIVITY_DATA,
		//						currentPlayable.getActivityData());
		//				notificationIntent.putExtra(Constants.EXTRA_DESCRIPTION,
		//						R.string.msg_main_subactivity_nowplaying);
		//			}
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
		notificationIntent.putExtra(BundleKey.GOAL_NAME,mGoal_name) ;
		notificationIntent.putExtra(BundleKey.GOAL_ID,mGoal_id) ;
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		mNotification.setLatestEventInfo(getApplicationContext(), "Alert: Practise Finished", "Return to Practise", contentIntent);

		mNotificationManager .notify(3, mNotification);

		Log.v(TAG, "START ALARM");
		startAlarm();
		Intent i = new Intent();
		i.setAction(BundleKey.TIMER_VISUAL_ADDRESS);
		i.putExtra(BundleKey.ALARM,true);
		i.putExtra(BundleKey.GOAL_NAME,mGoal_name);
		i.putExtra(BundleKey.GOAL_ID,mGoal_id);
		sendBroadcast(i);
	}

	private void stopAlarm()
	{
		if(mp!=null)
		{
			mp.setOnCompletionListener(null);
			mp.stop();
		}
	}

	 private void startAlarm()
	 {
		 if(mp==null || (mp!=null && !mp.isPlaying()))
		 {
			 mApplicationState.setApplicationState(STATE.RINGING);
			 mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
			 mp.setLooping(true);
			 mp.start();
		 }
	 }

}