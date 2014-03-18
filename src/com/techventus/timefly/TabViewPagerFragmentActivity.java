package com.techventus.timefly;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import com.techventus.timefly.R;


/**
 * The <code>TabsViewPagerFragmentActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 *
 * @author mwho
 */
public class TabViewPagerFragmentActivity extends SherlockFragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener
{

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabViewPagerFragmentActivity.TabInfo>();
	private PagerAdapter mPagerAdapter;

	/**
	 * @author mwho
	 *         Maintains extrinsic info of a tab's construct
	 */
	private class TabInfo
	{
		private String tag;
		private Class<?> clss;
		private Bundle args;
		private SherlockFragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle args)
		{
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	/**
	 * A simple factory that returns dummy views to the Tabhost
	 *
	 * @author mwho
	 */
	class TabFactory implements TabContentFactory
	{

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context)
		{
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 *
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag)
		{
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		inflater.inflate(R.menu.debug, menu);
		return true;
	}

	Dialog mAboutDialog;
	IntroAdapter mIntroAdaper;

	ViewPager myPager;

	private void populateAboutDialog()
	{

		mAboutDialog = new Dialog(this);
		mIntroAdaper = new IntroAdapter(this, 5);
		mIntroAdaper.setDialog(mAboutDialog);
		myPager = new ViewPager(this);
		mIntroAdaper.setViewPager(myPager);
		mAboutDialog.setTitle(getResources().getString(R.string.about));

		myPager.setAdapter(mIntroAdaper);
		mAboutDialog.setContentView(myPager);

		myPager.setCurrentItem(0);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.about:
				if (mAboutDialog == null)
				{
					populateAboutDialog();
				}
				myPager.setCurrentItem(0);
				mAboutDialog.show();


				return true;
			case R.id.contact:
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("text/plain");
				String addy = "mytimeflyexperience@gmail.com";
				sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{addy});
				sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
				sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Please tell us about your experiences:");

				this.startActivity(Intent.createChooser(sendIntent, "Send via which Application?"));
		}
		return false;
	}

	//TODO move to utils
	private void logDB()
	{
		DatabaseHelper db = new DatabaseHelper(this);
		SQLiteDatabase sqldb = db.getReadableDatabase();
		String[] goals_cols = {DatabaseHelper.field_goals_id_integer, DatabaseHelper.field_goals_goals_text};
		Cursor c = sqldb.query(DatabaseHelper.GOALS_TABLE_NAME, goals_cols, null, null, null, null, null);

		int i = 0;
		Log.v("DB", "TABLE " + DatabaseHelper.GOALS_TABLE_NAME + " ");
		while (c.moveToNext())
		{
			int goals_id = c.getInt(c.getColumnIndex(DatabaseHelper.field_goals_id_integer));

			String goals_goals = c.getString(c.getColumnIndex(DatabaseHelper.field_goals_goals_text));

			Log.v("DB", "ROW : " + i + "; " + DatabaseHelper.field_goals_id_integer + " : " + goals_id + " ; " + DatabaseHelper.field_goals_goals_text + " :" +
					" " +
					goals_goals);

			i++;
		}
		c.close();

		String[] practice_cols =
				{DatabaseHelper.field_practice_id_integer, DatabaseHelper.field_practice_goals_name_text, DatabaseHelper.field_practice_goals_id_integer,
						DatabaseHelper.field_practice_notes_text, DatabaseHelper.field_practice_date_long, DatabaseHelper.field_practice_seconds_integer};

		Cursor d = sqldb.query(DatabaseHelper.PRACTICE_TABLE_NAME, practice_cols, null, null, null, null, null);

		int j = 0;
		while (d.moveToNext())
		{
			int practice_id = d.getInt(d.getColumnIndex(DatabaseHelper.field_practice_id_integer));

			int practice_goals_id = d.getInt(d.getColumnIndex(DatabaseHelper.field_practice_goals_id_integer));

			String practice_goals_name = d.getString(d.getColumnIndex(DatabaseHelper.field_practice_goals_name_text));

			String practice_notes_text = d.getString(d.getColumnIndex(DatabaseHelper.field_practice_notes_text));

			String practice_secs = d.getString(d.getColumnIndex(DatabaseHelper.field_practice_seconds_integer));

			String practice_date = d.getString(d.getColumnIndex(DatabaseHelper.field_practice_date_long));

			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String formattedDate = f.format(new Date(Long.parseLong(practice_date)));

			Log.v("DB", "ROW : " + j + "; " + DatabaseHelper.field_practice_id_integer + " : " + practice_id + " ; " +
					DatabaseHelper.field_practice_goals_id_integer + " : " + practice_goals_id + " ; " + DatabaseHelper.field_practice_goals_name_text + " :" +
					" " +
					practice_goals_name + " ; " + DatabaseHelper.field_practice_notes_text + " : " + practice_notes_text + " ; " +
					DatabaseHelper.field_practice_date_long + " : " + practice_date + " " + formattedDate + " ; " +
					DatabaseHelper.field_practice_seconds_integer + " : " + practice_secs);

			j++;
		}
	}


	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Inflate the layout
		setContentView(R.layout.tabs_viewpager_layout);
		// Initialise the TabHost
		this.initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null)
		{
			try
			{
				mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// Intialise ViewPager
		this.intialiseViewPager();

	}

	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
		super.onSaveInstanceState(outState);
	}

	/**
	 * Initialise ViewPager
	 */
	private void intialiseViewPager()
	{

		List<Fragment> fragments = new Vector<Fragment>();
		//       fragments.add(SherlockFragment.instantiate(this, TimerFragment.class.getName()));
		//       fragments.add(Fragment.instantiate(this, Tab2Fragment.class.getName()));
		//      fragments.add(Fragment.instantiate(this, CalendarFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, GoalsListFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, LogListFragment.class.getName()));
		this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

		//
		this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
		if(this.mViewPager==null)
			Log.v("HELP","NULL VIEW PAGER");
		else if (this.mPagerAdapter==null)
			Log.v("HELP","NULL PAGER ADAPTER");

		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args)
	{
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;

		TabViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab4").setIndicator(getResources().getString(R.string.goals)),
				(tabInfo = new TabInfo("Tab4", GoalsListFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		TabViewPagerFragmentActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Log"),
				(tabInfo = new TabInfo("Tab3", LogListFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		// Default to first tab
		//this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * Add Tab content to the Tabhost
	 *
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param tabInfo
	 */
	private static void AddTab(TabViewPagerFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo)
	{
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag)
	{
		//TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();
		this.mViewPager.setCurrentItem(pos);
	}

	/* (non-Javadoc)
		* @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
		*/
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
		* @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
		*/
	@Override
	public void onPageSelected(int position)
	{
		// TODO Auto-generated method stub
		this.mTabHost.setCurrentTab(position);
	}

	/* (non-Javadoc)
		* @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
		*/
	@Override
	public void onPageScrollStateChanged(int state)
	{
		// TODO Auto-generated method stub

	}

}