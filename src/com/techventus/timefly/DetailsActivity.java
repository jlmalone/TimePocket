package com.techventus.timefly;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.techventus.timefly.R;

public class DetailsActivity extends SherlockActivity
{
	

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle("Details");
    }
}
