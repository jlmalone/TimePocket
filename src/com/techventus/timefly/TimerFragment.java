package com.techventus.timefly;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.techventus.timefly.R;

/**
 * refactor this as an intent service
 *
 * @author Victoria Hansen
 */
public class TimerFragment extends SherlockFragment
{

	public static final String TAG = TimerFragment.class.getSimpleName();

	TimerListener mCallback;

	// Container Activity must implement this interface
	public interface TimerListener
	{
		public void onTimerFinished(long intendedTime, long timeSpent);

		public void onTimerStarted();

	}

	int startingtime;
	long orig;
	long spent;
	long pausetime = 0;

	@Override
	public void onPause()
	{
		super.onPause();
		Log.v(TAG, "ON PAUSE");
		//		orig = mCountDownTimer.getOriginalTime();
		//		spent = spent + mCountDownTimer.getTimeSpent();// .originalTimeInMillis();
		//		pausetime = System.currentTimeMillis();
		//		mCountDownTimer.cancel();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try
		{
			Log.v(TAG, "ATTACHING ACTIVITY");
			mCallback = (TimerListener) activity;
			if (started)
			{
				mCallback.onTimerStarted();
			}

		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement TimerListener");
		}
	}

	@Override
	public void onDetach()
	{
		Log.v(TAG, "DETACHING ACTIVITY");
		super.onDetach();
		mCallback = null;
	}

	boolean started = false;
	boolean finished = false;
	boolean stopped = false;

	TextView clock;

	Button timer_button;

	DetailedCountDownTimer mCountDownTimer;

	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (container == null)
		{

			return null;

		}
		setRetainInstance(true);
		return (LinearLayout) inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);
		clock = (TextView) page.findViewById(R.id.timer);
		timer_button = (Button) page.findViewById(R.id.timer_action_button);
		timer_button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				if (!started)
				{
					start();

				}
				else if (!finished && !stopped)
				{
					stop();
					// pause
					// pause();

				}
				else
				{
					// reset();
				}
				// else
				// stop();

			}
		});

	}


	private void stop()
	{


		if (mCountDownTimer == null)
		{
			//TODO REPORT ERROR HOW DID THIS HAPPEN
			Log.v(TAG, "mCountDownTimer is NULL. HOW DID IT HAPPEN?");
			return;
		}

		stopped = true;

		if (started && !finished)
		{
			long origTime = mCountDownTimer.getOriginalTime();
			long timeSpent = mCountDownTimer.getTimeSpent();
			mCountDownTimer.cancel();
			mCallback.onTimerFinished(origTime, timeSpent);
		}
	}

	private void start()
	{
		presentPlayingNotification();
		started = true;

		if (startingtime == 0)
		{
			createTime();
			return;
		}

		mCallback.onTimerStarted();

		mCountDownTimer = new DetailedCountDownTimer(startingtime, 1000);
		mCountDownTimer.start();

		this.timer_button.setText(getActivity().getResources().getString(R.string.stop));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.v(TAG, "ON RESUME " + orig + " " + spent);
		//		mCountDownTimer = new DetailedCountDownTimer(orig - spent, 500);
		//		if (started && !stopped && !finished) {
		//			mCountDownTimer.start();
		//		}

		//todo add and if dialog not already showing
		if (startingtime <= 0)
		{
			Log.v(TAG, "starting time less than 0");
			createTime();
		}

		if (started)
		{
			timer_button.setText(getActivity().getResources().getString(R.string.stop));
		}
	}

	private void presentPlayingNotification()
	{
		CharSequence contentText = "TimeFly";//currentPlayable.getTitle();
		Notification notification =
				new Notification(R.drawable.timeflylogo,
						contentText,
						System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		Context context = getActivity().getApplicationContext();
		CharSequence title = getString(R.string.app_name);

		Class<?> notificationActivity = PerformingHabbit.class;

		Intent notificationIntent = new Intent(getActivity().getApplicationContext(), notificationActivity);
		//			if (currentPlayable.getActivityData() != null) {
		//				notificationIntent.putExtra(Constants.EXTRA_ACTIVITY_DATA,
		//						currentPlayable.getActivityData());
		//				notificationIntent.putExtra(Constants.EXTRA_DESCRIPTION,
		//						R.string.msg_main_subactivity_nowplaying);
		//			}
		notificationIntent.setAction(Intent.ACTION_VIEW);
		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
		//			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent,0);
		notification.setLatestEventInfo(context, title, contentText, contentIntent);

		NotificationManager mNotificationManager =
				(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(3,notification);


		//			.notify(mId, mBuilder.build());

		//			getActivity().startForeground(4, notification);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	class DetailedCountDownTimer extends CountDownTimer
	{

		private long originalTimeInMillis;
		private long timeSpent;

		public long getOriginalTime()
		{
			return originalTimeInMillis;
		}

		public long getTimeSpent()
		{
			return timeSpent;
		}

		public DetailedCountDownTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
			originalTimeInMillis = millisInFuture;
			timeSpent = 0;
		}

		@Override
		public void onFinish()
		{
			timeSpent = originalTimeInMillis;
			clock.setText("done!");
			Log.v(TAG, "mCallback null? " + (mCallback == null));
			Log.v(TAG, "TIMES " + getOriginalTime() + " " + getTimeSpent());

			try
			{
				if (getActivity() != null)
				{
					Log.v(TAG, "ERROR ACTIVITY IS NULL");


					//					Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					//				     if(alert == null){
					//				         // alert is null, using backup
					//				         alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					//				         if(alert == null){  // I can't see this ever being null (as always have a default notification) but just incase
					//				             // alert backup is null, using 2nd backup
					//				             alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					//				         }
					//				     }
					MediaPlayer mp = MediaPlayer.create(getActivity(), R.raw.bell);
					//					mp.setDataSource(getActivity(), alert);

					//					final AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
					//					 if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					//						 		mp.setAudioStreamType(AudioManager.STREAM_ALARM);
					//					            mp.setLooping(true);
					//					            mp.prepare();
					//					            mp.start();
					//					  }
					try
					{
						//						mp.setAudioStreamType(AudioManager.STREAM_ALARM);
						mp.prepare();

					}
					catch (IllegalStateException e)
					{
						e.printStackTrace();
						mp.start();


					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					return;
				}
			}
			catch (Exception e)
			{
				Log.v(TAG, "ERROR ");
				e.printStackTrace();
			}


			if (mCallback != null)
			{
				mCallback.onTimerFinished(getOriginalTime(), getTimeSpent());
			}
		}



		@Override
		public void onTick(long millisUntilFinished)
		{
			timeSpent = originalTimeInMillis - millisUntilFinished;

			int mins = (int) (millisUntilFinished / 60000);
			int secs = (int) ((millisUntilFinished % 60000) / 1000);

			clock.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));

			Log.v(TAG, "TICK " + secs + " " + timeSpent);

		}

	}

	private void createTime()
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_create_timer);
		dialog.setTitle(getResources().getString(R.string.add_time));

		final EditText goal_input = (EditText) dialog.findViewById(R.id.time_input);

		Button dialogButton = (Button) dialog.findViewById(R.id.enter_time_button);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String input = goal_input.getText().toString();
				try
				{
					int inp = Integer.parseInt(input);
					if (inp > 0)
					{

						startingtime = inp * 60000;
						clock.setText(inp + ":00");
						Thread.sleep(100);
						dialog.dismiss();
					}
					else
					{
						goal_input.getText().clear();
					}
				}
				catch (Exception e)
				{
					goal_input.getText().clear();
				}

			}
		});

		dialog.show();
	}

}
