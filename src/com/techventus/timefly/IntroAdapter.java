package com.techventus.timefly;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class IntroAdapter extends PagerAdapter
{
	int size;
	Activity act;
	View layout;
	Dialog mDialog;

	private ViewPager mViewPager;

	public void setViewPager(ViewPager pager)
	{
		mViewPager = pager;
	}

	public void setDialog(Dialog d)
	{
		mDialog = d;
	}


	public IntroAdapter(Activity activity, int noofsize)
	{
		size = 6;
		act = activity;
	}

	@Override
	public int getCount()
	{
		return size;
	}

	@Override
	public Object instantiateItem(View container, int position)
	{
		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.pages, null);
		Button b = (Button) layout.findViewById(R.id.next);
		b.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				int i = mViewPager.getCurrentItem();
				if (i < 5)
				{
					mViewPager.setCurrentItem(i + 1);
				}
				else
				{
					mDialog.dismiss();
				}

			}
		});

		if (position == 0)
		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_1, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);

		}
		else if (position == 1)
		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_2, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);

		}
		else if (position == 2)
		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_3, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);
		}
		else if (position == 3)
		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_4, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);
		}
		else if (position == 4)

		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_5, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);
		}
		else if (position == 5)

		{
			LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.about_6, null);
			((ScrollView) layout.findViewById(R.id.about_container)).removeAllViews();
			((ScrollView) layout.findViewById(R.id.about_container)).addView(linear);
		}
		((ViewPager) container).addView(layout, 0);
		return layout;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return arg0 == ((View) arg1);
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

}