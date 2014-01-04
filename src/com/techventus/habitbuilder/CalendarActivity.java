package com.techventus.habitbuilder;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joseph on 01.01.14.
 */
public class CalendarActivity   extends SherlockFragmentActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		         long[] datesAr = null;
		    if(getIntent().getExtras().containsKey(PerformingHabbit.BundleKey.EXTRA_DATES))
		    {
			    datesAr = getIntent().getExtras().getLongArray(PerformingHabbit.BundleKey.EXTRA_DATES) ;
		    }

		      if(datesAr==null)
		      {
			      //TODO SHOW TOAST
			      finish();
			      return;
		      }


		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);


		final CalendarPickerView page = (CalendarPickerView) getLayoutInflater().inflate(R.layout.calendar_dialog,null);

		setContentView(page);

		Calendar today = Calendar.getInstance();
		final ArrayList<Date> dates = new ArrayList<Date>();
		long mindate = datesAr[0];
		long maxdate=datesAr[0];
		for (int i = 0; i < datesAr.length; i++)
		{
			if(datesAr[i]<mindate)
			{
				mindate =  datesAr[i];
			}
			if(datesAr[i]>maxdate)
			{
				maxdate =  datesAr[i];
			}
			dates.add(new Date(datesAr[i]));
		}
		((CalendarPickerView)page).init(new Date(mindate-24L*1000*3600), new Date(maxdate+24L*1000*3600)) //
				.inMode(CalendarPickerView.SelectionMode.MULTIPLE) //
				.withSelectedDates(dates);


		page.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter()
		{
			@Override public boolean isDateSelectable(Date date)
			{

				return false;
			}
		});

		page.setOnInvalidDateSelectedListener(null);
	}
}
