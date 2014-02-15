/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techventus.timefly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.techventus.timefly.R;


public class CalendarFragment extends SherlockFragment
{


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
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		page = (CalendarPickerView) inflater.inflate(R.layout.calendar_dialog, container, false);

		Calendar today = Calendar.getInstance();
		final ArrayList<Date> dates = new ArrayList<Date>();
		for (int i = 0; i < 5; i++)
		{
			today.add(Calendar.DAY_OF_MONTH, 3);
			dates.add(today.getTime());
		}
		((CalendarPickerView) page).init(new Date(), nextYear.getTime()) //
				.inMode(SelectionMode.MULTIPLE) //
				.withSelectedDates(dates);

		setRetainInstance(true);
		return page;
	}

	CalendarPickerView page;

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

	}

}
