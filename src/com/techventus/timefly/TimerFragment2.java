package com.techventus.timefly;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import org.w3c.dom.Text;

/**
 * Created by Joseph on 22.02.14.
 */
public class TimerFragment2  extends SherlockFragment
{

   public static String TAG = TimerFragment2.class.getSimpleName();
	//Start Time
	TimerListener mCallback;

	boolean started;

	boolean startIndicatorBool;
	boolean timerStartedBool;
	boolean alarmBool ;
	boolean snoozeBool;

	// Container Activity must implement this interface
	public interface TimerListener
	{
//		public void onTimerFinished(long intendedTime, long timeSpent);

		public void onTimerStarted();

	}


//	TextView  timer_setup_text;
//	TextView start_indicator;
//	TextView current_time;
//	TextView snoozed;
//	TextView ended;
//
//	Button   timer_setup_button;
	Button start_timer_button;
	Button snooze_button;
//
	Button end_button;

	Button timer_button;
	TextView clock;

//	public void sayHello()
//	{
//		Toast.makeText(getActivity(),"Hello There",Toast.LENGTH_SHORT);
//	}


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
			if (started)
			{
				mCallback.onTimerStarted();
			}

			if(snoozing)
			{
				if(snoozeDialog!=null && snoozeDialog.isShowing())
					snoozeDialog.dismiss();
				snoozeDialogue();
			}

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

		 h = new Handler();
		 if(startingtime==0)
		 {
			 createTime();
		 }



	 }


	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{


		Log.v(TAG,"On Create View")    ;
		if (savedInstanceState != null) {
			// Do your oncreate stuff because there is no bundle
			Log.v(TAG,"savedInstanceState not null")    ;
			return null;
		}

		if (container == null)
		{
			Log.v(TAG,"Container Null")    ;
			return null;

		}
//		setRetainInstance(true);
		return (LinearLayout) inflater.inflate(R.layout.fragment_timer, container, false);
	}

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		if (savedInstanceState != null) {
			// Do your oncreate stuff because there is no bundle
			return ;
		}
		Log.v(TAG,"On  View Created")    ;
		super.onViewCreated(page, savedInstanceState);

		start_timer_button = (Button) page.findViewById(R.id.start_timer_button);
	    clock = (TextView)page.findViewById(R.id.timer);

//		timer_setup_text = (TextView) page.findViewById(R.id.timer_setup_text);
//		start_indicator = (TextView) page.findViewById(R.id.start_indicator);
//		current_time = (TextView) page.findViewById(R.id.current_time);
//		snoozed = (TextView) page.findViewById(R.id.snoozed);
//		ended = (TextView) page.findViewById(R.id.ended);
//
//		timer_setup_button = (Button) page.findViewById(R.id.timer_setup_button);
//		start_timer_button = (Button) page.findViewById(R.id.start_timer_button);
		snooze_button = (Button) page.findViewById(R.id.snooze_button);
//
		end_button = (Button) page.findViewById(R.id.stop_button);
//
//		timer_setup_button.setOnClickListener(timer_setup_click);
		start_timer_button.setOnClickListener(start_timer_click);
