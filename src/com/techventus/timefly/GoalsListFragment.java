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

/**
 * @author Joseph Malone
 */
public class GoalsListFragment extends SherlockFragment
{
	private final static String[] GOAL_CONTROL_OPTIONS = {"Rename", "Delete", "Share"};
	private final static String TAG = "GoalsListFragment";
	private final static String KEY_FIRST_TIME = "firsttime";
	private ListView mListView;
	private final List<String> mGoalsNameList = new ArrayList<String>();
	private final Map<String, Integer> mGoalNameIdMap = new TreeMap<String, Integer>();
	//	String[] values ;
	private StableArrayAdapter adapter;
	private Dialog mAboutDialog;
	private IntroAdapter mIntroAdaper;
	private ViewPager myPager;
	private String mGoalNameSelected;
	private Dialog mGoalSelectedOptionDialog;
	private RelativeLayout mNoDatastate;


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

	//TODO move to background thread
	private void refreshList()
	{
		mGoalsNameList.clear();

		mGoalNameIdMap.clear();
		DatabaseHelper db = new DatabaseHelper(getActivity());
		String[] fields = {DatabaseHelper.field_goals_goals_text, DatabaseHelper.field_goals_id_integer};

		Cursor c = db.getReadableDatabase().query(db.GOALS_TABLE_NAME, fields, null, null, null, null, null);

		while (c.moveToNext())
		{
			mGoalsNameList.add(c.getString(0));
			mGoalNameIdMap.put(c.getString(0), c.getInt(1));
			Log.v(TAG, "SHOW GOAL" + c.getString(0));
		}
		c.close();
		db.close();


		setupNoDataState();
		adapter.notifyDataSetChanged();
	}

	private OnItemClickListener mGoalOptionClick = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if (position == 0)
			{
				final Dialog renameDialog = new Dialog(getActivity());
				renameDialog.setTitle("Rename");
				LayoutInflater renameInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout rename_layout = (LinearLayout) renameInflater.inflate(R.layout.dialog_rename, null, false);
				renameDialog.setContentView(rename_layout);

				mGoalSelectedOptionDialog.dismiss();
				renameDialog.show();
				final EditText rename_input = (EditText) rename_layout.findViewById(R.id.rename_input);
				Button rename_button = (Button) rename_layout.findViewById(R.id.rename_button);
				rename_button.setOnClickListener(new OnClickListener()
				{
					   //TODO FACTOR OUT
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
							String[] whereArgs = new String[]{mGoalNameSelected};
							db.getWritableDatabase().update(DatabaseHelper.GOALS_TABLE_NAME, cvGoalsTable, whereClause, whereArgs);
							ContentValues cvPracticeTable = new ContentValues();
							cvPracticeTable.put(DatabaseHelper.field_practice_goals_name_text, renametext);
							db.getWritableDatabase().update(DatabaseHelper.PRACTICE_TABLE_NAME, cvPracticeTable,
									DatabaseHelper.field_practice_goals_name_text + " = ?", new String[]{mGoalNameSelected});
							db.close();
							renameDialog.dismiss();
							mGoalSelectedOptionDialog.dismiss();
							refreshList();
						}
						else
						{
							Toast.makeText(getActivity(), "Name Too Short", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			if (position == 1)
			{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				alertDialogBuilder.setTitle("Delete " + mGoalNameSelected + ": Are You Sure?").setPositiveButton("Delete",
						new android.content.DialogInterface.OnClickListener()
						{
							     //TODO FACTOR OUT
							@Override
							public void onClick(DialogInterface dg, int which)
							{
								DatabaseHelper db = new DatabaseHelper(getActivity());
								db.getWritableDatabase().delete(DatabaseHelper.GOALS_TABLE_NAME, DatabaseHelper.field_goals_goals_text + " = ?", new String[]{mGoalNameSelected});

								db.getWritableDatabase().delete(DatabaseHelper.PRACTICE_TABLE_NAME,DatabaseHelper.field_practice_goals_name_text + " = ?", new String[]{mGoalNameSelected});
								db.close();
								dg.dismiss();
								mGoalSelectedOptionDialog.dismiss();

								refreshList();

								promptAddGoalIfEmpty();
								adapter.updateData(mGoalsNameList);
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
	};



	private OnItemLongClickListener mGoalLongClick = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			mGoalNameSelected = mGoalsNameList.get(position);
			mGoalSelectedOptionDialog = new Dialog(getActivity());
			LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ListView v = (ListView) li.inflate(R.layout.listview_dialog, null, false);

			v.setAdapter( new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, GOAL_CONTROL_OPTIONS));
			v.setOnItemClickListener(mGoalOptionClick);

			mGoalSelectedOptionDialog.setContentView(v);
			mGoalSelectedOptionDialog.setTitle(mGoalsNameList.get(position));
			mGoalSelectedOptionDialog.show();
			return false;
		}
	};

	private OnItemClickListener mGoalClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if(mGoalSelectedOptionDialog!=null && mGoalSelectedOptionDialog.isShowing())
			{
				return;
			}
			String stringVal = mGoalsNameList.get(position);
			int goalId = mGoalNameIdMap.get(stringVal);
			Intent i = new Intent(getActivity(), PerformingHabbit.class);
			i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_ID, goalId);
			i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_NAME, stringVal);
			getActivity().startActivityForResult(i, 0);
		}
	};

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

