

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
//import com.squareup.timessquare.CalendarPickerView;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.techventus.timefly.R;


public class CalendarFragment extends SherlockFragment {
	

//	        public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";
//	        CalendarView mView = null;
//	        TextView mHit;
//	        Handler mHandler = new Handler();
	        
	        
	    	/**
	    	 * (non-Javadoc)
	    	 * 
	    	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	    	 *      android.view.ViewGroup, android.os.Bundle)
	    	 */
	    	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    			Bundle savedInstanceState) {
	    		if (container == null) {

	    			return null;

	    		}
	    		final Calendar nextYear = Calendar.getInstance();
	    		nextYear.add(Calendar.YEAR, 1);
	    		
	    		 page = (CalendarPickerView) inflater.inflate(R.layout.calendar_dialog,
	    				container, false);
	                
	              Calendar today = Calendar.getInstance();
	              final ArrayList<Date> dates = new ArrayList<Date>();
	              for (int i = 0; i < 5; i++) {
	                today.add(Calendar.DAY_OF_MONTH, 3);
	                dates.add(today.getTime());
	              }
			    ((CalendarPickerView)page).init(new Date(), nextYear.getTime()) //
			     .inMode(SelectionMode.MULTIPLE) //
	                  .withSelectedDates(dates);



//			    page.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener()
//			    {
//				    @Override
//				    public void onDateSelected(Date date)
//				    {
//					    page.clearChoices();
//					    ((CalendarPickerView)page).init(new Date(), nextYear.getTime()) //
//							    .inMode(SelectionMode.MULTIPLE) //
//							    .withSelectedDates(dates);
//				    }
//			    });








//	              dialogView.setE
//	              dialogView.setEnabled(false);
//	                dialogView.init(new Date(), nextYear.getTime());
//	                new AlertDialog.Builder(SampleTimesSquareActivity.this)
//	                    .setTitle("I'm a dialog!")
//	                    .setView(dialogView)
//	                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
//	                      @Override public void onClick(DialogInterface dialogInterface, int i) {
//	                        dialogInterface.dismiss();
//	                      }
//	                    })
//	                    .create()
//	                    .show();
//	              }
//	            });
	    		
	    		
	    		
	    		
	    		
	    		
	    		
	    		
//	    		CalendarPickerView.FluentInitializer fi = new CalendarPickerView.FluentInitializer();
//	    		fi.inMode(CalendarPickerView.SelectionMode.MULTIPLE);
//	    		fi.
	    		
//	    		calendar.init(today, nextYear.getTime(),true)
//	    		    .withSelectedDate(today);
//	    		
//	    		
//	    		
//	    		calendar.setEnabled(false);
	    		
	    		
	    		setRetainInstance(true);
	    		return page;
	    	}

	    	CalendarPickerView page;
	    	
	    	@Override
	    	public void onViewCreated(View page, Bundle savedInstanceState) {
	    		super.onViewCreated(page, savedInstanceState);
	    		
//	    		this.page = page;
	    		
//	    		final LayoutInflater  inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		
//	    		LinearLayout linear = (LinearLayout)page.findViewById(R.id.layout);
//good	           mView = new CalendarView(getActivity());
//	          LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//	          page.addView(mView);
	          
	          
	    		
//good	           linear.addView(mView,lp);
//	    		Button b = (Button)inflater.inflate(R.layout.buttons,
//	                    null);
//	    		
//	    		Inflater inflater = new LayoutInflater();
	    		
	    		
//	    		mView = (CalendarView)page.findViewById(R.id.calendar);
//	    		mView.setOnCellTouchListener(this);
//	    		  if(getActivity().getIntent().getAction().equals(Intent.ACTION_PICK))
//		                page.findViewById(R.id.hint).setVisibility(View.INVISIBLE);
	    	}
	    	
	        
//
//	        public void onTouch(Cell cell) {
//	                Intent intent = getActivity().getIntent();
//	                String action = intent.getAction();
//	                if(action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT)) {
//	                        int year  = mView.getYear();
//	                        int month = mView.getMonth();
//	                        int day   = cell.getDayOfMonth();
//	                        
//	                        // FIX issue 6: make some correction on month and year
//	                        if(cell instanceof CalendarView.GrayCell) {
//	                                // oops, not pick current month...                              
//	                                if (day < 15) {
//	                                        // pick one beginning day? then a next month day
//	                                        if(month==11)
//	                                        {
//	                                                month = 0;
//	                                                year++;
//	                                        } else {
//	                                                month++;
//	                                        }
//	                                        
//	                                } else {
//	                                        // otherwise, previous month
//	                                        if(month==0) {
//	                                                month = 11;
//	                                                year--;
//	                                        } else {
//	                                                month--;
//	                                        }
//	                                }
//	                        }
//	                        
//	                        Intent ret = new Intent();
//	                        ret.putExtra("year", year);
//	                        ret.putExtra("month", month);
//	                        ret.putExtra("day", day);
////	                        this.setResult(getActivity().RESULT_OK, ret);
////	                        finish();
//	                        return;
//	                }
//	                int day = cell.getDayOfMonth();
//	                if(mView.firstDay(day))
//	                        mView.previousMonth();
//	                else if(mView.lastDay(day))
//	                        mView.nextMonth();
//	                else
//	                        return;
//
//	                mHandler.post(new Runnable() {
//	                        public void run() {
//	                                Toast.makeText(getActivity(), DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " "+mView.getYear(), Toast.LENGTH_SHORT).show();
//	                        }
//	                });
//	        }
//
//	    
	

}
