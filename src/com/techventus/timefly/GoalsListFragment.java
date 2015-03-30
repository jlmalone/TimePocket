package com.techventus.timefly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.techventus.timefly.R;

/**
 * @author Victoria Hansen
 */
public class GoalsListFragment extends SherlockFragment
{


	private final static String TAG = "GoalsListFragment";

	final List<String> l = new ArrayList<String>();
	final Map<String, Integer> mp = new TreeMap<String, Integer>();
	//	String[] values ;
	StableArrayAdapter adapter;


	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		setHasOptionsMenu(true);
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


		return (RelativeLayout) inflater.inflate(R.layout.fragment_goals, container, false);
	}


	void refreshList()
	{
		l.clear();

		mp.clear();
		DatabaseHelper db = new DatabaseHelper(getActivity());
		String[] fields = {DatabaseHelper.field_goals_goals_text, DatabaseHelper.field_goals_id_integer};

		Cursor c = db.getReadableDatabase().query(db.GOALS_TABLE_NAME, fields, null, null, null, null, null);

		while (c.moveToNext())
		{
			l.add(c.getString(0));
			mp.put(c.getString(0), c.getInt(1));
			Log.v(TAG, "SHOW GOAL" + c.getString(0));
		}
		c.close();
		db.close();

	}


	//TODO figure out why this is here and why it is never called.

	private void updateAdapter()
	{
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

		refreshList();


		adapter = new StableArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, l);


		final ListView listview = (ListView) page.findViewById(R.id.listview);

		listview.setAdapter(adapter);


		listview.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				final String goalname = l.get(arg2);
				final Dialog dialog = new Dialog(getActivity());
				LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ListView v = (ListView) li.inflate(R.layout.listview_dialog, null, false);

				String[] items = {"Rename", "Delete", "Share"};

				final ArrayAdapter<String> choicesadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);

				v.setAdapter(choicesadapter);


				v.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
					{
						// TODO Auto-generated method stub
						if (arg2 == 0)
						{
							final Dialog renameDialog = new Dialog(getActivity());
							renameDialog.setTitle("Rename");
							LayoutInflater renameInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							LinearLayout rename_layout = (LinearLayout) renameInflater.inflate(R.layout.dialog_rename, null, false);
							renameDialog.setContentView(rename_layout);

							dialog.dismiss();
							renameDialog.show();
							final EditText rename_input = (EditText) rename_layout.findViewById(R.id.rename_input);
							Button rename_button = (Button) rename_layout.findViewById(R.id.rename_button);
							rename_button.setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
								{
									String renametext = rename_input.getText().toString();
									if (renametext.length() > 3)
									{
										DatabaseHelper db = new DatabaseHelper(getActivity());
										ContentValues cvGoalsTable = new ContentValues();
										cvGoalsTable.put(DatabaseHelper.field_goals_goals_text, renametext);
										String whereClause = DatabaseHelper.field_goals_goals_text + " = ?";
										String[] whereArgs = new String[]{goalname};
										db.getWritableDatabase().update(DatabaseHelper.GOALS_TABLE_NAME, cvGoalsTable, whereClause, whereArgs);
										ContentValues cvPracticeTable = new ContentValues();
										cvPracticeTable.put(DatabaseHelper.field_practice_goals_name_text, renametext);
										db.getWritableDatabase().update(DatabaseHelper.PRACTICE_TABLE_NAME, cvPracticeTable,
												DatabaseHelper.field_practice_goals_name_text + " = ?", new String[]{goalname});
										db.close();
										renameDialog.dismiss();
										dialog.dismiss();
										refreshList();
									}
									else
									{
										Toast.makeText(getActivity(), "Name Too Short", Toast.LENGTH_SHORT).show();
									}

								}

							});

						}
						if (arg2 == 1)
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
							alertDialogBuilder.setTitle("Delete " + goalname + ": Are You Sure?").setPositiveButton("Delete",
									new android.content.DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dg, int which)
										{
											// TODO Auto-generated method stub
											DatabaseHelper db = new DatabaseHelper(getActivity());
											db.getWritableDatabase().delete(DatabaseHelper.GOALS_TABLE_NAME,
													"" + DatabaseHelper.field_goals_goals_text + " = ?", new String[]{goalname});


											db.getWritableDatabase().delete(DatabaseHelper.PRACTICE_TABLE_NAME,
													"" + DatabaseHelper.field_practice_goals_name_text + " = ?", new String[]{goalname});
											db.close();
											dg.dismiss();
											dialog.dismiss();
											//									l.clear();

											refreshList();

											promptAddGoalIfEmpty();
											adapter.updateData(l);

										}
									}
							).setNegativeButton("No", new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int id)
								{
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});

							AlertDialog alertDialog = alertDialogBuilder.create();

							// show it
							alertDialog.show();
						}

					}
				});

				dialog.setContentView(v);
				dialog.setTitle(l.get(arg2));
				dialog.show();
				return false;
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				String stringVal = l.get(position);
				int goalId = mp.get(stringVal);
				Intent i = new Intent(getActivity(), PerformingHabbit.class);
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_ID, goalId);
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_NAME, stringVal);
				getActivity().startActivityForResult(i, 0);
			}
		});


		SharedPreferences prefs = getActivity().getPreferences(getActivity().MODE_PRIVATE);
		boolean firstTime = prefs.getBoolean("firsttime", true);
		if (firstTime)
		{
			populateAboutDialog();
			mAboutDialog.show();

			SharedPreferences.Editor editor = getActivity().getPreferences(getActivity().MODE_PRIVATE).edit();
			editor.putBoolean("firsttime", false);

			editor.commit();

		}
		else
		{
			promptAddGoalIfEmpty();
		}

	}


	private void promptAddGoalIfEmpty()
	{
		if (l.size() == 0)
		{
			createGoal();
		}
	}


	Dialog mAboutDialog;
	IntroAdapter mIntroAdaper;

	ViewPager myPager;

	private void populateAboutDialog()
	{

		mAboutDialog = new Dialog(getActivity());
		mIntroAdaper = new IntroAdapter(getActivity(), 5);
		mIntroAdaper.setDialog(mAboutDialog);
		myPager = new ViewPager(getActivity());
		mIntroAdaper.setViewPager(myPager);
		mAboutDialog.setTitle(getResources().getString(R.string.about));

		myPager.setAdapter(mIntroAdaper);
		mAboutDialog.setContentView(myPager);


		//		mAboutDialog.setContentView(R.layout.about_layout);
		myPager.setCurrentItem(0);

	}


	private class StableArrayAdapter extends ArrayAdapter<String>
	{

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects)
		{
			super(context, textViewResourceId, objects);

		}

		public void updateData(List<String> objects)
		{
			mIdMap.clear();
			for (int i = 0; i < objects.size(); ++i)
			{
				mIdMap.put(objects.get(i), i);
			}

			this.notifyDataSetChanged();
		}

		@Override
		public long getItemId(int position)
		{
			String item = getItem(position);
			if (mIdMap != null && mIdMap.size() > 0)

			{
				return mIdMap.get(item);
			}
			else
			{
				return 0;
			}
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.plus_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_add_goal:
				createGoal();
				return true;
		}
		return false;
	}


	@Override
	public void onResume()
	{
		super.onResume();
	}


	private void createGoal()
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.view_create_goal);
		dialog.setTitle(getResources().getString(R.string.add_goal));


		final EditText goal_input = (EditText) dialog.findViewById(R.id.goal_input);
		// set the custom dialog components - text, image and button
		//		TextView text = (TextView) dialog.findViewById(R.id.text);
		//		text.setText("Android custom dialog example!");
		//		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		//		image.setImageResource(R.drawable.ic_launcher);

		Button dialogButton = (Button) dialog.findViewById(R.id.enter_goal_button);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String input = goal_input.getText().toString();
				if (input.length() > 1)
				{
					try
					{
						DatabaseHelper db = new DatabaseHelper(getActivity());
						db.getWritableDatabase().execSQL(
								"INSERT INTO " + DatabaseHelper.GOALS_TABLE_NAME + " (" + db.field_goals_goals_text + ") VALUES ('" + input + "');");

						db.close();
					}
					catch (SQLiteConstraintException e)
					{
						Toast.makeText(getActivity(), "Goal Name Already Taken", Toast.LENGTH_LONG).show();
					}

					refreshList();
					adapter.updateData(l);
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}