		adapter = new StableArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, mGoalsNameList);
		mListView = (ListView) page.findViewById(R.id.listview);
		mListView.setAdapter(adapter);

		mListView.setOnItemLongClickListener(mGoalLongClick);
		mListView.setOnItemClickListener(mGoalClickListener	);

		mNoDatastate = (RelativeLayout)page.findViewById(R.id.no_data_state) ;

		SharedPreferences prefs = getActivity().getPreferences(getActivity().MODE_PRIVATE);
		firstTime = prefs.getBoolean(KEY_FIRST_TIME, true);
		Log.v(TAG, "firstTimeValue "+firstTime);
		if (firstTime)
		{
			populateAboutDialog();
			mAboutDialog.show();
			SharedPreferences.Editor editor = getActivity().getPreferences(getActivity().MODE_PRIVATE).edit();
			editor.putBoolean(KEY_FIRST_TIME, false);

			editor.commit();
		}

	}

	boolean firstTime = true;

	private void promptAddGoalIfEmpty()
	{
		if (!firstTime && mGoalsNameList.size() == 0)
		{
			createGoal();
		}
	}

	public void populateAboutDialog()
	{
		mAboutDialog = new Dialog(getActivity());
		mIntroAdaper = new IntroAdapter(getActivity(), 5);
		mIntroAdaper.setDialog(mAboutDialog);
		myPager = new ViewPager(getActivity());
		mIntroAdaper.setViewPager(myPager);
//		mAboutDialog.setTitle(getResources().getString(R.string.about));

		myPager.setAdapter(mIntroAdaper);
		mAboutDialog.setContentView(myPager);
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
		refreshList();
		promptAddGoalIfEmpty();
	}

	private Dialog mCreateGoalDialog;


	private void createGoal()
	{
		if(mCreateGoalDialog!=null && mCreateGoalDialog.isShowing())
		{
			mCreateGoalDialog.dismiss();
		}

		mCreateGoalDialog = new Dialog(getActivity());
		mCreateGoalDialog.setContentView(R.layout.view_create_goal);
		mCreateGoalDialog.setTitle(getResources().getString(R.string.add_goal));


		final EditText goal_input = (EditText) mCreateGoalDialog.findViewById(R.id.goal_input);
		// set the custom dialog components - text, image and button
		//		TextView text = (TextView) dialog.findViewById(R.id.text);
		//		text.setText("Android custom dialog example!");
		//		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		//		image.setImageResource(R.drawable.ic_launcher);

		Button dialogButton = (Button) mCreateGoalDialog.findViewById(R.id.enter_goal_button);
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
						                 ContentValues contentValues = new ContentValues(
						)        ;
						contentValues.put(db.field_goals_goals_text,input);

						//TODO TRY TO USE ABOVE CONTENT VALUES INSTEAD OF BELOW INSERT
						db.getWritableDatabase().insert(DatabaseHelper.GOALS_TABLE_NAME, null,contentValues);
//						.execSQL("INSERT INTO " + DatabaseHelper.GOALS_TABLE_NAME + " (" + db.field_goals_goals_text + ") VALUES ('" + input + "');");

						db.close();
					}
					catch (SQLiteConstraintException e)
					{
						Toast.makeText(getActivity(), "Goal Name Already Taken", Toast.LENGTH_LONG).show();
					}

					refreshList();
					adapter.updateData(mGoalsNameList);
				}
				mCreateGoalDialog.dismiss();
			}
		});
		mCreateGoalDialog.show();
	}



	private void setupNoDataState()
	{
		Log.v(TAG, "SET UP NO DATA STATE "+mGoalsNameList.isEmpty());
		if(mGoalsNameList.isEmpty())
		{
			mListView.setVisibility(View.GONE);
			mNoDatastate.setVisibility(View.VISIBLE);
			mNoDatastate.findViewById(R.id.create_goal_text)  .setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					createGoal();
				}
			});
		}
		else
		{
			mListView.setVisibility(View.VISIBLE);
			mNoDatastate.setVisibility(View.GONE);
		}
	}
}