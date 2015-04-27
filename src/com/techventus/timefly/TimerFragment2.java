package com.techventus.timefly;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

/**
 * Created by Joseph on 22.02.14.
 */
public class TimerFragment2 extends SherlockFragment
{

	public static String TAG = TimerFragment2.class.getSimpleName();
	//Start Time
	private TimerListener mCallback;

//	private boolean started;

	private boolean startIndicatorBool;
	private boolean timerStartedBool;
	private boolean alarmBool;
	private boolean snoozeBool;

	private Button start_timer_button;
	private Button end_button;
	private TextView clock;
	private long startingtime = 0;
	private Dialog snoozeDialog;


	// Container Activity must implement this interface
	public interface TimerListener
	{
		public void onTimerStarted();
	}


	boolean snoozing = false;

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

			//TODO IF TIMER HAS BEEN STARTED CALL onTIMERSTARTED
//			if (started)
//			{
//				mCallback.onTimerStarted();
//			}



		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement TimerListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		if (startingtime == 0)
		{
			createTime();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (snoozing)
		{
//			if (snoozeDialog != null && snoozeDialog.isShowing())
//			{
//				try
//				{
//					snoozeDialog.dismiss();
//				}
//				catch (java.lang.IllegalArgumentException e)
//				{
//					//TODO REPORT ERROR TO ANALYTICS
//					e.printStackTrace();
//				}
//			}
			snoozeDialogue();
		}
	}

	ViewGroup vg;

	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		vg = container;
		Log.v(TAG, "On Create View");
		if (savedInstanceState != null)
		{
			// Do your oncreate stuff because there is no bundle
			Log.v(TAG, "savedInstanceState not null");
			return null;
		}

		if (container == null)
		{
			Log.v(TAG, "Container Null");
			return null;

		}
		return inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			// Do your oncreate stuff because there is no bundle
			return;
		}
		Log.v(TAG, "On  View Created");
		super.onViewCreated(page, savedInstanceState);

		start_timer_button = (Button) page.findViewById(R.id.start_timer_button);
		clock = (TextView) page.findViewById(R.id.timer);
		end_button = (Button) page.findViewById(R.id.stop_button);
		start_timer_button.setOnClickListener(start_timer_click);
		end_button.setOnClickListener(end_click);

		setupComplete(startIndicatorBool);
		snooze(snoozeBool);
		timerStarted(timerStartedBool);

//		if (snoozing)
//		{
//			if (snoozeDialog != null && snoozeDialog.isShowing())
//			{
//				snoozeDialog.dismiss();
//			}
//			snoozeDialogue();
//		}
	}


	View.OnClickListener end_click = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			snoozing = false;
			Intent intent = new Intent();
			intent.setAction(TimerService.BundleKey.UPDATE_TIMER_SETTINGS);
			intent.putExtra(TimerService.BundleKey.STOP, true);
			getActivity().sendBroadcast(intent);
			if(snoozeDialog!=null && snoozeDialog.isShowing())
			{
				snoozeDialog.dismiss();
				vg.removeView(snoozeDialog.getWindow().getDecorView());
			}

		}
	};

	View.OnClickListener snoozeTimeClick(final long snoozetime)
	{

		View.OnClickListener snooze_click = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

					Log.v(TAG, "TRY TO DISMISS SNOOZE DIALOG");
					snoozeDialog.dismiss();
					vg.removeView(snoozeDialog.getWindow().getDecorView());
//					TimerFragment2.this.
				snoozing = false;
				Intent intent = new Intent();
				intent.setAction(TimerService.BundleKey.UPDATE_TIMER_SETTINGS);
				intent.putExtra(TimerService.BundleKey.SNOOZE, snoozetime);
				getActivity().sendBroadcast(intent);


			}
		};
		return snooze_click;
	}

	View.OnClickListener start_timer_click = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Log.v("TIMER FRAGMENT","Start Indicator Boolean "+startIndicatorBool);
			if (!startIndicatorBool)
			{
				createTime();
			}
			else
			{
				Intent intent = new Intent();
				intent.setAction(TimerService.BundleKey.UPDATE_TIMER_SETTINGS);
				intent.putExtra(TimerService.BundleKey.START, true);
				getActivity().sendBroadcast(intent);
				if (mCallback != null)    //TODO MAYBE CAN REMOVE NULL CHECK .put as precaution
				{
					mCallback.onTimerStarted();
				}
			}
		}
	};


	public void timerSetup()
	{
		Intent intent = new Intent();
		intent.setAction(TimerService.BundleKey.UPDATE_TIMER_SETTINGS);
		intent.putExtra(TimerService.BundleKey.NEW_TIME, startingtime);
		getActivity().sendBroadcast(intent);
	}


	public void setupComplete()
	{
		startIndicatorBool = true;
		setupComplete(startIndicatorBool);
		Log.v(TAG,"setup complete triggered "+startIndicatorBool +" "+start_timer_button.getVisibility());
	}

	public void setupComplete(boolean bool)
	{
		if (bool)
		{
			start_timer_button.setVisibility(View.VISIBLE);
		}

	}

	public void timerStarted()
	{

		timerStartedBool = true;
		timerStarted(timerStartedBool);
	}

	public void timerStarted(boolean bool)
	{
		if (bool)
		{
			start_timer_button.setVisibility(View.GONE);
			end_button.setVisibility(View.VISIBLE);
		}

	}

	public void alarmActive()
	{
		alarmBool = true;
		alarmActive(alarmBool);
	}

	public void alarmActive(boolean bool)
	{
		if (bool)
		{
			snoozeDialogue();
		}
	}

	public void snooze()
	{
		snoozeBool = true;
		snooze(snoozeBool);
	}

	public void snooze(boolean bool)
	{
		if (bool)
		{
			end_button.setVisibility(View.VISIBLE);
		}
	}


	public void updateTime(long time)
	{
		int mins = (int) (time / 60000);
		int secs = (int) ((time % 60000) / 1000);
		clock.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));

	}

	private void snoozeDialogue()
	{
		Log.v(TAG, "SNOOZE DIALOG CALLED");
		if(snoozeDialog==null)
		{
			snoozeDialog = new Dialog(getActivity());
			snoozeDialog.setContentView(R.layout.dialog_snooze);
			snoozeDialog.setTitle(getResources().getString(R.string.snooze));
			//		snoozeDialog.getWindow().getDecorView().getId();//.getWindow().getDecorView().rem

			Button end = (Button) snoozeDialog.findViewById(R.id.end);
			Button five_mins = (Button) snoozeDialog.findViewById(R.id.five_mins);
			Button ten_mins = (Button) snoozeDialog.findViewById(R.id.ten_mins);
			Button half_hour = (Button) snoozeDialog.findViewById(R.id.half_hour);

			end.setOnClickListener(end_click);
			five_mins.setOnClickListener(snoozeTimeClick(5 * 60 * 1000L));
			ten_mins.setOnClickListener(snoozeTimeClick(10 * 60 * 1000L));
			half_hour.setOnClickListener(snoozeTimeClick(30 * 60 * 1000L));
		}
		snoozeDialog.show();
		snoozing = true;
	}


	private void createTime()
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_create_timer);
		dialog.setTitle(getResources().getString(R.string.add_time));

		final EditText goal_input = (EditText) dialog.findViewById(R.id.time_input);

		Button dialogButton = (Button) dialog.findViewById(R.id.enter_time_button);

		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new View.OnClickListener()
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
						//send start time.
						Intent i = new Intent();

						startingtime = inp * 60000;
						timerSetup();
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