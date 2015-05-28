package com.techventus.timefly;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Build;

import com.actionbarsherlock.app.SherlockFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.techventus.timefly.model.Practice;

/**
 * @author Joseph Malone
 *
 *
 * I do not like this feature one bit, but it was a request.
 */
public class NotesStreamFragment extends SherlockFragment
{
	public static final String TAG = NotesListFragment.class.getSimpleName();
	final List<Practice> notesList = new ArrayList<Practice>();
	String[] values;
	NotesStreamAdapter adapter;



//	int goal_id;
//	String goal_name;

	SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");

	SimpleDateFormat dayFormatter = new SimpleDateFormat("EEE");


	NoteStreamSelectListener mCallback;

	// Container Activity must implement this interface
	public interface NoteStreamSelectListener
	{
		public void onNoteSelect(int practice_id, int goal_id, String goal_name, String note, int secs, long startDate);

		public void onNoteSelect(Practice p);
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
			mCallback = (NoteStreamSelectListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement NoteSelectListener");
		}
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
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist.  The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed.  Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}

//		goal_id = getArguments().getInt(GoalProgressActivity.BundleKey.EXTRA_GOAL_ID, -1);
//		goal_name = getArguments().getString(GoalProgressActivity.BundleKey.EXTRA_GOAL_NAME);

		return (RelativeLayout) inflater.inflate(R.layout.notestream, container, false);
	}

	List<Practice> practice_list;



	void refreshList()
	{
		notesList.clear();
		DatabaseHelper db = new DatabaseHelper(getActivity());

//		String whereclause = " " + DatabaseHelper.field_practice_goals_id_integer + " = ? ";//'"+goal_id+"' ";

//		String[] selection_args = {goal_id + ""};

		practice_list = db.getPracticeList(null, null);
		notesList.addAll(practice_list);

		for (Practice practice : practice_list)
		{
			Log.v(TAG, "PRACTICE OUTPUT " + practice.getNote() + " " + practice.getDate() + " " + practice.toString());
		}

		db.close();

		GraphViewData[] gvAr = new GraphViewData[practice_list.size()];

		int j = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		String[] labels = new String[practice_list.size()];
		for (int i = practice_list.size() - 1; i >= 0; i--)
		{
			gvAr[i] = new GraphViewData(practice_list.get(j).getDate(), practice_list.get(j).getSecs()/60);
			labels[i] =  sdf.format(new Date(practice_list.get(j).getDate()));
			j++;
		}

		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(gvAr);

		GraphView graphView = new LineGraphView(this.getActivity(), "");
		graphView.setBackgroundColor(getResources().getColor(R.color.black));
		if (Build.VERSION.SDK_INT >= 11)
		{
			graphView.setBottom(0);
		}
		graphView.addSeries(exampleSeries); // data
		graphView.setHorizontalLabels(labels);

	}



	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

		//get goals from database

		values = new String[]{};



		refreshList();

		adapter = new NotesStreamAdapter(this.getActivity(), R.layout.listitem_note, notesList);

		final ListView listview = (ListView) page.findViewById(R.id.listview);



		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Practice p = notesList.get(arg2);//mp.get(l.get(arg2));
				mCallback.onNoteSelect(p);
			}
		});
	}

	private class NoteListItemViewHolder
	{
		public TextView day_performed;
		public TextView date_performed;
		public TextView note;
		public TextView seconds_performed;
	}

	public class NotesStreamAdapter extends ArrayAdapter<Practice>
	{

		Context context;
		int layoutResourceId;
		List<Practice> practiceList;

		public NotesStreamAdapter(Context context, int layoutResourceId, List<Practice> practiceList)
		{

			super(context, layoutResourceId, practiceList);
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.practiceList = practiceList;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			 NoteListItemViewHolder viewHolder;

			if (convertView == null)
			{

				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				convertView = vi.inflate(R.layout.listitem_note, null);
				viewHolder = new NoteListItemViewHolder();
				viewHolder.day_performed = (TextView) convertView.findViewById(R.id.day_performed);
				viewHolder.date_performed = (TextView) convertView.findViewById(R.id.date_performed);
				viewHolder.seconds_performed = (TextView) convertView.findViewById(R.id.seconds_performed);
				viewHolder.note = (TextView) convertView.findViewById(R.id.note);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (NoteListItemViewHolder) convertView.getTag();
			}

			Practice p = practiceList.get(position);

			if (p != null)
			{
				viewHolder.seconds_performed.setText(DurationFormatter.getFormattedTime(p.getSecs()));

				viewHolder.note.setText(p.getNote());

				viewHolder.date_performed.setText(formatter.format(new Date(p.getDate())));
				viewHolder.day_performed.setText((dayFormatter.format(new Date(p.getDate()))).toUpperCase());

			}
			return convertView;
		}

	}

}

