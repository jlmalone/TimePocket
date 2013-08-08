package com.techventus.habitbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;
 
 
/**
 * @author Victoria Hansen 
 *
 */
public class LogListFragment extends SherlockFragment {
	
	public static final String TAG = LogListFragment.class.getSimpleName();
	
//	final List<String> l = new ArrayList<String>(); 
	final List<LogSummary> logSummaryList = new ArrayList<LogSummary>();
//	final Map<String, Integer> goal_id_map= new HashMap<String, Integer>();
	String[] values ;
//	 StableArrayAdapter adapter ;
	
	ListAdapter adapter;
	
	 
	 @Override
	 public void onResume()
	 {
		 super.onResume();
		 refreshList();
		 adapter.notifyDataSetChanged();
	 }
	 

	 

    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        
  
        return (RelativeLayout)inflater.inflate(R.layout.fragment_goals, container, false);
    }
    
    
    private void refreshList()
    {
//    	l.clear();
    	logSummaryList.clear();
//    	goal_id_map.clear();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        String[] fields =   {DatabaseHelper.field_goals_goals_text,DatabaseHelper.field_goals_id_integer};
        
//      Cursor c  =db.getReadableDatabase().query(db.GOALS_TABLE_NAME,fields, null, null, null, null, null);
      
        String selectString = "SELECT SUM("+DatabaseHelper.field_practice_seconds_integer+") AS TIMESUM, "+DatabaseHelper.field_practice_goals_name_text+", "+DatabaseHelper.field_practice_goals_id_integer+", MAX("+DatabaseHelper.field_practice_date_long+") AS RECENTDATE, COUNT(*) AS COUNT FROM "+DatabaseHelper.PRACTICE_TABLE_NAME+" GROUP BY "+DatabaseHelper.field_practice_goals_id_integer+" ORDER BY "+DatabaseHelper.field_practice_goals_id_integer+";";
      
      	Cursor c= db.getReadableDatabase().rawQuery(selectString, null);
      	

        while(c.moveToNext())
        {
        	
        	int seconds = c.getInt(0);
        	String goal_name = c.getString(1);
        	int goal_id = c.getInt(2);
        	long recentDate = c.getLong(3);
        	int count = c.getInt(4);
        	LogSummary ls = new LogSummary(goal_id, goal_name,seconds, recentDate,count);
        	logSummaryList.add(ls);
        	
        }

        c .close();
        db.close();
        
        
    }
    
    @Override
    public void onViewCreated(View page,Bundle savedInstanceState)
    {
    	super.onViewCreated(page, savedInstanceState)	;
    	
    	//get goals from database
    	
        values= new String[] { "WRITE SOFTWARE" };

//        refreshList();
        
        adapter = new ListAdapter(this.getActivity(), R.layout.listitem_log_summary,logSummaryList);

//        adapter= new StableArrayAdapter(this.getActivity(),
//                android.R.layout.simple_list_item_1, l);
 
    	final ListView listview = (ListView) page.findViewById(R.id.listview);

    	listview.setAdapter(adapter);
    	
    	listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getActivity(), "CLICK ITEM "+arg2, Toast.LENGTH_LONG).show();
				
				Intent i = new Intent(getActivity(), GoalProgressActivity.class);
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_NAME, adapter.getItem(arg2).getGoal());
				i.putExtra(GoalProgressActivity.BundleKey.EXTRA_GOAL_ID, adapter.getItem(arg2).getGoal_id());
				
//				Log.v(TAG, adapter.getItem(arg2)+"  "+goal_id_map.get(adapter.getItem(arg2)));
				getActivity().startActivityForResult(i, 0, null);
			}});
    }
    
    

    
    public class ListAdapter extends ArrayAdapter<LogSummary> {

    	public ListAdapter(Context context, int textViewResourceId) {
    	    super(context, textViewResourceId);
    	    // TODO Auto-generated constructor stub
    	}

    	private List<LogSummary> items;

    	public ListAdapter(Context context, int resource, List<LogSummary> items) {

    	    super(context, resource, items);

    	    this.items = items;

    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

    	    View v = convertView;

    	    if (v == null) {

    	        LayoutInflater vi;
    	        vi = LayoutInflater.from(getContext());
    	        v = vi.inflate(R.layout.listitem_log_summary, null);
    	       
    	    }

    	    LogSummary p = items.get(position);

    	    if (p != null) {

    	        TextView goal_name = (TextView) v.findViewById(R.id.goal_name);
    	        TextView seconds_performed = (TextView) v.findViewById(R.id.seconds_performed);
    	        TextView count = (TextView) v.findViewById(R.id.count);
    	        
    	        count.setText("Performance Count: "+p.getCount());

    	        if (seconds_performed != null) {
    	        	seconds_performed.setText(DurationFormatter.getFormattedTime(p.getTotalSeconds()));
    	        }
    	        if (goal_name != null) {

    	        	goal_name.setText(p.getGoal());
    	        }

    	    }

    	    return v;

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

