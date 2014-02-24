package com.techventus.timefly;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockFragment;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import com.techventus.timefly.R;

/**
 * refactor this as an intent service
 *
 * @author Joseph Malone
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

	void sendStartSignal()
	{
		Intent startIntent = new Intent();
	}

//
//
//	/**
//	 * (non-Javadoc)
//	 *
//	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//	 * android.view.ViewGroup, android.os.Bundle)
//	 */
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		if (container == null)
//		{
//
//			return null;
//
//		}
//		setRetainInstance(true);
//		return (LinearLayout) inflater.inflate(R.layout.fragment_timer, container, false);
//	}
//
//	@Override
//	public void onViewCreated(View page, Bundle savedInstanceState)
//	{
//		super.onViewCreated(page, savedInstanceState);
//		clock = (TextView) page.findViewById(R.id.timer);
//		timer_button = (Button) page.findViewById(R.id.timer_action_button);
//		timer_button.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View arg0)
//			{
//				if (!started)
//				{
//					start();
//
//				}
//				else if (!finished && !stopped)
//				{
//					stop();
//					// pause
//					// pause();
//
//				}
//				else
//				{
//					// reset();
//				}
//				// else
//				// stop();
//
//			}
//		});
//
//	}
//
//
//	@Override
//	public void onResume()
//	{
//		super.onResume();
//
//		Log.v(TAG, "ON RESUME " + orig + " " + spent);
//		//		mCountDownTimer = new DetailedCountDownTimer(orig - spent, 500);
//		//		if (started && !stopped && !finished) {
//		//			mCountDownTimer.start();
//		//		}
//
//		//todo add and if dialog not already showing
//		if (startingtime <= 0)
//		{
//			Log.v(TAG, "starting time less than 0");
//			createTime();
//		}
//
//		if (started)
//		{
//			timer_button.setText(getActivity().getResources().getString(R.string.stop));
//		}
//	}
//
//	private void presentPlayingNotification()
//	{
//		CharSequence contentText = "TimeFly";//currentPlayable.getTitle();
//		Notification notification =
//				new Notification(R.drawable.timeflylogo,
//						contentText,
//						System.currentTimeMillis());
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
//		Context context = getActivity().getApplicationContext();
//		CharSequence title = getString(R.string.app_name);
//
//		Class<?> notificationActivity = PerformingHabbit.class;
//
//		Intent notificationIntent = new Intent(getActivity(), notificationActivity);
//		//			if (currentPlayable.getActivityData() != null) {
//		//				notificationIntent.putExtra(Constants.EXTRA_ACTIVITY_DATA,
//		//						currentPlayable.getActivityData());
//		//				notificationIntent.putExtra(Constants.EXTRA_DESCRIPTION,
//		//						R.string.msg_main_subactivity_nowplaying);
//		//			}
//		notificationIntent.setAction(Intent.ACTION_VIEW);
//		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
//					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//				notificationIntent,0);
//		notification.setLatestEventInfo(context, title, contentText, contentIntent);
//
//		NotificationManager mNotificationManager =
//				(NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify(3,notification);
//
//
//		//			.notify(mId, mBuilder.build());
//
//		//			getActivity().startForeground(4, notification);
//	}
//
//
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//	{
//		inflater.inflate(R.menu.main, menu);
//		super.onCreateOptionsMenu(menu, inflater);
//	}
//
//
//	private void createTime()
//	{
//		final Dialog dialog = new Dialog(getActivity());
//		dialog.setContentView(R.layout.dialog_create_timer);
//		dialog.setTitle(getResources().getString(R.string.add_time));
//
//		final EditText goal_input = (EditText) dialog.findViewById(R.id.time_input);
//
//		Button dialogButton = (Button) dialog.findViewById(R.id.enter_time_button);
//		// if button is clicked, close the custom dialog
//		dialogButton.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				String input = goal_input.getText().toString();
//				try
//				{
//					int inp = Integer.parseInt(input);
//					if (inp > 0)
//					{
//						//send start time.
//						Intent i = new Intent() ;
//
//						startingtime = inp * 60000;
//						clock.setText(inp + ":00");
//						Thread.sleep(100);
//						dialog.dismiss();
//					}
//					else
//					{
//						goal_input.getText().clear();
//					}
//				}
//				catch (Exception e)
//				{
//					goal_input.getText().clear();
//				}
//
//			}
//		});
//
//		dialog.show();
//	}

}