//		snooze_button.setOnClickListener(snoozeDialogue());
		end_button.setOnClickListener(end_click);

		setupComplete(startIndicatorBool);
		snooze(snoozeBool);
		timerStarted(timerStartedBool);

		if(snoozing)
		{
			if(snoozeDialog!=null && snoozeDialog.isShowing())
				snoozeDialog.dismiss();
			snoozeDialogue();
		}
	}


	View.OnClickListener end_click = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			snoozing = false;
			Intent intent = new Intent();
			intent.setAction("com.techventus.timefly.updatetimersettings");
			intent.putExtra(TimerService.BundleKey.STOP,true);
			getActivity().sendBroadcast(intent);
			if(snoozeDialog!=null && snoozeDialog.isShowing())
				snoozeDialog.dismiss();
		}
	};

	View.OnClickListener snoozeTimeClick(final long snoozetime)
	{

		View.OnClickListener snooze_click = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				snoozing = false;
				Intent intent = new Intent();
				intent.setAction("com.techventus.timefly.updatetimersettings");
				intent.putExtra(TimerService.BundleKey.SNOOZE,snoozetime);
				getActivity().sendBroadcast(intent);

				if(snoozeDialog!=null && snoozeDialog.isShowing())
					snoozeDialog.dismiss();
			}
		};
		return snooze_click;
	}

	 View.OnClickListener start_timer_click = new View.OnClickListener()
	 {
		 @Override
		 public void onClick(View view)
		 {
			 if(!startIndicatorBool)
			    createTime();
			 else
			 {


				 Intent intent = new Intent();
				 intent.setAction("com.techventus.timefly.updatetimersettings");
				 intent.putExtra(TimerService.BundleKey.START,true);
				 getActivity().sendBroadcast(intent);
			 }
		 }
	 };




	public void timerSetup()
	{
		Intent intent = new Intent();
		intent.setAction("com.techventus.timefly.updatetimersettings");
		intent.putExtra(TimerService.BundleKey.NEW_TIME,startingtime);
		getActivity().sendBroadcast(intent);
	}


	public void setupComplete()
	{
		startIndicatorBool = true;
		setupComplete(startIndicatorBool);
	}

	public void setupComplete(boolean bool)
	{
		if(bool)
		{
//			start_indicator.setVisibility(View.VISIBLE);
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
		if(bool)
		{
			start_timer_button.setVisibility(View.GONE);
			end_button.setVisibility(View.VISIBLE);
//			.setVisibility(View.VISIBLE);
//			current_time.setText("STARTED Current Time");
		}

	}

	public void alarmActive()
	{
		alarmBool = true;
		alarmActive(alarmBool);
	}

	public void alarmActive(boolean bool)
	{
		if(bool)
		{
//			snooze_button.setVisibility(View.VISIBLE);
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
		if(bool)
		{
//			snoozed.setVisibility(View.VISIBLE);
			end_button.setVisibility(View.VISIBLE);
		}
	}


	public void updateTime(long time)
	{
//		current_time.setText(""+time);
		int mins = (int) (time / 60000);
		int secs = (int) ((time % 60000) / 1000);

		clock.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));

	}

	public void ended(long time)
	{
//		ended.setVisibility(View.VISIBLE);
//		ended.setText(time+"");
	}

//	public void activateAlarm()
//	{
//
//	}


	   Handler h;

	Runnable r =   new Runnable()
	{
		@Override
		public void run()
		{
			 if(clock.getVisibility()==View.VISIBLE)
			 {
				 clock.setVisibility(View.INVISIBLE);
			 }
			else
			 {
				 clock.setVisibility(View.VISIBLE);
			 }
		}
	};

	     long startingtime = 0;

	Dialog snoozeDialog;

	private void snoozeDialogue()
	{
		snoozeDialog = new Dialog(getActivity());
		snoozeDialog.setContentView(R.layout.dialog_snooze);
		snoozeDialog.setTitle(getResources().getString(R.string.snooze));

		Button end = (Button)snoozeDialog.findViewById(R.id.end);
		Button five_mins = (Button)snoozeDialog.findViewById(R.id.five_mins);
		Button ten_mins = (Button)snoozeDialog.findViewById(R.id.ten_mins);
		Button half_hour = (Button)snoozeDialog.findViewById(R.id.half_hour);

		end.setOnClickListener(end_click);
		five_mins.setOnClickListener(snoozeTimeClick(5*60*1000L));
		ten_mins.setOnClickListener(snoozeTimeClick(10*60*1000L));
		half_hour.setOnClickListener(snoozeTimeClick(30*60*1000L));
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
							Intent i = new Intent() ;

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

//
//<intent-filter>
//<action android:name="com.techventus.timefly.updatetimervisual" />
//e=".TimerService" >
//<intent-filter>
//<action android:name="com.techventus.timefly.updatetimersettings" />