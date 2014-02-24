package com.techventus.timefly;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.techventus.timefly.R;


/**
 * @author Victoria Hansen
 */
public class LogListFragment extends SherlockFragment
{

	public static final String TAG = LogListFragment.class.getSimpleName();

	final List<LogSummary> logSummaryList = new ArrayList<LogSummary>();
	String[] values;

	ListAdapter adapter;


	@Override
	public void onResume()
	{
		super.onResume();
		refreshList();
		adapter.notifyDataSetChanged();
	}


	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		if (container == null)
		{
			return null;
		}


		return (RelativeLayout) inflater.inflate(R.layout.fragment_log, container, false);
	}


	private void refreshList()
	{
		logSummaryList.clear();
		DatabaseHelper db = new DatabaseHelper(getActivity());

		String selectString =
				"SELECT SUM(" + DatabaseHelper.field_practice_seconds_integer + ") AS TIMESUM, " + DatabaseHelper.field_practice_goals_name_text + ", " +
						DatabaseHelper.field_practice_goals_id_integer + ", MAX(" + DatabaseHelper.field_practice_date_long +
						") AS RECENTDATE, COUNT(*) AS COUNT FROM " + DatabaseHelper.PRACTICE_TABLE_NAME + " GROUP BY " +
						DatabaseHelper.field_practice_goals_id_integer + " ORDER BY " + DatabaseHelper.field_practice_goals_id_integer + ";";

		Cursor c = db.getReadableDatabase().rawQuery(selectString, null);


		while (c.moveToNext())
		{

			int seconds = c.getInt(0);
			String goal_name = c.getString(1);
			int goal_id = c.getInt(2);
			long recentDate = c.getLong(3);
			int count = c.getInt(4);
			LogSummary ls = new LogSummary(goal_id, goal_name, seconds, recentDate, count);
			logSummaryList.add(ls);

		}

		c.close();
		db.close();

	}


	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

		//get goals from database

		values = new String[]{"WRITE SOFTWARE"};


		adapter = new ListAdapter(this.getActivity(), R.layout.listitem_log_summary, logSummaryList);


		final GridView gridview = (GridView) page.findViewById(R.id.gridview);

		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener()
		{

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{

				Intent i = new Intent(getActivity(), GoalProgressActivity.class);
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_NAME, adapter.getItem(arg2).getGoal());
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_ID, adapter.getItem(arg2).getGoal_id());

				getActivity().startActivityForResult(i, 0, null);
			}
		});
	}


	public class ListAdapter extends ArrayAdapter<LogSummary>
	{

		public ListAdapter(Context context, int textViewResourceId)
		{
			super(context, textViewResourceId);
		}

		private List<LogSummary> items;

		public ListAdapter(Context context, int resource, List<LogSummary> items)
		{

			super(context, resource, items);

			this.items = items;

		}

		public class ViewHolder
		{
			public TextView goal_name;
			public TextView seconds_performed;
			public TextView count;
			public ProgressBar progress_bar;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			ViewHolder vh;
			if (convertView == null)
			{
				vh = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.listitem_log_summary, null);

				vh.count = (TextView) convertView.findViewById(R.id.count);
				vh.goal_name = (TextView) convertView.findViewById(R.id.goal_name);
				vh.seconds_performed = (TextView) convertView.findViewById(R.id.seconds_performed);
				vh.progress_bar = (ProgressBar) convertView.findViewById(R.id.progressBar1);

				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}

			LogSummary p = items.get(position);

			if (p != null)
			{


				vh.count.setText("Performance Count: " + p.getCount());

				//    	        if (seconds_performed != null) {
				vh.seconds_performed.setText(DurationFormatter.getFormattedTime(p.getTotalSeconds()));
				//    	        }
				//    	        if (goal_name != null) {

				vh.goal_name.setText(p.getGoal());
				//    	        }

				Drawable draw = getResources().getDrawable(R.drawable.custom_progressbar);
				// set the drawable as progress drawavle

				vh.progress_bar.setProgressDrawable(draw);
				vh.progress_bar.setProgress((p.getCount() * 10));

			}

			return convertView;

		}
	}

	//    private class StableArrayAdapter extends ArrayAdapter<String> {
	//
	//      HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	//
	//      public StableArrayAdapter(Context context, int textViewResourceId,
	//          List<String> objects) {
	//        super(context, textViewResourceId, objects);
	//
	//      }
	//
	//
	//
	//      @Override
	//      public long getItemId(int position) {
	//        String item = getItem(position);
	//        if(mIdMap!=null && mIdMap.size()>0)
	//
	//        	return mIdMap.get(item);
	//        else return -1;
	//      }
	//
	//      @Override
	//      public boolean hasStableIds() {
	//        return true;
	//      }
	//
	//    }

}

