package com.techventus.timefly;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.techventus.timefly.R;

/**
 * refactor this as an intent service
 *
 * @author Victoria Hansen
 */
public class DetailsFragment extends SherlockFragment
{

	public static final String TAG = DetailsFragment.class.getSimpleName();

	private static final SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");

	private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("EEE");

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
		return (LinearLayout) inflater.inflate(R.layout.fragment_details, container, false);
	}


	View page;

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);
		this.page = page;
		fillData();
	}


	void fillData()
	{
		if (practice != null)
		{
			//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			((TextView) page.findViewById(R.id.notesText)).setText(practice.getNote());
			((TextView) page.findViewById(R.id.date)).setText(formatter.format(new Date(practice.getDate())));
			//			((TextView)page.findViewById(R.id.goal)).setText(practice.getGoalName());
			((TextView) page.findViewById(R.id.time_spent)).setText(DurationFormatter.getFormattedTime(practice.getSecs()) + "");

		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	Practice practice;

	public void setValues(Practice p)
	{
		practice = p;
		if (page != null)
		{
			((TextView) page.findViewById(R.id.notesText)).setText(p.getNote());
		}
		// TODO Auto-generated method stub
		else
		{
			Log.v(TAG, "PAGE IS NULL");
		}

	}

}
