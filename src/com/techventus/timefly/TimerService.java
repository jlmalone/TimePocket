package com.techventus.timefly;

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

import java.io.IOException;

/**
 * Created by Joseph on 16.02.14.
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
		public static final String TIME_SPENT = "TIME_SPENT";
		public static final String NEW_TIME = "NEW_TIME";
		public static final String TIME_NOT_SET = "TIME_NOT_SET";
		public static final String UPDATE_TIME = "UPDATE_TIME";
		public static final String STATE = "STATE";
		public static final String GOAL_NAME = "GOAL_NAME";
		public static final String GOAL_ID = "GOAL_ID";
		public static final String GET_STATE = "GET_STATE";
	}

	int goal_id = -1;
	String goal_name = "";


	long startingCountdownTime = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO do something useful
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		//TODO for communication return IBinder implementation
		return null;
	}


	private String STATE = "NOT_SET";

	long initialStartingTime;

	 private static final String TAG = TimerService.class.getSimpleName();

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Bundle extras = intent.getExtras() ;

			int temp_goal_id =  extras.getInt(BundleKey.GOAL_ID,-1);
			String temp_goal_name = extras.getString(BundleKey.GOAL_NAME,"");

			if(temp_goal_id != -1)
			{
				goal_id = temp_goal_id;
			}
			if(!temp_goal_name .equals(""))
			{
				goal_name = temp_goal_name ;
			}


			if(extras.containsKey(BundleKey.NEW_TIME))
			{
				STATE = BundleKey.SETUP;
				startingCountdownTime = extras.getLong(BundleKey.NEW_TIME);
				Intent i = new Intent();
				i.setAction("com.techventus.timefly.updatetimervisuals");
				i.putExtra(BundleKey.SETUP,true);
				sendBroadcast(i);
			}
			else if(extras.containsKey(BundleKey.START))
			{

				STATE = BundleKey.START;
				initialStartingTime = System.currentTimeMillis();
				start();

			}
			else if(extras.containsKey(BundleKey.STOP))
			{
				STATE = BundleKey.STOP;
				Intent i = new Intent();
				i.setAction("com.techventus.timefly.updatetimervisuals");
				i.putExtra(BundleKey.FINISH, true);
				i.putExtra(BundleKey.TIME_SPENT,(System.currentTimeMillis()-initialStartingTime));
				i.putExtra(BundleKey.GOAL_NAME,goal_name) ;
				i.putExtra(BundleKey.GOAL_ID,goal_id) ;
				sendBroadcast(i);
				stopAlarm();
				if(mCountDown!=null)
				{
					mCountDown.cancel();

				}
				mNotificationManager.cancel(3);
			}
			else if(extras.containsKey(BundleKey.GET_STATE))
			{
				Intent i = new Intent();
				i.setAction("com.techventus.timefly.updatetimervisuals");
				i.putExtra(BundleKey.STATE,STATE);


				if(STATE.equals("STARTED"))
				{
					i.putExtra(BundleKey.UPDATE_TIME,millisUntilFinished);
				}
				sendBroadcast(i);
			}
			else if(extras.containsKey(BundleKey.SNOOZE))
			{
				stopAlarm();
				snooze(extras.getLong(BundleKey.SNOOZE));
				Intent i = new Intent();
				i.setAction("com.techventus.timefly.updatetimervisuals");
				i.putExtra(BundleKey.SNOOZE,true);
				sendBroadcast(i);

			}

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



	public void onCreate()
	{
		super.onCreate();
		Log.w("TAG", "ScreenListenerService---OnCreate ");

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.techventus.timefly.updatetimersettings");
		registerReceiver(mReceiver,filter);

	}



//
//
//	DetailedCountDownTimer mCountDownTimer;
//
	boolean started = false;
//	boolean finished = false;
//	boolean stopped = false;
//

//
//
//	private void stop()
//	{
//
//		if (mCountDownTimer == null)
//		{
//			//TODO REPORT ERROR HOW DID THIS HAPPEN
//			Log.v(TAG, "mCountDownTimer is NULL. HOW DID IT HAPPEN?");
//			return;
//		}
//
//		stopped = true;
//
//		if (started && !finished)
//		{
//			long origTime = mCountDownTimer.getOriginalTime();
//			long timeSpent = mCountDownTimer.getTimeSpent();
//			mCountDownTimer.cancel();
//
//			//TIMER FINISHED
//
//
////			mCallback.onTimerFinished(origTime, timeSpent);
//		}
//	}
//

	private void snooze(long snooze)
	{
	      stopAlarm();
		if(mCountDown!=null)
		{
			mCountDown.cancel();

		}

		mCountDown = new DetailedCountDown(snooze,1000L);
		mCountDown.start();
	}

	Notification notification;
	NotificationManager mNotificationManager;


	private void presentPlayingNotification()
	{
		CharSequence contentText = "TimeFly";//currentPlayable.getTitle();
		notification =
				new Notification(R.drawable.timeflylogo,
						contentText,
						System.currentTimeMillis());
		notification.flags = Notification.FLAG_ONGOING_EVENT;
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
		notificationIntent.putExtra(BundleKey.GOAL_NAME,goal_name) ;
		notificationIntent.putExtra(BundleKey.GOAL_ID,goal_id) ;
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, title, contentText, contentIntent);

		 mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(3,notification);

	}




	private void start()
	{

		if(started)
		{
			Intent i = new Intent();
			i.setAction("com.techventus.timefly.updatetimervisuals");
			i.putExtra(BundleKey.ERROR_ALREADY_STARTED,true);
			sendBroadcast(i);
			return;
		}
		//		presentPlayingNotification();


		if (startingCountdownTime == 0)
		{

			//TODO ALERT that Time is not set
			Intent i = new Intent();
			i.setAction("com.techventus.timefly.updatetimervisuals");
			i.putExtra(BundleKey.TIME_NOT_SET,true);
			sendBroadcast(i);

			return;
		}
		started = true;

		presentPlayingNotification();





		stopAlarm();
		if(mCountDown!=null)
		{
			mCountDown.cancel();

		}
		mCountDown = new DetailedCountDown(startingCountdownTime,1000L);
		mCountDown.start();

		Intent i = new Intent();
		i.setAction("com.techventus.timefly.updatetimervisuals");
		i.putExtra(BundleKey.START,true);
		i.putExtra(BundleKey.GOAL_NAME,goal_name) ;
		i.putExtra(BundleKey.GOAL_ID,goal_id) ;
		sendBroadcast(i);

	}


	boolean millisUntilFinished;
	DetailedCountDown mCountDown;

	class DetailedCountDown extends CountDownTimer
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
			i.setAction("com.techventus.timefly.updatetimervisuals");
			i.putExtra(BundleKey.UPDATE_TIME,l);
			sendBroadcast(i);



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
			notificationIntent.putExtra(BundleKey.GOAL_NAME,goal_name) ;
			notificationIntent.putExtra(BundleKey.GOAL_ID,goal_id) ;
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			int mins = (int) (l / 60000);
			int secs = (int) ((l % 60000) / 1000);

			String currentTime = String.format("%02d", mins) + ":" + String.format("%02d", secs);

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
				notification.setLatestEventInfo(getApplicationContext(), "Practise Countdown:", currentTime, contentIntent);

				 mNotificationManager .notify(3, notification);

		}

		@Override
		public void onFinish()
		{

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
			notificationIntent.putExtra(BundleKey.GOAL_NAME,goal_name) ;
			notificationIntent.putExtra(BundleKey.GOAL_ID,goal_id) ;
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(), "Alert: Practise Finished", "Return to Practise", contentIntent);

			mNotificationManager .notify(3, notification);


			startAlarm();
			Intent i = new Intent();
			i.setAction("com.techventus.timefly.updatetimervisuals");
			i.putExtra(BundleKey.ALARM,true);
			i.putExtra(BundleKey.GOAL_NAME,goal_name);
			i.putExtra(BundleKey.GOAL_ID,goal_id);
			sendBroadcast(i);
		}
	}



	MediaPlayer mp;
	String PlayerState;


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


		 mp = MediaPlayer.create(getApplicationContext(),  R.raw.bell);
		 mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			 public void onCompletion(MediaPlayer mp)
			 {
				 mp.create(getApplicationContext(), R.raw.bell);
//				 try
//				 {
//					 mp.prepare();
//				 }
//				 catch (IOException e)
//				 {
//					 e.printStackTrace();
//				 }
				 mp.start();
			 }

		 });

		 mp.start();



	 }









//
//
//	class DetailedCountDownTimer extends CountDownTimer
//	{
//
//		private long originalTimeInMillis;
//		private long timeSpent;
//
//		public long getOriginalTime()
//		{
//			return originalTimeInMillis;
//		}
//
//		public long getTimeSpent()
//		{
//			return timeSpent;
//		}
//
//		public DetailedCountDownTimer(long millisInFuture, long countDownInterval)
//		{
//			super(millisInFuture, countDownInterval);
//			originalTimeInMillis = millisInFuture;
//			timeSpent = 0;
//		}
//
//		@Override
//		public void onFinish()
//		{
//			timeSpent = originalTimeInMillis;
//			Intent finishIntent = new Intent() ;
//			finishIntent.setAction("com.techventus.timefly.updatetimervisual");
//			finishIntent.putExtra(BundleKey.ALARM, true);
//			finishIntent.putExtra(BundleKey.TIME_SPENT, timeSpent);
//
//			clock.setText("done!");
//			Log.v(TAG, "mCallback null? " + (mCallback == null));
//			Log.v(TAG, "TIMES " + getOriginalTime() + " " + getTimeSpent());
//
//			try
//			{
//				if (TimerService.this != null)
//				{
//					Log.v(TAG, "ERROR ACTIVITY IS NULL");
//
//
//					//					Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//					//				     if(alert == null){
//					//				         // alert is null, using backup
//					//				         alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//					//				         if(alert == null){  // I can't see this ever being null (as always have a default notification) but just incase
//					//				             // alert backup is null, using 2nd backup
//					//				             alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//					//				         }
//					//				     }
//					MediaPlayer mp = MediaPlayer.create(TimerService.this, R.raw.bell);
//					//					mp.setDataSource(getActivity(), alert);
//
//					//					final AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
//					//					 if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//					//						 		mp.setAudioStreamType(AudioManager.STREAM_ALARM);
//					//					            mp.setLooping(true);
//					//					            mp.prepare();
//					//					            mp.start();
//					//					  }
//					try
//					{
//						//						mp.setAudioStreamType(AudioManager.STREAM_ALARM);
//						mp.prepare();
//
//					}
//					catch (IllegalStateException e)
//					{
//						e.printStackTrace();
//						mp.start();
//
//
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//				}
//				else
//				{
//					return;
//				}
//			}
//			catch (Exception e)
//			{
//				Log.v(TAG, "ERROR ");
//				e.printStackTrace();
//			}
//
//
//			if (mCallback != null)
//			{
//				mCallback.onTimerFinished(getOriginalTime(), getTimeSpent());
//			}
//		}
//
//		void returnCallToSetup()
//		{
//			Intent finishIntent = new Intent() ;
//			finishIntent.setAction("com.techventus.timefly.updatetimervisual");
//		}
//
//		@Override
//		public void onTick(long millisUntilFinished)
//		{
//			timeSpent = originalTimeInMillis - millisUntilFinished;
//
//			int mins = (int) (millisUntilFinished / 60000);
//			int secs = (int) ((millisUntilFinished % 60000) / 1000);
//
//			clock.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
//
//			Log.v(TAG, "TICK " + secs + " " + timeSpent);
//
//		}
//
//	}

}
