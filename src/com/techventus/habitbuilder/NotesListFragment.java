package com.techventus.habitbuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.actionbarsherlock.app.SherlockFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.techventus.habitbuilder.GoalProgressActivity.BundleKey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
 
/**
 * @author Victoria Hansen 
 *
 */
public class NotesListFragment extends SherlockFragment  {
	
	
	public static final String TAG = NotesListFragment.class.getSimpleName();
	final List<Practice> notesList = new ArrayList<Practice>();
//	final Map<String, Practice> mp = new HashMap<String, Practice>();
//	final List<String> l = new ArrayList<String>(); 
	String[] values ;
//	StableArrayAdapter adapter ;
	NotesAdapter adapter;
	
	Button startTimerButton;
	
	int thisGoalId;
	
	int goal_id;
	String goal_name;
	
	SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
	
	SimpleDateFormat dayFormatter = new SimpleDateFormat("EEE");
	

	NoteSelectListener mCallback;

	// Container Activity must implement this interface
	public interface NoteSelectListener {
		public void onNoteSelect(int practice_id, int goal_id, String goal_name,  String note,
				int secs, long startDate);
		public void onNoteSelect(Practice p);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			Log.v(TAG, "ATTACHING ACTIVITY");
			mCallback = (NoteSelectListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement TimerListener");
		}
	}

	
	
	

    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
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
        
        goal_id=getArguments().getInt(BundleKey.EXTRA_GOAL_ID,-1);
        goal_name=getArguments().getString(BundleKey.EXTRA_GOAL_NAME);

        return (RelativeLayout)inflater.inflate(R.layout.fragment_notes, container, false);
    }
    
    
    void refreshList()
    {
    	
    	notesList.clear();
//    	l.clear(); 
        DatabaseHelper db = new DatabaseHelper(getActivity());
//        String[] fields =   {DatabaseHelper.field_practice_notes_text };
        
        String whereclause = " "+DatabaseHelper.field_practice_goals_id_integer+" = ? ";//'"+goal_id+"' ";
        
        String[] selection_args = {goal_id+""};
        
//        Cursor c  =db.getReadableDatabase().query(db.PRACTICE_TABLE_NAME,fields, whereclause, selection_args, null, null, orderby);
       List<Practice> practice_list = db.getPracticeList(whereclause, selection_args);
       notesList.addAll(practice_list);
       
//       //REFACTOR
//       for(Practice p:practice_list)
//       {
//    	   notesList.
//    	   l.add(p.getNote());
//    	   mp.put(p.getNote(), p);
//       }
       
        db.close();
        
        GraphViewData[] gvAr = new GraphViewData[practice_list.size()];
        
        int j=0;
        for(int i=practice_list.size()-1;i>=0; i--)
        {
        	gvAr[i]=new GraphViewData(practice_list.get(j).getDate(), practice_list.get(j).getSecs());
        	j++;
        }
        
     // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(gvAr);
        
      

        GraphView graphView = new LineGraphView(this.getActivity() , "Activity" );
        graphView.setBackgroundColor(getResources().getColor(R.color.black));
        graphView.setBottom(0);
//        graphView.s
        graphView.addSeries(exampleSeries); // data
//        graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today", "tomorrow"});
//        graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
        
        
        chart_holder.removeAllViews();
        chart_holder.addView(graphView);

        
        
        
        
        
        
        
        
        
        
        
        
        
    }
    
    
    LinearLayout chart_holder ;
    
    @Override
    public void onViewCreated(View page,Bundle savedInstanceState)
    {
    	super.onViewCreated(page, savedInstanceState)	;
    	
    	//get goals from database
    	
        values= new String[] {  };

        
   	 chart_holder = (LinearLayout)page. findViewById(R.id.performance_chart);

        refreshList();
        
        adapter = new NotesAdapter(this.getActivity(), R.layout.listitem_note, notesList);

//        adapter= new StableArrayAdapter(this.getActivity(),
//                android.R.layout.simple_list_item_1, l);
        
    	final ListView listview = (ListView) page.findViewById(R.id.listview);
    	startTimerButton = (Button)page.findViewById(R.id.startPracticeButton);
    	
    	startTimerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NotesListFragment.this.getActivity(), PerformingHabbit.class);
				intent.putExtra(PerformingHabbit.BundleKey.EXTRA_GOAL_ID, goal_id);
				intent.putExtra(PerformingHabbit.BundleKey.EXTRA_GOAL_NAME, goal_name);
				startActivity(intent);
				getActivity().finish();
			}});

    	listview.setAdapter(adapter);
    	
    	listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getActivity(), "CLICK ITEM "+arg2, Toast.LENGTH_LONG).show();
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
    
    
    
    
    
    public class NotesAdapter extends ArrayAdapter<Practice>
    {
    	
    	Context context;
    	int layoutResourceId;  
    	List<Practice> practiceList;

		public NotesAdapter(Context context, int layoutResourceId,
				List<Practice> practiceList) {
			
			super(context, layoutResourceId, practiceList);
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.practiceList = practiceList;
			// TODO Auto-generated constructor stub
		}
		
		
		

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {

//    	    View v = convertView;
    	    NoteListItemViewHolder viewHolder ;

    	    if (convertView == null) {

    	        LayoutInflater vi;
    	        vi = LayoutInflater.from(getContext());
    	        convertView = vi.inflate(R.layout.listitem_note, null);
    	        viewHolder = new NoteListItemViewHolder();
    	        viewHolder.day_performed = (TextView)convertView.findViewById(R.id.day_performed);
    	        viewHolder.date_performed = (TextView)convertView.findViewById(R.id.date_performed);
    	        viewHolder.seconds_performed = (TextView) convertView.findViewById(R.id.seconds_performed);
    	        viewHolder.note = (TextView) convertView.findViewById(R.id.note);
    	        convertView.setTag(viewHolder);
    	    }else
    	    {
    	    	viewHolder = (NoteListItemViewHolder)convertView.getTag();
    	    }

    	    Practice p = practiceList.get(position);

    	    if (p != null) {


//    	        TextView tt3 = (TextView) v.findViewById(R.id.description);
    	        	viewHolder.seconds_performed.setText(DurationFormatter.getFormattedTime(p.getSecs()));

    	        	viewHolder.note.setText(p.getNote());
    	        	
    	        	viewHolder.date_performed.setText(formatter.format(new Date(p.getDate())));
    	        	viewHolder.day_performed.setText((dayFormatter.format(new Date(p.getDate()))).toUpperCase());

    	    }

    	    return convertView;

    	}
    	
    }
    
    
    
    
//    
//
//    private class StableArrayAdapter extends ArrayAdapter<String> 
//    {
//
//      HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//      public StableArrayAdapter(Context context, int textViewResourceId,
//          List<String> objects) 
//      {
//        super(context, textViewResourceId, objects);
//
//      }
//      
//      public void updateData(List<String > objects)
//      {
//    	  mIdMap.clear();
//          for (int i = 0; i < objects.size(); ++i) 
//          {
//              mIdMap.put(objects.get(i), i);
//          }
//    	  this.notifyDataSetChanged();
//      }
//
//      @Override
//      public long getItemId(int position) 
//      {
//        String item = getItem(position);
//        if(mIdMap!=null && mIdMap.size()>0)
//        	return mIdMap.get(item);
//        else return 0;
//      }
//
//      @Override
//      public boolean hasStableIds() 
//      {
//        return true;
//      }
//
//    }

}
