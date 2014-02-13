package com.techventus.timefly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.techventus.timefly.R;

public class GoalProgressActivity extends SherlockFragmentActivity implements NotesListFragment.NoteSelectListener{
	
	public static final String TAG = PerformingHabbit.class.getSimpleName();
//	RelativeLayout mRoot ;
	
	class BundleKey
	{
		public static final String EXTRA_GOAL_ID = "EXTRA_GOAL_ID";
		public static final String EXTRA_GOAL_NAME = "EXTRA_GOAL_NAME";
	}

	int goal_id;
	String goal_name;
	
	NotesListFragment notesListFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        goal_id=intent.getIntExtra(BundleKey.EXTRA_GOAL_ID,-1);
        goal_name=intent.getStringExtra(BundleKey.EXTRA_GOAL_NAME);
        
        setTitle(goal_name);

        Log.v(TAG, "CALL ONCREATE");

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.root) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            notesListFragment = new NotesListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            notesListFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root, notesListFragment,"notes").commitAllowingStateLoss();
        }  
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onNoteSelect(int practice_id, int goal_id, String goal_name,  String note,
			int secs, long startDate) {
		try
		{
			Fragment f = getSupportFragmentManager().findFragmentByTag("notes");
			if(f!=null)
				getSupportFragmentManager().beginTransaction()
		        .remove(f).commit();		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try{

			DetailsFragment detailFragment = new DetailsFragment();
			
			getSupportFragmentManager().beginTransaction()
				.add(R.id.root, detailFragment,"details").commitAllowingStateLoss();
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onNoteSelect(Practice p) {
		try
		{
			Fragment f = getSupportFragmentManager().findFragmentByTag("notes");
			if(f!=null)
				getSupportFragmentManager().beginTransaction()
		        .remove(f).commit();		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try{

			DetailsFragment detailFragment = new DetailsFragment();
			
			detailFragment.setValues(p);
			
			getSupportFragmentManager().beginTransaction()
				.add(R.id.root, detailFragment,"details").commitAllowingStateLoss();
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    
    
    
    



	

}